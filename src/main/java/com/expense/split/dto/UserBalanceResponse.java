package com.expense.split.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBalanceResponse {
  private String debtorId;
  private String debtorName;
  private String creditorId;
  private String creditorName;
  private String message;
  private double amount;
}
