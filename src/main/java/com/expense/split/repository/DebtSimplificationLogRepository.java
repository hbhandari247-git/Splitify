package com.expense.split.repository;

import com.expense.split.models.DebtSimplificationLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtSimplificationLogRepository
    extends JpaRepository<DebtSimplificationLog, Long> {
  List<DebtSimplificationLog> findByGroupIdOrderBySimplifiedAtDesc(String groupId);
}
