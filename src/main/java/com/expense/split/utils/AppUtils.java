package com.expense.split.utils;

import com.expense.split.dto.ExpenseResponseDTO;
import com.expense.split.dto.GroupInfoResponse;
import com.expense.split.models.Expense;
import com.expense.split.models.Group;
import com.expense.split.models.User;
import java.util.HashSet;
import java.util.Set;

public class AppUtils {

  public static ExpenseResponseDTO createExpenseDTOFromExpense(
      Expense expense, boolean isIndividualExpense) {
    return new ExpenseResponseDTO(
        expense.getExpenseId(),
        expense.getDescription(),
        expense.getTotalAmount(),
        expense.getPaidBy().getUserId(),
        expense.getPaidBy().getName(),
        null != expense.getGroup() ? expense.getGroup().getName() : null,
        null != expense.getGroup() ? expense.getGroup().getGroupId() : null);
  }

  public static GroupInfoResponse createGroupInfoResponseFromGroup(Group group) {
    GroupInfoResponse groupInfoResponse = new GroupInfoResponse();
    groupInfoResponse.setGroupId(group.getGroupId());
    groupInfoResponse.setGroupName(group.getName());

    if (null != group.getMembers() && !group.getMembers().isEmpty()) {
      Set<String> members = new HashSet<>();
      for (User user : group.getMembers()) {
        members.add(user.getName() + " ( " + user.getUserId() + " ) ");
      }
      groupInfoResponse.setGroupMembers(members);
    }
    return groupInfoResponse;
  }
}
