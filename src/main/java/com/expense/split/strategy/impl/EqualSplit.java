package com.expense.split.strategy.impl;

import com.expense.split.models.Split;
import com.expense.split.strategy.SplitStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EqualSplit implements SplitStrategy {

  @Override
  public List<Split> calculateSplit(double totalAmount, List<String> userIds, List<Double> values) {
    List<Split> splits = new ArrayList<>();
    double share = totalAmount / userIds.size();

    for (String userId : userIds) {
      splits.add(Split.builder().userId(userId).amount(share).build());
    }
    return splits;
  }
}
