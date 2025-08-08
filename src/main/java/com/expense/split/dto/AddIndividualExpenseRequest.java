package com.expense.split.dto;

import com.expense.split.enums.SplitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class AddIndividualExpenseRequest {

  @NotBlank private String description;

  @Positive private double amount;

  @NotBlank private String paidByUserId;

  @NotBlank private String toUserId;

  @NotNull private SplitType splitType;

  private List<@Positive Double> splitValues;
}
