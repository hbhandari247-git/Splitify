package com.expense.split.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String expenseId;

  private String description;

  private double totalAmount;

  @ManyToOne
  @JoinColumn(name = "paid_by_user_id")
  @JsonIgnore
  private User paidBy;

  @ManyToOne
  @JoinColumn(name = "group_id")
  @JsonIgnore
  private Group group;

  @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Split> splits;
}
