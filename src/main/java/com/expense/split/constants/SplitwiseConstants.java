package com.expense.split.constants;

public final class SplitwiseConstants {

  private SplitwiseConstants() {}

  public static final String EXCEPTION_PERCENTAGE_SPLIT_ONE =
      "Split percentages must match number of users";
  public static final String EXCEPTION_PERCENTAGE_SPLIT_TWO =
      "Split percentages must add up to 100";
  public static final String EXCEPTION_GROUP_ONE = "Group not found";
  public static final String OWES_YOU = " owes you";
  public static final String YOU_OWE = "You owe ";
  public static final String EXCEPTION_USER_ONE = "User not found";
  public static final String EXCEPTION_USER_TWO = "Payer not found";
  public static final String EXCEPTION_USER_THREE = "Recipient not found";
  public static final String USER = "S-";
  public static final String USER_ONE = "User ";
  public static final String NOT_IN_GROUP = " not in group";
  public static final String NOT_FOUND = " not found";
  public static final String GROUP = "group";
  public static final String EXPENSE = "expense";
  public static final String NOTIFICATION_MSG = "[NOTIFICATION to {}]: {}";
  public static final String SWAGGER_TITLE = "Splitwise Expense Manager API";
  public static final String SWAGGER_DESC =
      "API documentation for managing groups, users, expenses, and settlements.";
  public static final String SWAGGER_VERSION = "1.0.0";
  public static final double EPSILON = 0.01;
}
