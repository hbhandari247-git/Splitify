package com.expense.split.dto;

import com.expense.split.enums.SplitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class AddExpenseRequest {

  @NotBlank(message = "Group ID is required")
  private String groupId;

  @NotBlank(message = "Description is required")
  private String description;

  @Positive(message = "Amount must be positive")
  private double amount;

  @NotBlank(message = "PaidByUserId is required")
  private String paidByUserId;

  @NotEmpty(message = "Involved users must be provided")
  private List<@NotBlank String> involvedUserIds;

  @NotNull(message = "Split type must be specified")
  private SplitType splitType;

  private List<@Positive Double> splitValues;
}
