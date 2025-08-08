package com.expense.split.service;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.dto.AddIndividualExpenseRequest;
import com.expense.split.enums.SplitType;
import com.expense.split.factory.SplitFactory;
import com.expense.split.models.*;
import com.expense.split.notification.NotificationService;
import com.expense.split.repository.*;
import com.expense.split.strategy.SplitStrategy;
import jakarta.transaction.Transactional;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final SplitRepository splitRepository;
  private final SplitFactory splitFactory;
  private final NotificationService notificationService;

  @Transactional
  public Expense addExpenseToGroup(
      String groupId,
      String description,
      double amount,
      String paidByUserId,
      List<String> involvedUserIds,
      SplitType splitType,
      List<Double> splitValues) {

    Group group =
        groupRepository
            .findByGroupId(groupId)
            .orElseThrow(() -> new RuntimeException(SplitwiseConstants.EXCEPTION_GROUP_ONE));

    User paidBy =
        userRepository
            .findByUserId(paidByUserId)
            .orElseThrow(() -> new RuntimeException(SplitwiseConstants.EXCEPTION_USER_TWO));

    for (String userId : involvedUserIds) {
      if (group.getMembers().stream().noneMatch(u -> u.getUserId().equals(userId))) {
        throw new RuntimeException(
            SplitwiseConstants.EXCEPTION_USER_ONE + userId + SplitwiseConstants.NOT_IN_GROUP);
      }
    }

    SplitStrategy strategy = splitFactory.getStrategy(splitType);
    List<Split> splits = strategy.calculateSplit(amount, involvedUserIds, splitValues);

    Expense expense =
        Expense.builder()
            .expenseId(UUID.randomUUID().toString())
            .description(description)
            .totalAmount(amount)
            .paidBy(paidBy)
            .group(group)
            .build();

    expense = expenseRepository.save(expense);

    for (Split split : splits) {
      split.setExpense(expense);
    }

    splitRepository.saveAll(splits);
    expense.setSplits(splits);

    for (Split split : splits) {
      String userId = split.getUserId();
      double share = split.getAmount();

      if (!userId.equals(paidByUserId)) {
        User debtor =
            userRepository
                .findByUserId(userId)
                .orElseThrow(
                    () ->
                        new RuntimeException(
                            SplitwiseConstants.USER_ONE + userId + SplitwiseConstants.NOT_FOUND));

        // Store balance from debtor → creditor
        Map<String, Double> balances = debtor.getGroupBalances();
        balances.put(paidByUserId, balances.getOrDefault(paidByUserId, 0.0) + share);

        userRepository.save(debtor);
      }
    }

    userRepository.save(paidBy);

    notificationService.notifyGroup(
        group,
        "New expense added: " + description + " of Rs " + amount + " by " + paidBy.getName());

    return expense;
  }

  @Transactional
  public Expense addIndividualExpense(AddIndividualExpenseRequest request) {
    User paidBy =
        userRepository
            .findByUserId(request.getPaidByUserId())
            .orElseThrow(() -> new RuntimeException(SplitwiseConstants.EXCEPTION_USER_TWO));

    User toUser =
        userRepository
            .findByUserId(request.getToUserId())
            .orElseThrow(() -> new RuntimeException(SplitwiseConstants.EXCEPTION_USER_THREE));

    SplitStrategy strategy = splitFactory.getStrategy(request.getSplitType());

    List<Split> splits =
        strategy.calculateSplit(
            request.getAmount(),
            List.of(paidBy.getUserId(), toUser.getUserId()),
            request.getSplitValues() != null ? request.getSplitValues() : List.of());

    Expense expense =
        Expense.builder()
            .expenseId(UUID.randomUUID().toString())
            .description(request.getDescription())
            .totalAmount(request.getAmount())
            .paidBy(paidBy)
            .group(null)
            .build();

    expense = expenseRepository.save(expense);

    for (Split split : splits) {
      split.setExpense(expense);
    }

    splitRepository.saveAll(splits);
    expense.setSplits(splits);

    for (Split split : splits) {
      String userId = split.getUserId();
      double share = split.getAmount();

      if (!userId.equals(paidBy.getUserId())) {
        User debtor =
            userRepository
                .findByUserId(userId)
                .orElseThrow(
                    () ->
                        new RuntimeException(
                            SplitwiseConstants.USER_ONE + userId + SplitwiseConstants.NOT_FOUND));

        // Store balance from debtor → creditor
        Map<String, Double> balances = debtor.getIndividualBalances();
        balances.put(paidBy.getUserId(), balances.getOrDefault(paidBy.getUserId(), 0.0) + share);

        userRepository.save(debtor);
      }
    }

    userRepository.save(paidBy);

    notificationService.notifyUser(
        paidBy, "You paid Rs " + request.getAmount() + " for " + toUser.getName());
    notificationService.notifyUser(
        toUser, paidBy.getName() + " added an expense on your behalf: Rs " + request.getAmount());

    return expense;
  }
}
