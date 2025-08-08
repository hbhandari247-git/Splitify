package com.expense.split.repository;

import com.expense.split.models.Split;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SplitRepository extends JpaRepository<Split, Long> {}
