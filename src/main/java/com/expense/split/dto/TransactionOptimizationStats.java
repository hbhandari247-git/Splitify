package com.expense.split.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionOptimizationStats {
  private int originalTransactionCount;
  private int simplifiedTransactionCount;
  private int transactionsSaved;
}
