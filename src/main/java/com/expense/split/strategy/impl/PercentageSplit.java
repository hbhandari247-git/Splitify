package com.expense.split.strategy.impl;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.models.Split;
import com.expense.split.strategy.SplitStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PercentageSplit implements SplitStrategy {

  @Override
  public List<Split> calculateSplit(double totalAmount, List<String> userIds, List<Double> values) {
    if (values.size() != userIds.size()) {
      throw new IllegalArgumentException(SplitwiseConstants.EXCEPTION_PERCENTAGE_SPLIT_ONE);
    }

    double sum = values.stream().mapToDouble(Double::doubleValue).sum();
    if (Math.abs(sum - 100.0) > SplitwiseConstants.EPSILON) {
      throw new IllegalArgumentException(SplitwiseConstants.EXCEPTION_PERCENTAGE_SPLIT_TWO);
    }

    List<Split> splits = new ArrayList<>();
    for (int i = 0; i < userIds.size(); i++) {
      double amount = (totalAmount * values.get(i)) / 100.0;
      splits.add(Split.builder().userId(userIds.get(i)).amount(amount).build());
    }
    return splits;
  }
}
