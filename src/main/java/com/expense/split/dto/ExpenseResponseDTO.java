package com.expense.split.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseResponseDTO {
  private String expenseId;
  private String description;
  private double totalAmount;
  private String paidByUserId;
  private String paidByUserName;
  private String groupName;
  private String groupId;
}
