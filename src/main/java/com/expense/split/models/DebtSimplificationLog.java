package com.expense.split.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "debt_simplification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtSimplificationLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String groupId;
  private String simplifiedByUserId;

  private int originalTransactionCount;
  private int simplifiedTransactionCount;
  private int transactionsSaved;

  private LocalDateTime simplifiedAt;

  @ElementCollection
  @CollectionTable(name = "simplified_debts", joinColumns = @JoinColumn(name = "log_id"))
  private List<DebtEntry> simplifiedDebts;
}
