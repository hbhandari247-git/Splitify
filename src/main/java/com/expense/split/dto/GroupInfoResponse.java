package com.expense.split.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoResponse {
  private String groupId;
  private String groupName;
  private Set<String> groupMembers;
}
