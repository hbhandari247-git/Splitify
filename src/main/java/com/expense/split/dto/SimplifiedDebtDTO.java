package com.expense.split.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimplifiedDebtDTO {
  private String fromUser;
  private String fromUserName;
  private String toUser;
  private String toUserName;
  private double amount;
}
