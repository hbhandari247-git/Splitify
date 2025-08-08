package com.expense.split.strategy.impl;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.models.Split;
import com.expense.split.strategy.SplitStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExactSplit implements SplitStrategy {

  @Override
  public List<Split> calculateSplit(double totalAmount, List<String> userIds, List<Double> values) {
    if (values.size() != userIds.size()) {
      throw new IllegalArgumentException("Split values must match number of users");
    }

    double sum = values.stream().mapToDouble(Double::doubleValue).sum();
    if (Math.abs(sum - totalAmount) > SplitwiseConstants.EPSILON) {
      throw new IllegalArgumentException("Split values must add up to total amount");
    }

    List<Split> splits = new ArrayList<>();
    for (int i = 0; i < userIds.size(); i++) {
      splits.add(Split.builder().userId(userIds.get(i)).amount(values.get(i)).build());
    }
    return splits;
  }
}
