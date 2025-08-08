package com.expense.split.strategy;

import com.expense.split.models.Split;
import java.util.List;

public interface SplitStrategy {
  List<Split> calculateSplit(double totalAmount, List<String> userIds, List<Double> values);
}
