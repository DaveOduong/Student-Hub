package com.example.data.source

import com.example.data.model.Student

object WebPortalGenerator {

    fun generateHtml(students: List<Student>): String {
        val studentRows = if (students.isEmpty()) {
            """<tr><td colspan="6" style="text-align:center; padding: 24px; color:#64748b;">No registered students found in student_db. Use the form above to add one.</td></tr>"""
        } else {
            students.joinToString("\n") { s ->
                """
                <tr>
                    <td><strong>#${s.id}</strong></td>
                    <td><span class="student-name">${escapeHtml(s.name)}</span></td>
                    <td><span class="badge">${escapeHtml(s.registrationNumber)}</span></td>
                    <td>${escapeHtml(s.email)}</td>
                    <td><span class="course-pill">${escapeHtml(s.course)}</span></td>
                    <td class="action-cell">
                        <button class="btn-action btn-edit" onclick="editStudent(${s.id}, '${escapeJs(s.name)}', '${escapeJs(s.registrationNumber)}', '${escapeJs(s.email)}', '${escapeJs(s.course)}')">Edit</button>
                        <button class="btn-action btn-delete" onclick="deleteStudent(${s.id}, '${escapeJs(s.name)}')">Delete</button>
                    </td>
                </tr>
                """.trimIndent()
            }
        }

        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Student Management System</title>
    <style>
        :root {
            --primary: #1E3A8A;
            --primary-hover: #1e40af;
            --secondary: #D97706;
            --success: #16A34A;
            --danger: #DC2626;
            --slate-800: #1E293B;
            --slate-600: #475569;
            --slate-100: #F1F5F9;
            --slate-50: #F8FAFC;
            --border: #E2E8F0;
            --radius: 12px;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body { background-color: var(--slate-50); color: var(--slate-800); padding: 14px; font-size: 14px; }
        .hero { background: linear-gradient(135deg, #1E3A8A 0%, #312E81 100%); color: white; border-radius: var(--radius); padding: 18px; margin-bottom: 16px; box-shadow: 0 4px 10px rgba(30, 58, 138, 0.2); }
        .hero h1 { font-size: 1.3rem; margin-bottom: 4px; font-weight: 700; }
        .hero p { font-size: 0.85rem; opacity: 0.85; }
        .badge-live { display: inline-block; background: #22C55E; color: white; font-size: 0.7rem; font-weight: 700; padding: 2px 8px; border-radius: 12px; margin-top: 6px; text-transform: uppercase; }

        .card { background: white; border-radius: var(--radius); padding: 16px; border: 1px solid var(--border); box-shadow: 0 2px 5px rgba(0,0,0,0.04); margin-bottom: 16px; }
        .card h2 { font-size: 1.1rem; color: var(--primary); margin-bottom: 12px; display: flex; align-items: center; justify-content: space-between; }
        .count-tag { font-size: 0.8rem; background: var(--slate-100); color: var(--slate-600); padding: 2px 8px; border-radius: 12px; font-weight: 600; }

        .form-group { margin-bottom: 12px; }
        label { display: block; font-weight: 600; font-size: 0.82rem; margin-bottom: 4px; color: var(--slate-600); }
        .req { color: var(--danger); }
        input, select { width: 100%; padding: 10px 12px; border: 1.5px solid var(--border); border-radius: 8px; font-size: 0.9rem; background: #fff; transition: border-color 0.2s; }
        input:focus, select:focus { outline: none; border-color: var(--primary); }
        .error-msg { color: var(--danger); font-size: 0.75rem; margin-top: 3px; display: none; }
        .error-visible { display: block; }
        .input-error { border-color: var(--danger) !important; background-color: #FEF2F2; }

        .btn-submit { width: 100%; background: var(--primary); color: white; padding: 12px; border: none; border-radius: 8px; font-size: 0.95rem; font-weight: 600; cursor: pointer; transition: background 0.2s; margin-top: 4px; }
        .btn-submit:hover { background: var(--primary-hover); }
        .btn-reset { width: 100%; background: var(--slate-100); color: var(--slate-600); padding: 8px; border: 1px solid var(--border); border-radius: 8px; font-size: 0.82rem; margin-top: 8px; cursor: pointer; }

        .table-wrap { overflow-x: auto; -webkit-overflow-scrolling: touch; border-radius: 8px; border: 1px solid var(--border); }
        table { width: 100%; border-collapse: collapse; min-width: 540px; font-size: 0.84rem; background: white; }
        th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid var(--border); }
        th { background: #F8FAFC; color: var(--slate-600); font-weight: 600; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.04em; }
        tr:last-child td { border-bottom: none; }
        .student-name { font-weight: 600; color: #0F172A; }
        .badge { background: #FEF3C7; color: #92400E; padding: 3px 6px; border-radius: 4px; font-family: monospace; font-size: 0.76rem; font-weight: 600; }
        .course-pill { background: #E0E7FF; color: #3730A3; padding: 3px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 500; }
        
        .action-cell { white-space: nowrap; }
        .btn-action { padding: 4px 8px; border-radius: 4px; border: none; font-size: 0.75rem; font-weight: 600; cursor: pointer; margin-right: 4px; }
        .btn-edit { background: #DBEAFE; color: #1D4ED8; }
        .btn-delete { background: #FEE2E2; color: #DC2626; }

        /* Notification toast */
        #toast { position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background: #1E293B; color: white; padding: 10px 18px; border-radius: 20px; font-size: 0.85rem; box-shadow: 0 4px 12px rgba(0,0,0,0.25); display: none; z-index: 100; font-weight: 500; }
    </style>
</head>
<body>
    <div class="hero">
        <h1>Student Management Portal</h1>
        <p>PHP & MySQL Full-Stack Simulation (Connected to student_db)</p>
        <span class="badge-live">● Live Database Active</span>
    </div>

    <!-- Student Registration Form (Question 2) -->
    <div class="card">
        <h2 id="formTitle">Register New Student</h2>
        <form id="studentForm" onsubmit="event.preventDefault(); handleSubmit();">
            <input type="hidden" id="editId" value="">
            
            <div class="form-group">
                <label for="name">Student Name <span class="req">*</span></label>
                <input type="text" id="name" placeholder="e.g. John Doe" autocomplete="off">
                <div class="error-msg" id="nameError">Student name is required.</div>
            </div>

            <div class="form-group">
                <label for="regNo">Registration Number <span class="req">*</span></label>
                <input type="text" id="regNo" placeholder="e.g. CIT-221-001/2023" autocomplete="off">
                <div class="error-msg" id="regError">Registration number is required.</div>
            </div>

            <div class="form-group">
                <label for="email">Email Address <span class="req">*</span></label>
                <input type="email" id="email" placeholder="e.g. student@university.edu" autocomplete="off">
                <div class="error-msg" id="emailError">Please enter a valid email address.</div>
            </div>

            <div class="form-group">
                <label for="course">Course of Study <span class="req">*</span></label>
                <select id="course">
                    <option value="">-- Select Course --</option>
                    <option value="Computer Science">Computer Science</option>
                    <option value="Information Technology">Information Technology</option>
                    <option value="Software Engineering">Software Engineering</option>
                    <option value="Data Science">Data Science</option>
                    <option value="Business Computing">Business Computing</option>
                </select>
                <div class="error-msg" id="courseError">Please select a course.</div>
            </div>

            <button type="submit" id="submitBtn" class="btn-submit">Save Student Record</button>
            <button type="button" id="cancelBtn" class="btn-reset" style="display:none;" onclick="cancelEdit()">Cancel Edit</button>
        </form>
    </div>

    <!-- Student Records Table (Question 5) -->
    <div class="card">
        <h2>
            Registered Students (students table)
            <span class="count-tag">${students.size} Records</span>
        </h2>
        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>#ID</th>
                        <th>Student Name</th>
                        <th>Reg Number</th>
                        <th>Email</th>
                        <th>Course</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="studentTbody">
                    $studentRows
                </tbody>
            </table>
        </div>
    </div>

    <div id="toast"></div>

    <script>
        function showToast(msg) {
            const toast = document.getElementById('toast');
            toast.textContent = msg;
            toast.style.display = 'block';
            setTimeout(() => { toast.style.display = 'none'; }, 2500);
        }

        function validateInput() {
            let valid = true;
            const name = document.getElementById('name');
            const regNo = document.getElementById('regNo');
            const email = document.getElementById('email');
            const course = document.getElementById('course');

            const nameErr = document.getElementById('nameError');
            const regErr = document.getElementById('regError');
            const emailErr = document.getElementById('emailError');
            const courseErr = document.getElementById('courseError');

            // Reset errors
            [name, regNo, email, course].forEach(el => el.classList.remove('input-error'));
            [nameErr, regErr, emailErr, courseErr].forEach(el => el.classList.remove('error-visible'));

            // 1. Name validation
            if (!name.value.trim()) {
                name.classList.add('input-error');
                nameErr.classList.add('error-visible');
                valid = false;
            }

            // 2. Reg Number validation
            if (!regNo.value.trim()) {
                regNo.classList.add('input-error');
                regErr.classList.add('error-visible');
                valid = false;
            }

            // 3. Email validation (Question 7)
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!email.value.trim() || !emailRegex.test(email.value.trim())) {
                email.classList.add('input-error');
                emailErr.classList.add('error-visible');
                valid = false;
            }

            // 4. Course validation
            if (!course.value) {
                course.classList.add('input-error');
                courseErr.classList.add('error-visible');
                valid = false;
            }

            return valid;
        }

        function handleSubmit() {
            if (!validateInput()) {
                showToast("Please fix the validation errors.");
                return;
            }

            const id = document.getElementById('editId').value;
            const name = document.getElementById('name').value.trim();
            const regNo = document.getElementById('regNo').value.trim();
            const email = document.getElementById('email').value.trim();
            const course = document.getElementById('course').value;

            if (window.AndroidBridge) {
                if (id) {
                    window.AndroidBridge.updateStudent(parseInt(id), name, regNo, email, course);
                } else {
                    window.AndroidBridge.addStudent(name, regNo, email, course);
                }
            } else {
                showToast("Record saved in simulation mode!");
            }

            cancelEdit();
        }

        function editStudent(id, name, regNo, email, course) {
            document.getElementById('editId').value = id;
            document.getElementById('name').value = name;
            document.getElementById('regNo').value = regNo;
            document.getElementById('email').value = email;
            document.getElementById('course').value = course;

            document.getElementById('formTitle').textContent = "Update Student #" + id;
            document.getElementById('submitBtn').textContent = "Save Changes (UPDATE)";
            document.getElementById('cancelBtn').style.display = 'block';
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }

        function cancelEdit() {
            document.getElementById('editId').value = "";
            document.getElementById('name').value = "";
            document.getElementById('regNo').value = "";
            document.getElementById('email').value = "";
            document.getElementById('course').value = "";
            document.getElementById('formTitle').textContent = "Register New Student";
            document.getElementById('submitBtn').textContent = "Save Student Record";
            document.getElementById('cancelBtn').style.display = 'none';
        }

        function deleteStudent(id, name) {
            if (confirm("Are you sure you want to delete " + name + "?")) {
                if (window.AndroidBridge) {
                    window.AndroidBridge.deleteStudent(id);
                } else {
                    showToast("Deleted student #" + id);
                }
            }
        }
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun escapeJs(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
}
