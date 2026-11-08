package com.example.data.source

data class SourceFile(
    val fileName: String,
    val language: String,
    val description: String,
    val content: String
)

object AssignmentCodeRepository {

    val files: List<SourceFile> = listOf(
        SourceFile(
            fileName = "README.md",
            language = "markdown",
            description = "Project documentation, execution guide for XAMPP/WAMP, and assignment answers",
            content = """
# Student Management System
### Practical Assignment: Database Integration and Full-Stack Development

A simple Student Management System developed with HTML, CSS, JavaScript, PHP, and MySQL.

## Assignment Objectives & Coverage:
1. Create MySQL database 'student_db' and table 'students' -> database.sql
2. Design student registration form (Name, Reg No, Email, Course) -> index.php
3. Connect PHP application to MySQL database -> db_connect.php
4. Save student records into database -> create.php
5. Display all registered students in a table -> index.php
6. Implement CRUD operations (Add, View, Update, Delete) -> create.php, update.php, delete.php
7. Validate user input (required fields & valid email) -> validation.js & create.php

## How to Run on XAMPP / WAMP:
1. Start Apache & MySQL in XAMPP Control Panel.
2. In phpMyAdmin (http://localhost/phpmyadmin/), import 'database.sql'.
3. Copy all project PHP/CSS/JS files to 'C:\xampp\htdocs\student_db\'.
4. Open your browser and visit: http://localhost/student_db/
""".trimIndent()
        ),
        SourceFile(
            fileName = "database.sql",
            language = "sql",
            description = "Question 1: Creates MySQL database student_db and table students with constraints",
            content = """
-- ==============================================================
-- Practical Assignment: Database Integration & Full-Stack Dev
-- File: database.sql
-- Description: Creates database 'student_db' & table 'students'
-- ==============================================================

-- 1. Create database if it does not already exist
CREATE DATABASE IF NOT EXISTS `student_db` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. Switch to student_db
USE `student_db`;

-- 3. Create students table
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

-- 4. Insert sample student records
INSERT INTO `students` (`name`, `registration_number`, `email`, `course`) VALUES
('Alice Wambui', 'CIT-221-001/2023', 'alice.wambui@student.edu', 'Computer Science'),
('Brian Omondi', 'BIT-221-045/2023', 'brian.omondi@student.edu', 'Information Technology'),
('Catherine Mwangi', 'BSE-102-019/2024', 'catherine.mwangi@student.edu', 'Software Engineering'),
('David Kiprop', 'CDS-301-082/2022', 'david.kiprop@student.edu', 'Data Science'),
('Emily Cherotich', 'BBC-110-064/2024', 'emily.cherotich@student.edu', 'Business Computing');
""".trimIndent()
        ),
        SourceFile(
            fileName = "db_connect.php",
            language = "php",
            description = "Question 3: Connects PHP application to MySQL database using secure PDO",
            content = """
<?php
// ==============================================================
// Practical Assignment: Database Integration & Full-Stack Dev
// File: db_connect.php
// Description: Secure PDO connection to MySQL database student_db
// ==============================================================

// Database configuration settings
${'$'}host     = "localhost";
${'$'}db_name  = "student_db";
${'$'}username = "root";
${'$'}password = "";          // Default XAMPP password is blank
${'$'}charset  = "utf8mb4";

// DSN (Data Source Name)
${'$'}dsn = "mysql:host=${'$'}host;dbname=${'$'}db_name;charset=${'$'}charset";

// PDO driver options
${'$'}options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION, // Throw exceptions on error
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,       // Return associative arrays
    PDO::ATTR_EMULATE_PREPARES   => false,                  // Use native prepared statements
];

try {
    // Establish the PDO connection
    ${'$'}pdo = new PDO(${'$'}dsn, ${'$'}username, ${'$'}password, ${'$'}options);
} catch (PDOException ${'$'}e) {
    // Graceful error termination
    die("<div style='color:red; font-family:sans-serif; padding:20px;'>" .
        "<strong>Database Connection Error:</strong> " . htmlspecialchars(${'$'}e->getMessage()) . 
        "<br>Make sure MySQL service is running in XAMPP/WAMP and 'student_db' exists.</div>");
}
?>
""".trimIndent()
        ),
        SourceFile(
            fileName = "index.php",
            language = "php",
            description = "Question 2 & 5: Student Registration Form & Dynamic Students Table",
            content = """
<?php
// ==============================================================
// Practical Assignment: Database Integration & Full-Stack Dev
// File: index.php
// Description: Main portal with registration form & student list
// ==============================================================

require_once 'db_connect.php';

// Fetch all students from the database (CRUD: Read)
${'$'}stmt = ${'$'}pdo->query("SELECT * FROM students ORDER BY id DESC");
${'$'}students = ${'$'}stmt->fetchAll();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management System</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <h1>Student Management System</h1>
            <p>Database Integration & Full-Stack CRUD Application</p>
        </header>

        <!-- Status Alerts -->
        <?php if (isset(${'$'}_GET['status'])): ?>
            <?php if (${'$'}_GET['status'] === 'success'): ?>
                <div class="alert alert-success">Action completed successfully!</div>
            <?php elseif (${'$'}_GET['status'] === 'error'): ?>
                <div class="alert alert-danger"><?= htmlspecialchars(${'$'}_GET['msg'] ?? 'An error occurred.') ?></div>
            <?php endif; ?>
        <?php endif; ?>

        <div class="content-grid">
            <!-- Student Registration Form (Question 2) -->
            <div class="card form-card">
                <h2>Register New Student</h2>
                <form id="studentForm" action="create.php" method="POST" onsubmit="return validateForm();">
                    <div class="form-group">
                        <label for="name">Student Name <span class="req">*</span></label>
                        <input type="text" id="name" name="name" placeholder="e.g. John Doe" required>
                        <small class="error-msg" id="nameError"></small>
                    </div>

                    <div class="form-group">
                        <label for="registration_number">Registration Number <span class="req">*</span></label>
                        <input type="text" id="registration_number" name="registration_number" placeholder="e.g. CIT-221-001/2023" required>
                        <small class="error-msg" id="regError"></small>
                    </div>

                    <div class="form-group">
                        <label for="email">Email Address <span class="req">*</span></label>
                        <input type="email" id="email" name="email" placeholder="e.g. student@university.edu" required>
                        <small class="error-msg" id="emailError"></small>
                    </div>

                    <div class="form-group">
                        <label for="course">Course of Study <span class="req">*</span></label>
                        <select id="course" name="course" required>
                            <option value="">-- Select Course --</option>
                            <option value="Computer Science">Computer Science</option>
                            <option value="Information Technology">Information Technology</option>
                            <option value="Software Engineering">Software Engineering</option>
                            <option value="Data Science">Data Science</option>
                            <option value="Business Computing">Business Computing</option>
                        </select>
                        <small class="error-msg" id="courseError"></small>
                    </div>

                    <button type="submit" class="btn btn-primary">Save Student Record</button>
                </form>
            </div>

            <!-- Student Records Table (Question 5) -->
            <div class="card table-card">
                <div class="table-header">
                    <h2>Registered Students (<?= count(${'$'}students) ?>)</h2>
                </div>
                
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>#ID</th>
                                <th>Name</th>
                                <th>Reg Number</th>
                                <th>Email</th>
                                <th>Course</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if (empty(${'$'}students)): ?>
                                <tr>
                                    <td colspan="6" class="text-center">No students registered yet. Fill out the form to add one.</td>
                                </tr>
                            <?php else: ?>
                                <?php foreach (${'$'}students as ${'$'}row): ?>
                                    <tr>
                                        <td><?= htmlspecialchars(${'$'}row['id']) ?></td>
                                        <td><strong><?= htmlspecialchars(${'$'}row['name']) ?></strong></td>
                                        <td><span class="badge"><?= htmlspecialchars(${'$'}row['registration_number']) ?></span></td>
                                        <td><?= htmlspecialchars(${'$'}row['email']) ?></td>
                                        <td><?= htmlspecialchars(${'$'}row['course']) ?></td>
                                        <td class="action-buttons">
                                            <a href="update.php?id=<?= ${'$'}row['id'] ?>" class="btn-sm btn-edit">Edit</a>
                                            <a href="delete.php?id=<?= ${'$'}row['id'] ?>" class="btn-sm btn-delete" onclick="return confirm('Are you sure you want to delete <?= htmlspecialchars(${'$'}row['name']) ?>?');">Delete</a>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="validation.js"></script>
</body>
</html>
""".trimIndent()
        ),
        SourceFile(
            fileName = "create.php",
            language = "php",
            description = "Question 4 & 7: Validates input & saves new student record into MySQL",
            content = """
<?php
// ==============================================================
// Practical Assignment: Database Integration & Full-Stack Dev
// File: create.php
// Description: Server-side validation & record insertion
// ==============================================================

require_once 'db_connect.php';

if (${'$'}_SERVER['REQUEST_METHOD'] === 'POST') {
    // 1. Sanitize and retrieve POST input
    ${'$'}name    = trim(${'$'}_POST['name'] ?? '');
    ${'$'}regNo   = trim(${'$'}_POST['registration_number'] ?? '');
    ${'$'}email   = trim(${'$'}_POST['email'] ?? '');
    ${'$'}course  = trim(${'$'}_POST['course'] ?? '');

    // 2. Server-side validation (Question 7)
    // Validate required non-empty fields
    if (empty(${'$'}name) || empty(${'$'}regNo) || empty(${'$'}email) || empty(${'$'}course)) {
        header("Location: index.php?status=error&msg=" . urlencode("All required fields must be filled."));
        exit;
    }

    // Validate email format
    if (!filter_var(${'$'}email, FILTER_VALIDATE_EMAIL)) {
        header("Location: index.php?status=error&msg=" . urlencode("Invalid email format provided."));
        exit;
    }

    // 3. Check for duplicate registration number
    ${'$'}checkStmt = ${'$'}pdo->prepare("SELECT id FROM students WHERE registration_number = ?");
    ${'$'}checkStmt->execute([${'$'}regNo]);
    if (${'$'}checkStmt->fetch()) {
        header("Location: index.php?status=error&msg=" . urlencode("Registration number already exists."));
        exit;
    }

    // 4. Save into database using prepared statements (Question 4)
    try {
        ${'$'}sql = "INSERT INTO students (name, registration_number, email, course) 
                VALUES (:name, :reg, :email, :course)";
        ${'$'}stmt = ${'$'}pdo->prepare(${'$'}sql);
        ${'$'}stmt->execute([
            ':name'   => ${'$'}name,
            ':reg'    => ${'$'}regNo,
            ':email'  => ${'$'}email,
            ':course' => ${'$'}course
        ]);

        header("Location: index.php?status=success");
        exit;
    } catch (PDOException ${'$'}e) {
        header("Location: index.php?status=error&msg=" . urlencode("Database error: " . ${'$'}e->getMessage()));
        exit;
    }
} else {
    header("Location: index.php");
    exit;
}
?>
""".trimIndent()
        ),
        SourceFile(
            fileName = "update.php",
            language = "php",
            description = "Question 6: Fetches student data, validates updates, and saves changes",
            content = """
<?php
// ==============================================================
// Practical Assignment: Database Integration & Full-Stack Dev
// File: update.php
// Description: Edit existing student record (CRUD: Update)
// ==============================================================

require_once 'db_connect.php';

${'$'}id = ${'$'}_GET['id'] ?? ${'$'}_POST['id'] ?? null;
if (!${'$'}id) {
    header("Location: index.php");
    exit;
}

// Fetch current record
${'$'}stmt = ${'$'}pdo->prepare("SELECT * FROM students WHERE id = ?");
${'$'}stmt->execute([${'$'}id]);
${'$'}student = ${'$'}stmt->fetch();

if (!${'$'}student) {
    header("Location: index.php?status=error&msg=" . urlencode("Student not found."));
    exit;
}

// Handle Form Submission
if (${'$'}_SERVER['REQUEST_METHOD'] === 'POST') {
    ${'$'}name   = trim(${'$'}_POST['name'] ?? '');
    ${'$'}regNo  = trim(${'$'}_POST['registration_number'] ?? '');
    ${'$'}email  = trim(${'$'}_POST['email'] ?? '');
    ${'$'}course = trim(${'$'}_POST['course'] ?? '');

    // Validation (Question 7)
    if (empty(${'$'}name) || empty(${'$'}regNo) || empty(${'$'}email) || empty(${'$'}course)) {
        ${'$'}error = "All fields are required and cannot be empty.";
    } elseif (!filter_var(${'$'}email, FILTER_VALIDATE_EMAIL)) {
        ${'$'}error = "Please provide a valid email address.";
    } else {
        // Check duplicate regNo on other students
        ${'$'}dupCheck = ${'$'}pdo->prepare("SELECT id FROM students WHERE registration_number = ? AND id != ?");
        ${'$'}dupCheck->execute([${'$'}regNo, ${'$'}id]);
        if (${'$'}dupCheck->fetch()) {
            ${'$'}error = "Registration number is already taken by another student.";
        } else {
            // Update database record
            ${'$'}updateSql = "UPDATE students SET name = :name, registration_number = :reg, email = :email, course = :course WHERE id = :id";
            ${'$'}updateStmt = ${'$'}pdo->prepare(${'$'}updateSql);
            ${'$'}updateStmt->execute([
                ':name'   => ${'$'}name,
                ':reg'    => ${'$'}regNo,
                ':email'  => ${'$'}email,
                ':course' => ${'$'}course,
                ':id'     => ${'$'}id
            ]);

            header("Location: index.php?status=success");
            exit;
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Student - Student Management System</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container container-sm">
        <header class="header">
            <h1>Update Student Record</h1>
            <p>Editing details for ID #<?= htmlspecialchars(${'$'}student['id']) ?></p>
        </header>

        <?php if (!empty(${'$'}error)): ?>
            <div class="alert alert-danger"><?= htmlspecialchars(${'$'}error) ?></div>
        <?php endif; ?>

        <div class="card form-card">
            <form action="update.php" method="POST">
                <input type="hidden" name="id" value="<?= htmlspecialchars(${'$'}student['id']) ?>">

                <div class="form-group">
                    <label for="name">Student Name <span class="req">*</span></label>
                    <input type="text" id="name" name="name" value="<?= htmlspecialchars(${'$'}student['name']) ?>" required>
                </div>

                <div class="form-group">
                    <label for="registration_number">Registration Number <span class="req">*</span></label>
                    <input type="text" id="registration_number" name="registration_number" value="<?= htmlspecialchars(${'$'}student['registration_number']) ?>" required>
                </div>

                <div class="form-group">
                    <label for="email">Email Address <span class="req">*</span></label>
                    <input type="email" id="email" name="email" value="<?= htmlspecialchars(${'$'}student['email']) ?>" required>
                </div>

                <div class="form-group">
                    <label for="course">Course <span class="req">*</span></label>
                    <input type="text" id="course" name="course" value="<?= htmlspecialchars(${'$'}student['course']) ?>" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                    <a href="index.php" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
""".trimIndent()
        ),
        SourceFile(
            fileName = "delete.php",
            language = "php",
            description = "Question 6: Deletes a student record safely using prepared statement",
            content = """
<?php
// ==============================================================
// Practical Assignment: Database Integration & Full-Stack Dev
// File: delete.php
// Description: Deletes student record by ID (CRUD: Delete)
// ==============================================================

require_once 'db_connect.php';

${'$'}id = ${'$'}_GET['id'] ?? null;

if (${'$'}id) {
    try {
        ${'$'}stmt = ${'$'}pdo->prepare("DELETE FROM students WHERE id = ?");
        ${'$'}stmt->execute([${'$'}id]);
        header("Location: index.php?status=success");
        exit;
    } catch (PDOException ${'$'}e) {
        header("Location: index.php?status=error&msg=" . urlencode("Delete failed: " . ${'$'}e->getMessage()));
        exit;
    }
} else {
    header("Location: index.php");
    exit;
}
?>
""".trimIndent()
        ),
        SourceFile(
            fileName = "validation.js",
            language = "javascript",
            description = "Question 7: Client-side validation for required fields & email format",
            content = """
/**
 * Practical Assignment: Database Integration & Full-Stack Development
 * File: validation.js
 * Description: Client-side form validation ensuring required fields
 *              are not empty and email format is valid.
 */

function validateForm() {
    let isValid = true;

    // Get input elements
    const nameInput    = document.getElementById('name');
    const regInput     = document.getElementById('registration_number');
    const emailInput   = document.getElementById('email');
    const courseInput  = document.getElementById('course');

    // Get error placeholder elements
    const nameError   = document.getElementById('nameError');
    const regError    = document.getElementById('regError');
    const emailError  = document.getElementById('emailError');
    const courseError = document.getElementById('courseError');

    // Clear previous error messages
    nameError.textContent = '';
    regError.textContent = '';
    emailError.textContent = '';
    courseError.textContent = '';

    // 1. Validate Student Name
    if (nameInput.value.trim() === '') {
        nameError.textContent = 'Student name is required.';
        isValid = false;
    }

    // 2. Validate Registration Number
    if (regInput.value.trim() === '') {
        regError.textContent = 'Registration number is required.';
        isValid = false;
    }

    // 3. Validate Email Address
    const emailValue = emailInput.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (emailValue === '') {
        emailError.textContent = 'Email address is required.';
        isValid = false;
    } else if (!emailRegex.test(emailValue)) {
        emailError.textContent = 'Please enter a valid email address (e.g., student@university.edu).';
        isValid = false;
    }

    // 4. Validate Course
    if (courseInput.value.trim() === '') {
        courseError.textContent = 'Please select a course.';
        isValid = false;
    }

    return isValid;
}
""".trimIndent()
        ),
        SourceFile(
            fileName = "style.css",
            language = "css",
            description = "Responsive styling for student portal, registration form, and data table",
            content = """
/*
 * Practical Assignment: Database Integration & Full-Stack Development
 * File: style.css
 * Description: Modern clean CSS styling for Student Management System
 */

:root {
    --primary: #1E3A8A;
    --primary-hover: #1e40af;
    --secondary: #D97706;
    --success: #16A34A;
    --danger: #DC2626;
    --dark: #0F172A;
    --slate: #475569;
    --light-bg: #F8FAFC;
    --white: #FFFFFF;
    --border: #E2E8F0;
    --radius: 10px;
}

* {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

body {
    background-color: var(--light-bg);
    color: var(--dark);
    padding: 24px;
    line-height: 1.5;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
}

.container-sm {
    max-width: 600px;
}

.header {
    text-align: center;
    margin-bottom: 28px;
    padding-bottom: 16px;
    border-bottom: 2px solid var(--border);
}

.header h1 {
    color: var(--primary);
    font-size: 2rem;
    margin-bottom: 6px;
}

.header p {
    color: var(--slate);
    font-size: 0.95rem;
}

.content-grid {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 24px;
}

@media (max-width: 900px) {
    .content-grid {
        grid-template-columns: 1fr;
    }
}

.card {
    background: var(--white);
    border-radius: var(--radius);
    padding: 24px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.07), 0 2px 4px -2px rgba(0, 0, 0, 0.05);
    border: 1px solid var(--border);
}

.card h2 {
    font-size: 1.25rem;
    color: var(--primary);
    margin-bottom: 18px;
}

.form-group {
    margin-bottom: 16px;
}

label {
    display: block;
    margin-bottom: 6px;
    font-weight: 600;
    font-size: 0.88rem;
    color: var(--slate);
}

.req {
    color: var(--danger);
}

input[type="text"],
input[type="email"],
select {
    width: 100%;
    padding: 10px 14px;
    border: 1.5px solid var(--border);
    border-radius: 6px;
    font-size: 0.95rem;
    transition: border-color 0.2s;
}

input:focus, select:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(30, 58, 138, 0.15);
}

.error-msg {
    color: var(--danger);
    font-size: 0.8rem;
    display: block;
    margin-top: 4px;
}

.btn {
    display: inline-block;
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    text-decoration: none;
    transition: background 0.2s;
}

.btn-primary {
    background-color: var(--primary);
    color: var(--white);
    width: 100%;
}

.btn-primary:hover {
    background-color: var(--primary-hover);
}

.btn-secondary {
    background-color: var(--border);
    color: var(--slate);
}

.btn-sm {
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 0.82rem;
    text-decoration: none;
    font-weight: 600;
}

.btn-edit {
    background: #E0E7FF;
    color: var(--primary);
}

.btn-delete {
    background: #FEE2E2;
    color: var(--danger);
}

.table-responsive {
    overflow-x: auto;
}

.data-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 0.9rem;
}

.data-table th, .data-table td {
    padding: 12px 14px;
    text-align: left;
    border-bottom: 1px solid var(--border);
}

.data-table th {
    background-color: #F1F5F9;
    color: var(--slate);
    font-weight: 600;
    text-transform: uppercase;
    font-size: 0.78rem;
    letter-spacing: 0.05em;
}

.data-table tr:hover {
    background-color: #F8FAFC;
}

.badge {
    background: #FEF3C7;
    color: #92400E;
    padding: 4px 8px;
    border-radius: 4px;
    font-family: monospace;
    font-size: 0.82rem;
}

.alert {
    padding: 12px 16px;
    border-radius: 6px;
    margin-bottom: 20px;
    font-weight: 500;
}

.alert-success {
    background: #DCFCE7;
    color: #166534;
    border: 1px solid #BBF7D0;
}

.alert-danger {
    background: #FEE2E2;
    color: #991B1B;
    border: 1px solid #FECACA;
}

.action-buttons {
    display: flex;
    gap: 8px;
}
""".trimIndent()
        )
    )
}
