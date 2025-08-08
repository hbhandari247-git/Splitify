package com.expense.split.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Balance {
  String userId;
  String userName;
  double amount;
}
