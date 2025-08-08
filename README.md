# 📘 SplitwiseApp

**SplitwiseApp** is a full-featured Spring Boot application for managing shared expenses among individuals and groups. It supports multiple split types (Equal, Exact, Percentage), tracks both group and individual balances, and includes a transaction simplification algorithm to reduce the number of settlements required.

---

## 🚀 Features

- ✅ User and Group management
- ✅ Add group and individual expenses
- ✅ Equal, Exact, and Percentage split options (Strategy Pattern)
- ✅ Balance tracking per user (group & individual)
- ✅ Debt simplification to reduce settlement transactions
- ✅ Audit trail with simplification stats
- ✅ Real-time notifications (via log output)
- ✅ RESTful APIs with Swagger documentation

---

## 🧱 Tech Stack

| Layer         | Technology                       |
|---------------|----------------------------------|
| Language      | Java 17                          |
| Framework     | Spring Boot 3                    |
| Database      | MySQL with Spring Data JPA       |
| Build Tool    | Gradle or Maven                  |
| Validation    | Jakarta Bean Validation          |
| Logging       | SLF4J (Spring Boot default)      |
| Docs          | Swagger / OpenAPI                |
| Utilities     | Lombok, HikariCP                 |

---

## 🗂️ Project Structure

```
com.expense.split
├── constants           # Static error messages & constants
├── controller          # REST endpoints for users, groups, expenses
├── dto                 # Request and response DTOs
├── enums               # Enum: SplitType (EQUAL, EXACT, PERCENTAGE)
├── factory             # SplitFactory for resolving strategy
├── handler             # Global exception handler
├── models              # JPA entities (User, Group, Expense, etc.)
├── notification        # Notification logging (to console)
├── pojos               # Balance POJO for simplification logic
├── repository          # Spring Data JPA repositories
├── service             # Business logic layer
├── strategy            # Strategy interface and implementations
└── utils               # Helper for DTO mapping
```

---

## 🔁 Debt Simplification

To minimize settlements between users:
- Net balances are computed across the group
- Debtors and creditors are paired greedily
- Final simplified debts are persisted
- A transaction log is created with:
    - Total transactions before & after
    - Transactions saved
    - Timestamp & user who initiated simplification

---

## 🛠️ Setup & Run

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/splitwise-app.git
cd splitwise-app
```

---

### 2. Configure MySQL

Create a MySQL database:

```sql
CREATE DATABASE expense_db;
```

Update the DB config in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

---

### 3. Run the Application

#### Using Gradle:
```bash
./gradlew bootRun
```

#### Or Using Maven:
```bash
./mvnw spring-boot:run
```

App will be accessible at:  
🔗 **http://localhost:8080**

---

## 🔍 Swagger API Documentation

Once the app is running, visit:  
👉 **http://localhost:8080/swagger-ui/index.html**

You can test all APIs directly from Swagger.

---

## 📊 Sample API Usage

### ➕ Create a User

```http
POST /api/users/create?name=Alice&email=alice@example.com
```

---

### ➕ Create a Group

```http
POST /api/groups/create?name=Trip2025
```

---

### ➕ Add Group Expense

```http
POST /api/expenses/group
Content-Type: application/json

{
  "groupId": "G-1234",
  "description": "Hotel Booking",
  "amount": 3000,
  "paidByUserId": "U-1",
  "involvedUserIds": ["U-1", "U-2", "U-3"],
  "splitType": "EQUAL"
}
```

---

### ➕ Add Individual Expense

```http
POST /api/expenses/individual
Content-Type: application/json

{
  "description": "Taxi",
  "amount": 600,
  "paidByUserId": "U-1",
  "toUserId": "U-2",
  "splitType": "EXACT",
  "splitValues": [300, 300]
}
```

---

## 📌 Roadmap (Phase 2)

- [ ] Replace `RuntimeException` with custom exception classes
- [ ] Add unit and integration tests with JUnit and Mockito
- [ ] Improve Swagger with examples, descriptions, and response codes
- [ ] Add feature to leave group with validation for unsettled balances
- [ ] Normalize balance data structure for scalability
- [ ] Implement user roles and access control (RBAC)

---

## 🤝 Contributing

We welcome contributions!  
Please fork the repository, create a branch, and open a pull request.  
If proposing a major change, kindly open an issue first to discuss it.

---

## 📄 License

This project is licensed under the **MIT License**.  
You are free to use, modify, and distribute it with attribution.


---

## ⚙️ Design Patterns & Algorithms Used

### 🧠 Strategy Pattern (for Splits)
To support multiple ways of splitting an expense:

| Split Type  | Description                                               | Implementation Class             |
|-------------|-----------------------------------------------------------|----------------------------------|
| `EQUAL`     | Splits amount equally among all involved users           | `EqualSplit`                     |
| `EXACT`     | Users specify the exact amount each person owes          | `ExactSplit`                     |
| `PERCENTAGE`| Amount is split based on provided percentage per user    | `PercentageSplit`                |

🔁 These are implemented using the **Strategy Pattern** and resolved at runtime using a `SplitFactory`.

### 🧮 Debt Simplification Algorithm

- Algorithm type: **Greedy Matching**
- Objective: Minimize the number of transactions required to settle all balances in a group.
- Steps:
    1. Compute **net balance** of each user.
    2. Separate users into **debtors** (negative balance) and **creditors** (positive balance).
    3. Sort both lists by absolute value (descending).
    4. Pair largest debtor with largest creditor and record a transaction.
    5. Repeat until balances converge within an `EPSILON` threshold (e.g., 0.01).

🔄 Each simplification is:
- Logged in the `DebtSimplificationLog` table
- Tracked with stats: original vs. simplified transactions and total saved
