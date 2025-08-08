package com.expense.split.controller;

import com.expense.split.dto.AddExpenseRequest;
import com.expense.split.dto.AddIndividualExpenseRequest;
import com.expense.split.dto.ExpenseResponseDTO;
import com.expense.split.models.Expense;
import com.expense.split.service.ExpenseService;
import com.expense.split.utils.AppUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

  private final ExpenseService expenseService;

  @PostMapping("/group")
  public ResponseEntity<ExpenseResponseDTO> addGroupExpense(
      @RequestBody @Valid AddExpenseRequest request) {
    Expense expense =
        expenseService.addExpenseToGroup(
            request.getGroupId(),
            request.getDescription(),
            request.getAmount(),
            request.getPaidByUserId(),
            request.getInvolvedUserIds(),
            request.getSplitType(),
            request.getSplitValues() != null ? request.getSplitValues() : List.of());
    ExpenseResponseDTO response = AppUtils.createExpenseDTOFromExpense(expense, false);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/individual")
  public ResponseEntity<ExpenseResponseDTO> addIndividualExpense(
      @RequestBody @Valid AddIndividualExpenseRequest request) {
    Expense expense = expenseService.addIndividualExpense(request);
    ExpenseResponseDTO response = AppUtils.createExpenseDTOFromExpense(expense, true);
    return ResponseEntity.ok(response);
  }
}
