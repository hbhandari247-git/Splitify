package com.expense.split.repository;

import com.expense.split.models.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
  Optional<Group> findByGroupId(String groupId);
}
