package com.expense.split.service;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.dto.UserBalanceResponse;
import com.expense.split.models.User;
import com.expense.split.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User createUser(String name, String email) {
    User user = User.builder().name(name).email(email).userId(UUID.randomUUID().toString()).build();
    return userRepository.save(user);
  }

  public Optional<User> getUserByUserId(String userId) {
    return userRepository.findByUserId(userId);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Map<String, List<UserBalanceResponse>> getUserBalancesSeparated(String userId) {
    Map<String, List<UserBalanceResponse>> result = new HashMap<>();

    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException(SplitwiseConstants.EXCEPTION_USER_ONE));

    Map<String, User> allUsers =
        userRepository.findAll().stream().collect(Collectors.toMap(User::getUserId, u -> u));

    result.put("group", extractBalances(userId, user.getGroupBalances(), allUsers, user));
    result.put("individual", extractBalances(userId, user.getIndividualBalances(), allUsers, user));

    return result;
  }

  private List<UserBalanceResponse> extractBalances(
      String userId, Map<String, Double> balances, Map<String, User> allUsers, User debtor) {

    List<UserBalanceResponse> responses = new ArrayList<>();

    for (Map.Entry<String, Double> entry : balances.entrySet()) {
      String creditorId = entry.getKey();
      double amount = entry.getValue();

      if (amount > SplitwiseConstants.EPSILON) {
        User creditor = allUsers.get(creditorId);
        responses.add(
            new UserBalanceResponse(
                debtor.getUserId(),
                debtor.getName(),
                creditor.getUserId(),
                creditor.getName(),
                debtor.getName() + " owes " + creditor.getName(),
                amount));
      }
    }

    return responses;
  }
}
