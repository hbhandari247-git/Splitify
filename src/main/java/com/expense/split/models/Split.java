package com.expense.split.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "splits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Split {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userId;

  private double amount;

  @ManyToOne
  @JoinColumn(name = "expense_id")
  private Expense expense;
}
