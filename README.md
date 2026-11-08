# Student Management System
### Practical Assignment: Database Integration and Full-Stack Development

A comprehensive, full-stack **Student Management System** developed with **HTML5, CSS3, JavaScript, PHP, and MySQL**, accompanied by an Android demonstration companion application built with **Jetpack Compose and SQLite/Room**.

---

## 📋 Assignment Objectives & Coverage

| # | Objective | Implementation Status | Primary File |
|---|-----------|:---------------------:|--------------|
| **1** | Create MySQL database `student_db` and table `students` | ✅ Completed | `database.sql` |
| **2** | Design student registration form (Name, Reg No, Email, Course) | ✅ Completed | `index.php` |
| **3** | Connect PHP application to MySQL database using secure PDO | ✅ Completed | `db_connect.php` |
| **4** | Save student records into the database with prepared statements | ✅ Completed | `create.php` |
| **5** | Display all registered students in a dynamic responsive table | ✅ Completed | `index.php` |
| **6** | Implement full CRUD operations (Create, Read, Update, Delete) | ✅ Completed | `create.php`, `update.php`, `delete.php` |
| **7** | Validate user input (non-empty fields & RFC email verification) | ✅ Completed | `validation.js` & `create.php` |

---

## 🛠️ Technology Stack

- **Frontend**: HTML5, CSS3 (Flexbox/Grid, Responsive), Vanilla JavaScript
- **Backend**: PHP 7.4 / 8.x with PDO (PHP Data Objects)
- **Database Engine**: MySQL 5.7+ / MariaDB 10.4+
- **Security**: Prepared Statements (Anti-SQLi), Input Sanitization (`htmlspecialchars` Anti-XSS)
- **Local Server**: XAMPP / WAMP / LAMP (Apache & MySQL)
- **Mobile Companion**: Android (Kotlin, Jetpack Compose, Material Design 3, Room SQLite)

---

## 🗄️ Database Schema (`student_db`)

### SQL DDL Definition (`database.sql`)

```sql
CREATE DATABASE IF NOT EXISTS `student_db`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `student_db`;

DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `registration_number` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL,
  `course` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Table Structure

| Field | Type | Null | Key | Default | Extra |
|-------|------|:----:|:---:|:-------:|:-----:|
| `id` | `INT(11)` | NO | PRI | NULL | `auto_increment` |
| `name` | `VARCHAR(100)` | NO | | NULL | |
| `registration_number` | `VARCHAR(50)` | NO | UNI | NULL | Unique index |
| `email` | `VARCHAR(100)` | NO | | NULL | |
| `course` | `VARCHAR(100)` | NO | | NULL | |
| `created_at` | `TIMESTAMP` | NO | | `CURRENT_TIMESTAMP` | |

---

## 📂 Project Directory Structure

```text
student_management_system/
│
├── database.sql        # Database initialization & sample student data
├── db_connect.php      # PDO database connection configuration
├── index.php           # Main portal with Registration Form & dynamic Students Table
├── create.php          # Server-side validation and record insertion (Create)
├── update.php          # Edit student form & record update handler (Update)
├── delete.php          # Student record deletion handler (Delete)
├── validation.js       # Client-side input and email regex validation
├── style.css           # Modern responsive stylesheet for forms & tables
└── README.md           # Documentation, setup guide, and assignment report
```

---

## 🚀 How to Run on XAMPP / WAMP (Localhost)

Follow these 5 steps to run the PHP/MySQL web application:

### Step 1: Install & Launch XAMPP
1. Download and install [XAMPP](https://www.apachefriends.org/) (for Windows, macOS, or Linux).
2. Open the **XAMPP Control Panel**.
3. Click **Start** for both **Apache** and **MySQL**. Verify both show a green "Running" status.

### Step 2: Create the Database in phpMyAdmin
1. Open your web browser and navigate to: `http://localhost/phpmyadmin/`.
2. Click the **SQL** tab in the top menu.
3. Open `database.sql` from this project, copy its entire contents, paste it into the SQL query box, and click **Go**.
4. You will see `student_db` created in the left sidebar containing the `students` table with sample records.

### Step 3: Deploy the Project Files
1. Navigate to your local server document root:
   - **XAMPP Windows**: `C:\xampp\htdocs\`
   - **XAMPP macOS**: `/Applications/XAMPP/xamppfiles/htdocs/`
   - **WampServer**: `C:\wamp64\www\`
2. Create a new folder named `student_db`.
3. Copy the following files into `htdocs/student_db/`:
   - `database.sql`
   - `db_connect.php`
   - `index.php`
   - `create.php`
   - `update.php`
   - `delete.php`
   - `validation.js`
   - `style.css`

### Step 4: Verify Database Connection Configuration
Open `db_connect.php` and ensure credentials match your local MySQL settings (defaults for XAMPP):
```php
$host     = "localhost";
$db_name  = "student_db";
$username = "root";
$password = ""; // Default XAMPP password is empty
```

### Step 5: Launch the Application
1. In your browser, open:
   ```text
   http://localhost/student_db/
   ```
2. You will be greeted by the **Student Management System** interface.
3. Test all CRUD functions:
   - **Register Student**: Fill the form with name, registration number, email, and course, then click **Save Student Record**.
   - **Validation**: Attempt submitting empty fields or invalid emails to verify alerts.
   - **Edit**: Click the **Edit** button on any table row to modify student details.
   - **Delete**: Click the **Delete** button and confirm deletion in the browser prompt.

---

## 🛡️ Input Validation & Security Implementation (Question 7)

### 1. Client-Side Validation (`validation.js`)
Prevents unnecessary HTTP round-trips by intercepting form submission:
- Checks each required input (`name`, `registration_number`, `email`, `course`) for whitespace-only or empty strings.
- Validates the email address against RFC compliant regex: `/^[^\s@]+@[^\s@]+\.[^\s@]+$/`.
- Visually highlights error fields in red with descriptive helper text.

### 2. Server-Side Validation & Security (`create.php` & `update.php`)
Ensures data integrity even if client-side validation is bypassed:
```php
// Check empty required fields
if (empty($name) || empty($regNo) || empty($email) || empty($course)) {
    die("All fields are mandatory.");
}

// Validate email syntax
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    die("Invalid email format provided.");
}

// Prevent SQL Injection using Prepared Statements
$stmt = $pdo->prepare("INSERT INTO students (name, registration_number, email, course) VALUES (:name, :reg, :email, :course)");
$stmt->execute([
    ':name'   => $name,
    ':reg'    => $regNo,
    ':email'  => $email,
    ':course' => $course
]);
```

### 3. Cross-Site Scripting (XSS) Prevention (`index.php`)
Every field rendered into HTML is sanitized using `htmlspecialchars()`:
```php
<td><?= htmlspecialchars($row['name']) ?></td>
<td><?= htmlspecialchars($row['email']) ?></td>
```

---

## 📱 Android Companion Features

The Android application provides:
1. **Students (CRUD)**: Native Material Design 3 interface with SQLite/Room persistence, table/card toggle, instant search, and course filters.
2. **Web Portal (Live)**: In-app WebView simulating the PHP/HTML portal with live two-way synchronization to the database.
3. **PHP Code Suite**: Syntax-highlighted code viewers with one-tap copy and share for all 8 assignment source files.
4. **Lab Guide**: Question-by-question breakdown with architectural explanations and grading points.

---

## 📄 License & Attribution

Developed for academic coursework in **Database Integration and Full-Stack Development**. Free to use, adapt, and distribute for educational purposes.
