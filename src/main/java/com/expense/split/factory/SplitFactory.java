package com.expense.split.factory;

import com.expense.split.enums.SplitType;
import com.expense.split.strategy.SplitStrategy;
import com.expense.split.strategy.impl.EqualSplit;
import com.expense.split.strategy.impl.ExactSplit;
import com.expense.split.strategy.impl.PercentageSplit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SplitFactory {

  private final EqualSplit equalSplit;
  private final ExactSplit exactSplit;
  private final PercentageSplit percentageSplit;

  public SplitStrategy getStrategy(SplitType type) {
    return switch (type) {
      case EQUAL -> equalSplit;
      case EXACT -> exactSplit;
      case PERCENTAGE -> percentageSplit;
    };
  }
}
