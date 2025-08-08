package com.expense.split.service;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.dto.SimplifiedDebtDTO;
import com.expense.split.dto.TransactionOptimizationStats;
import com.expense.split.models.*;
import com.expense.split.pojos.Balance;
import com.expense.split.repository.DebtSimplificationLogRepository;
import com.expense.split.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DebtSimplificationService {

  private final UserRepository userRepository;
  private final DebtSimplificationLogRepository logRepository;

  public List<SimplifiedDebtDTO> getSimplifiedDebts(List<User> users) {
    Map<String, Double> netBalance = new HashMap<>();
    TreeMap<String, String> userIdToUserNameMap = new TreeMap<>();

    for (User user : users) {
      String userId = user.getUserId();
      userIdToUserNameMap.put(userId, user.getName());

      for (Map.Entry<String, Double> entry : user.getGroupBalances().entrySet()) {
        String creditorId = entry.getKey();
        double amount = entry.getValue();

        netBalance.put(userId, netBalance.getOrDefault(userId, 0.0) - amount);
        netBalance.put(creditorId, netBalance.getOrDefault(creditorId, 0.0) + amount);
      }
    }

    List<Balance> creditors = new ArrayList<>();
    List<Balance> debtors = new ArrayList<>();

    for (Map.Entry<String, Double> entry : netBalance.entrySet()) {
      String userId = entry.getKey();
      double amount = entry.getValue();

      if (amount > SplitwiseConstants.EPSILON)
        creditors.add(new Balance(userId, userIdToUserNameMap.get(userId), amount));
      else if (amount < -SplitwiseConstants.EPSILON)
        debtors.add(new Balance(userId, userIdToUserNameMap.get(userId), -amount));
    }

    creditors.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
    debtors.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));

    List<SimplifiedDebtDTO> result = new ArrayList<>();
    int i = 0, j = 0;

    while (i < debtors.size() && j < creditors.size()) {
      Balance debtor = debtors.get(i);
      Balance creditor = creditors.get(j);

      double settledAmount = Math.min(debtor.getAmount(), creditor.getAmount());

      result.add(
          new SimplifiedDebtDTO(
              debtor.getUserId(),
              debtor.getUserName(),
              creditor.getUserId(),
              creditor.getUserName(),
              settledAmount));

      debtor.setAmount(debtor.getAmount() - settledAmount);
      creditor.setAmount(creditor.getAmount() - settledAmount);

      if (debtor.getAmount() < SplitwiseConstants.EPSILON) i++;
      if (creditor.getAmount() < SplitwiseConstants.EPSILON) j++;
    }

    return result;
  }

  @Transactional
  public void applySimplifiedDebtsToDatabase(
      List<User> users, String groupId, String simplifiedByUserId) {
    List<SimplifiedDebtDTO> simplifiedDebts = getSimplifiedDebts(users);
    int originalTransactionCount = 0;

    for (User user : users) {
      originalTransactionCount += user.getGroupBalances().size();
      user.getGroupBalances().clear();
    }

    for (SimplifiedDebtDTO dto : simplifiedDebts) {
      User debtor =
          users.stream().filter(u -> u.getUserId().equals(dto.getFromUser())).findFirst().get();
      debtor.getGroupBalances().put(dto.getToUser(), dto.getAmount());
    }

    userRepository.saveAll(users);

    List<DebtEntry> entries =
        simplifiedDebts.stream()
            .map(d -> new DebtEntry(d.getFromUser(), d.getToUser(), d.getAmount()))
            .collect(Collectors.toList());

    DebtSimplificationLog log =
        DebtSimplificationLog.builder()
            .groupId(groupId)
            .simplifiedByUserId(simplifiedByUserId)
            .originalTransactionCount(originalTransactionCount)
            .simplifiedTransactionCount(entries.size())
            .transactionsSaved(originalTransactionCount - entries.size())
            .simplifiedAt(LocalDateTime.now())
            .simplifiedDebts(entries)
            .build();

    logRepository.save(log);
  }

  @Transactional
  public void rebuildGroupBalancesFromExpenses(Group group) {
    List<User> users = group.getMembers();
    Map<String, User> userMap =
        users.stream().collect(Collectors.toMap(User::getUserId, Function.identity()));

    users.forEach(user -> user.getGroupBalances().clear());

    for (Expense expense : group.getGroupExpenses()) {
      String paidByUserId = expense.getPaidBy().getUserId();
      for (Split split : expense.getSplits()) {
        String userId = split.getUserId();
        double share = split.getAmount();

        if (!userId.equals(paidByUserId)) {
          User debtor = userMap.get(userId);
          Map<String, Double> balances = debtor.getGroupBalances();
          balances.put(paidByUserId, balances.getOrDefault(paidByUserId, 0.0) + share);
        }
      }
    }

    userRepository.saveAll(users);
  }

  public TransactionOptimizationStats getTransactionSimplificationStats(List<User> users) {
    int original = 0;
    for (User user : users) {
      for (double val : user.getGroupBalances().values()) {
        if (val > SplitwiseConstants.EPSILON) original++;
      }
    }

    List<SimplifiedDebtDTO> simplified = getSimplifiedDebts(users);
    return new TransactionOptimizationStats(
        original, simplified.size(), original - simplified.size());
  }
}
