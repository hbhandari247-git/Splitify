package com.expense.split.service;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.dto.GroupBalanceResponse;
import com.expense.split.models.Group;
import com.expense.split.models.User;
import com.expense.split.repository.GroupRepository;
import com.expense.split.repository.UserRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public Group createGroup(String groupName) {
    Group group = Group.builder().name(groupName).groupId(UUID.randomUUID().toString()).build();

    return groupRepository.save(group);
  }

  public Optional<Group> getGroupByGroupId(String groupId) {
    return groupRepository.findByGroupId(groupId);
  }

  public Optional<Group> addUserToGroup(String groupId, String userId) {
    Optional<Group> groupOpt = groupRepository.findByGroupId(groupId);
    Optional<User> userOpt = userRepository.findByUserId(userId);

    if (groupOpt.isPresent() && userOpt.isPresent()) {
      Group group = groupOpt.get();
      User user = userOpt.get();

      if (!group.getMembers().contains(user)) {
        group.getMembers().add(user);
        groupRepository.save(group);
      }

      return Optional.of(group);
    }

    return Optional.empty();
  }

  public List<GroupBalanceResponse> getGroupBalances(String groupId) {
    Group group =
        groupRepository
            .findByGroupId(groupId)
            .orElseThrow(() -> new RuntimeException(SplitwiseConstants.EXCEPTION_GROUP_ONE));

    List<GroupBalanceResponse> responses = new ArrayList<>();
    List<User> members = group.getMembers();

    for (User debtor : members) {
      String debtorId = debtor.getUserId();
      for (Map.Entry<String, Double> entry : debtor.getGroupBalances().entrySet()) {
        String creditorId = entry.getKey();
        double amount = entry.getValue();

        if (amount > SplitwiseConstants.EPSILON) {
          User creditor =
              members.stream()
                  .filter(u -> u.getUserId().equals(creditorId))
                  .findFirst()
                  .orElse(null);
          if (creditor != null) {
            responses.add(
                new GroupBalanceResponse(
                    debtorId, debtor.getName(), creditorId, creditor.getName(), amount));
          }
        }
      }
    }

    return responses;
  }
}
