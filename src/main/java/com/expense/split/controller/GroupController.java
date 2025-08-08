package com.expense.split.controller;

import com.expense.split.dto.GroupBalanceResponse;
import com.expense.split.dto.GroupInfoResponse;
import com.expense.split.dto.SimplifiedDebtDTO;
import com.expense.split.dto.TransactionOptimizationStats;
import com.expense.split.models.DebtSimplificationLog;
import com.expense.split.models.Group;
import com.expense.split.repository.DebtSimplificationLogRepository;
import com.expense.split.service.DebtSimplificationService;
import com.expense.split.service.GroupService;
import com.expense.split.utils.AppUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupService groupService;
  private final DebtSimplificationService debtSimplificationService;
  private final DebtSimplificationLogRepository debtSimplificationLogRepository;

  @PostMapping("/create")
  public ResponseEntity<GroupInfoResponse> createGroup(@RequestParam String name) {
    Group group = groupService.createGroup(name);
    GroupInfoResponse groupInfoResponse = AppUtils.createGroupInfoResponseFromGroup(group);
    return ResponseEntity.ok(groupInfoResponse);
  }

  @PostMapping("/{groupId}/addUser/{userId}")
  public ResponseEntity<GroupInfoResponse> addUserToGroup(
      @PathVariable String groupId, @PathVariable String userId) {
    Optional<Group> group = groupService.addUserToGroup(groupId, userId);
    return group
        .map(AppUtils::createGroupInfoResponseFromGroup)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.badRequest().build());
  }

  @GetMapping("/{groupId}")
  public ResponseEntity<GroupInfoResponse> getGroup(@PathVariable String groupId) {
    return groupService
        .getGroupByGroupId(groupId)
        .map(AppUtils::createGroupInfoResponseFromGroup)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{groupId}/balances")
  public ResponseEntity<List<GroupBalanceResponse>> getGroupBalances(@PathVariable String groupId) {
    return ResponseEntity.ok(groupService.getGroupBalances(groupId));
  }

  @GetMapping("/{groupId}/getSimplifiedDebts")
  public ResponseEntity<List<SimplifiedDebtDTO>> getSimplifiedDebts(@PathVariable String groupId) {
    Group group =
        groupService
            .getGroupByGroupId(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    List<SimplifiedDebtDTO> transactions =
        debtSimplificationService.getSimplifiedDebts(group.getMembers());
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/{groupId}/simplificationStats")
  public ResponseEntity<TransactionOptimizationStats> getStats(@PathVariable String groupId) {
    Group group =
        groupService
            .getGroupByGroupId(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    return ResponseEntity.ok(
        debtSimplificationService.getTransactionSimplificationStats(group.getMembers()));
  }

  @PostMapping("/{groupId}/rebuildBalances")
  public ResponseEntity<String> rebuildGroupBalances(@PathVariable String groupId) {
    Group group =
        groupService
            .getGroupByGroupId(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    debtSimplificationService.rebuildGroupBalancesFromExpenses(group);
    return ResponseEntity.ok("Group balances rebuilt from expenses successfully.");
  }

  @GetMapping("/{groupId}/audit")
  public ResponseEntity<List<DebtSimplificationLog>> getSimplificationHistory(
      @PathVariable String groupId) {
    return ResponseEntity.ok(
        debtSimplificationLogRepository.findByGroupIdOrderBySimplifiedAtDesc(groupId));
  }

  @PostMapping("/{groupId}/applySimplification")
  public ResponseEntity<String> applySimplifiedDebts(
      @PathVariable String groupId, @RequestParam String simplifiedByUserId) {
    Group group =
        groupService
            .getGroupByGroupId(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    debtSimplificationService.applySimplifiedDebtsToDatabase(
        group.getMembers(), groupId, simplifiedByUserId);

    return ResponseEntity.ok("Simplified debts applied and saved successfully.");
  }
}
