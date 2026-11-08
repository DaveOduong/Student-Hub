package com.example.data.source

data class QuestionGuide(
    val questionNumber: Int,
    val title: String,
    val questionText: String,
    val conceptExplanation: String,
    val codeSnippet: String,
    val keyTakeaways: List<String>
)

object AssignmentGuideRepository {

    val questions: List<QuestionGuide> = listOf(
        QuestionGuide(
            questionNumber = 1,
            title = "Database & Table Creation",
            questionText = "Create a MySQL database named student_db and a table named students.",
            conceptExplanation = "In relational databases like MySQL, we create a structured container called a database (`student_db`), within which we define tables with explicit column types, nullability constraints, auto-incrementing primary keys, and unique indexes to prevent duplicate student registration numbers.",
            codeSnippet = """
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE students (
  id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  registration_number VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL,
  course VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
""".trimIndent(),
            keyTakeaways = listOf(
                "`id INT AUTO_INCREMENT PRIMARY KEY` uniquely identifies every student.",
                "`registration_number VARCHAR(50) UNIQUE` prevents duplicate enrollments.",
                "`NOT NULL` ensures mandatory field integrity at the database layer.",
                "`created_at TIMESTAMP` automatically tracks record creation timestamp."
            )
        ),
        QuestionGuide(
            questionNumber = 2,
            title = "Student Registration Form",
            questionText = "Design a student registration form that captures student name, registration number, email, and course.",
            conceptExplanation = "The HTML5 registration form uses semantic input fields (`text`, `email`, `select`), label tags with `for` attributes, and `required` attributes for accessible client-side form capture. Form submission uses HTTP POST method directed to `create.php`.",
            codeSnippet = """
<form id="studentForm" action="create.php" method="POST" onsubmit="return validateForm();">
    <div class="form-group">
        <label for="name">Student Name *</label>
        <input type="text" id="name" name="name" required>
    </div>
    <div class="form-group">
        <label for="registration_number">Registration Number *</label>
        <input type="text" id="registration_number" name="registration_number" required>
    </div>
    <div class="form-group">
        <label for="email">Email Address *</label>
        <input type="email" id="email" name="email" required>
    </div>
    <div class="form-group">
        <label for="course">Course of Study *</label>
        <select id="course" name="course" required>
            <option value="">-- Select Course --</option>
            <option value="Computer Science">Computer Science</option>
            <option value="Information Technology">Information Technology</option>
            <option value="Software Engineering">Software Engineering</option>
        </select>
    </div>
    <button type="submit" class="btn btn-primary">Save Student Record</button>
</form>
""".trimIndent(),
            keyTakeaways = listOf(
                "Use `method=\"POST\"` so form data is transmitted securely in HTTP payload rather than URL query parameters.",
                "Capture Name, Registration Number, Email, and Course.",
                "Attach `onsubmit=\"return validateForm();\"` to trigger JavaScript validation before submission."
            )
        ),
        QuestionGuide(
            questionNumber = 3,
            title = "PHP to MySQL Database Connection",
            questionText = "Connect the PHP application to the MySQL database.",
            conceptExplanation = "PDO (PHP Data Objects) is the modern, secure standard for connecting PHP to MySQL. It supports native prepared statements that defend against SQL Injection attacks and provides exception handling for database errors.",
            codeSnippet = """
<?php
${'$'}host     = "localhost";
${'$'}db_name  = "student_db";
${'$'}username = "root";
${'$'}password = "";
${'$'}charset  = "utf8mb4";

${'$'}dsn = "mysql:host=${'$'}host;dbname=${'$'}db_name;charset=${'$'}charset";
${'$'}options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    ${'$'}pdo = new PDO(${'$'}dsn, ${'$'}username, ${'$'}password, ${'$'}options);
} catch (PDOException ${'$'}e) {
    die("Database Connection Failed: " . ${'$'}e->getMessage());
}
?>
""".trimIndent(),
            keyTakeaways = listOf(
                "PDO abstraction layer works with multiple database drivers.",
                "`PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION` surfaces database syntax errors during development.",
                "Wrap connection in `try...catch (PDOException \$e)` for graceful failure."
            )
        ),
        QuestionGuide(
            questionNumber = 4,
            title = "Save Student Records",
            questionText = "Save student records into the database.",
            conceptExplanation = "When the user submits the registration form, `create.php` captures the HTTP POST values, sanitizes them, and executes a parameterized `INSERT INTO` statement via PDO prepared statements.",
            codeSnippet = """
<?php
require_once 'db_connect.php';

${'$'}name    = trim(${'$'}_POST['name']);
${'$'}regNo   = trim(${'$'}_POST['registration_number']);
${'$'}email   = trim(${'$'}_POST['email']);
${'$'}course  = trim(${'$'}_POST['course']);

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
?>
""".trimIndent(),
            keyTakeaways = listOf(
                "Always use named parameters (`:name`, `:reg`, etc.) or positional `?` placeholders.",
                "Never concatenate raw user input directly into SQL strings (`INSERT INTO ... VALUES ('\$name')`), as this causes SQL Injection vulnerabilities.",
                "Execute redirect with `header(\"Location: ...\")` following Post-Redirect-Get pattern."
            )
        ),
        QuestionGuide(
            questionNumber = 5,
            title = "Display Registered Students in a Table",
            questionText = "Display all registered students in a table.",
            conceptExplanation = "The PHP script queries the database with `SELECT * FROM students ORDER BY id DESC`, fetches the result set with `fetchAll()`, and loops through the array using `foreach` to dynamically generate HTML `<tr>` and `<td>` rows.",
            codeSnippet = """
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
        <?php foreach (${'$'}students as ${'$'}row): ?>
            <tr>
                <td><?= htmlspecialchars(${'$'}row['id']) ?></td>
                <td><?= htmlspecialchars(${'$'}row['name']) ?></td>
                <td><?= htmlspecialchars(${'$'}row['registration_number']) ?></td>
                <td><?= htmlspecialchars(${'$'}row['email']) ?></td>
                <td><?= htmlspecialchars(${'$'}row['course']) ?></td>
                <td>
                    <a href="update.php?id=<?= ${'$'}row['id'] ?>">Edit</a>
                    <a href="delete.php?id=<?= ${'$'}row['id'] ?>" onclick="return confirm('Delete?');">Delete</a>
                </td>
            </tr>
        <?php endforeach; ?>
    </tbody>
</table>
""".trimIndent(),
            keyTakeaways = listOf(
                "Always wrap rendered data in `htmlspecialchars(...)` to prevent Cross-Site Scripting (XSS).",
                "Order by `id DESC` or `name ASC` for readable, logical presentation.",
                "Provide fallback UI message when table is empty (`empty(\$students)`)."
            )
        ),
        QuestionGuide(
            questionNumber = 6,
            title = "CRUD Operations (Add, View, Update, Delete)",
            questionText = "Implement CRUD operations (Add, View, Update, and Delete student records).",
            conceptExplanation = "CRUD represents Create (INSERT), Read (SELECT), Update (UPDATE), and Delete (DELETE). Update retrieves existing records by primary key `id`, populates the form for editing, and saves changes. Delete removes records by ID with a confirmation safety prompt.",
            codeSnippet = """
-- 1. Create (Add)
INSERT INTO students (name, registration_number, email, course) VALUES (?, ?, ?, ?);

-- 2. Read (View All & View By ID)
SELECT * FROM students ORDER BY id DESC;
SELECT * FROM students WHERE id = ?;

-- 3. Update
UPDATE students SET name = ?, registration_number = ?, email = ?, course = ? WHERE id = ?;

-- 4. Delete
DELETE FROM students WHERE id = ?;
""".trimIndent(),
            keyTakeaways = listOf(
                "C = Create: `create.php` handles new record insertion.",
                "R = Read: `index.php` lists all records; view modal inspects student details.",
                "U = Update: `update.php` pre-populates form and executes SQL UPDATE with WHERE id = ?.",
                "D = Delete: `delete.php` executes SQL DELETE with WHERE id = ? and user confirmation."
            )
        ),
        QuestionGuide(
            questionNumber = 7,
            title = "Input & Email Validation",
            questionText = "Validate user input to ensure that required fields are not left empty and email addresses are valid.",
            conceptExplanation = "Robust validation must occur on both layers: client-side (JavaScript for instantaneous user feedback) and server-side (PHP to ensure security even if client validation is bypassed).",
            codeSnippet = """
// Client-Side (validation.js)
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
if (nameInput.value.trim() === '') {
    showError('Name is required');
}
if (!emailRegex.test(emailInput.value.trim())) {
    showError('Valid email is required');
}

// Server-Side (create.php & update.php)
if (empty(${'$'}name) || empty(${'$'}regNo) || empty(${'$'}email) || empty(${'$'}course)) {
    die("All required fields must be filled.");
}
if (!filter_var(${'$'}email, FILTER_VALIDATE_EMAIL)) {
    die("Invalid email address format.");
}
""".trimIndent(),
            keyTakeaways = listOf(
                "Client-side validation provides fast UI feedback and stops blank form submission.",
                "Server-side validation (`empty()` & `filter_var(\$email, FILTER_VALIDATE_EMAIL)`) is mandatory for security.",
                "Check for duplicate registration numbers to maintain unique constraint integrity."
            )
        )
    )

    val xamppInstructions = """
### How to Run this PHP & MySQL Project on XAMPP / WAMP:

1. **Install XAMPP**: Download and install XAMPP from apachefriends.org (or WampServer).
2. **Start Services**: Open XAMPP Control Panel and click **Start** next to **Apache** and **MySQL**.
3. **Create Database in phpMyAdmin**:
   - Open your browser and navigate to `http://localhost/phpmyadmin/`.
   - Click on the **SQL** tab.
   - Paste the contents of `database.sql` and click **Go**.
   - This creates `student_db` and the `students` table.
4. **Place Project Files**:
   - Navigate to your XAMPP installation directory, typically `C:\xampp\htdocs\`.
   - Create a folder named `student_db`.
   - Copy `db_connect.php`, `index.php`, `create.php`, `update.php`, `delete.php`, `style.css`, and `validation.js` into `C:\xampp\htdocs\student_db\`.
5. **Launch the Application**:
   - Open your web browser and visit: `http://localhost/student_db/`.
   - You will see the Student Management System form and registered students table!
   - Test adding, viewing, editing, and deleting student records.
""".trimIndent()
}
