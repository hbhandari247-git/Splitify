package com.expense.split.models;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userId;

  private String name;

  private String email;

  @ElementCollection
  @CollectionTable(name = "user_group_balances", joinColumns = @JoinColumn(name = "user_id"))
  @MapKeyColumn(name = "other_user_id")
  @Column(name = "amount")
  @Builder.Default
  private Map<String, Double> groupBalances = new HashMap<>();

  @ElementCollection
  @CollectionTable(name = "user_individual_balances", joinColumns = @JoinColumn(name = "user_id"))
  @MapKeyColumn(name = "other_user_id")
  @Column(name = "amount")
  @Builder.Default
  private Map<String, Double> individualBalances = new HashMap<>();
}
