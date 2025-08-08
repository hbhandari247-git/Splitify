package com.expense.split.controller;

import com.expense.split.dto.UserBalanceResponse;
import com.expense.split.models.User;
import com.expense.split.service.UserService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  public ResponseEntity<User> createUser(@RequestParam String name, @RequestParam String email) {
    User user = userService.createUser(name, email);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<User> getUser(@PathVariable String userId) {
    return userService
        .getUserByUserId(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/getAllUsers")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{userId}/balance")
  public ResponseEntity<Map<String, List<UserBalanceResponse>>> getUserBalance(
      @PathVariable String userId) {
    return ResponseEntity.ok(userService.getUserBalancesSeparated(userId));
  }
}
