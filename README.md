# Store Management System

A **console-based Store Management System** built with **Java**, **MySQL**, and **JDBC** for a college-level project. Demonstrates OOP principles including Encapsulation, Abstraction, and Polymorphism.

---

## Features

| Module | Features |
|---|---|
| **Authentication** | Login, role-based access (Admin / StoreKeeper) |
| **Inventory** | Add, Update, Delete, View products; Stock tracking; Supplier info |
| **Sales** | Create sale, auto deduct stock, Sales history, Sales report |

---

## Project Structure

```
Store Management System/
├── database.sql            ← MySQL setup script (run this first!)
├── README.md
└── src/
    ├── Main.java           ← Program entry point & all menus
    ├── DBConnection.java   ← Singleton JDBC connection manager
    ├── User.java           ← User model (entity)
    ├── Product.java        ← Product model (entity)
    ├── Sale.java           ← Sale model (entity)
    ├── AuthService.java    ← Login & user registration logic
    ├── InventoryService.java ← CRUD operations for products
    └── SalesService.java   ← Sales transactions & reports
```

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 8 or above |
| MySQL Server | 5.7 or above |
| MySQL Connector/J | 8.x (`mysql-connector-j-8.x.x.jar`) |

---

## Setup Instructions

### Step 1 — Set up the Database

1. Open **MySQL Workbench** or **Command Prompt** and log in:
   ```bash
   mysql -u root -p
   ```
2. Run the SQL script:
   ```sql
   SOURCE D:/Codes/Store Management System/database.sql;
   ```
   This will create the `store_management` database, all tables, and seed default users/products.

### Step 2 — Download MySQL JDBC Driver

1. Download `mysql-connector-j-x.x.x.jar` from [https://dev.mysql.com/downloads/connector/j/](https://dev.mysql.com/downloads/connector/j/)
2. Place the JAR file in a `lib/` folder inside the project:
   ```
   Store Management System/
   └── lib/
       └── mysql-connector-j-8.x.x.jar
   ```

### Step 3 — Configure Database Connection

Open `src/DBConnection.java` and update these constants if needed:

```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/store_management";
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = ""; // ← Your MySQL password
```

### Step 4 — Compile the Project

Open **Command Prompt**, navigate to the project root, and run:

```bash
cd "D:\Codes\Store Management System"

javac -cp "lib\mysql-connector-j-8.x.x.jar" -d out src\*.java
```

> Replace `mysql-connector-j-8.x.x.jar` with your actual filename.

### Step 5 — Run the Program

```bash
java -cp "out;lib\mysql-connector-j-8.x.x.jar" Main
```

---

## Default Login Credentials

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin |
| `keeper` | `keeper123` | StoreKeeper |

---

## Example Program Output

```
=======================================================
       STORE MANAGEMENT SYSTEM
       Powered by Java + MySQL
=======================================================

--- LOGIN ---
Username : admin
Password : admin123

[LOGIN SUCCESS] Welcome, admin! Role: Admin

========================================
  MAIN MENU  [Admin]
========================================
  1. Inventory Management
  2. Sales Management
  3. Add New User
  0. Exit
========================================
Enter your choice: 1

--- INVENTORY MANAGEMENT ---
  1. View All Products
  2. Add Product        [Admin Only]
  3. Update Product     [Admin Only]
  4. Delete Product     [Admin Only]
  0. Back to Main Menu
Enter your choice: 1

========================================================================
  ID    Name                      Price      Qty      Supplier
========================================================================
  1     Rice (1kg)                55.00      100      FreshFarm Traders
  2     Sugar (1kg)               42.00      80       SweetHarvest Co.
  3     Cooking Oil               90.00      50       NaturePure Oils
  4     Wheat Flour               35.00      60       GrainMills Ltd.
========================================================================
  Total products: 4
```

---

## Role-Based Access Control

| Feature | Admin | StoreKeeper |
|---|---|---|
| View Products | ✅ | ✅ |
| Add/Update/Delete Products | ✅ | ❌ |
| Create Sales | ✅ | ✅ |
| View Sales History | ✅ | ✅ |
| Generate Sales Report | ✅ | ✅ |
| Add New User | ✅ | ❌ |

---

## OOP Concepts Used

| Concept | Where Applied |
|---|---|
| **Encapsulation** | `User.java`, `Product.java`, `Sale.java` — all private fields with getters/setters |
| **Abstraction** | Service classes hide all SQL logic from `Main.java` |
| **Polymorphism** | Role-based menu rendering in `showMainMenu()` |
| **Modularity** | Each class has a single responsibility (Model / Service / UI) |
