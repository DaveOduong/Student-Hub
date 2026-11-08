package com.example.data.repository

import com.example.data.db.StudentDao
import com.example.data.db.StudentDatabase
import com.example.data.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudentRepository(
    private val studentDao: StudentDao
) {
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()

    fun searchStudents(query: String): Flow<List<Student>> {
        return if (query.isBlank()) {
            studentDao.getAllStudents()
        } else {
            studentDao.searchStudents(query)
        }
    }

    suspend fun getStudentById(id: Long): Student? = withContext(Dispatchers.IO) {
        studentDao.getStudentById(id)
    }

    suspend fun isRegistrationNumberTaken(regNo: String, excludeId: Long? = null): Boolean =
        withContext(Dispatchers.IO) {
            val existing = studentDao.getStudentByRegNo(regNo.trim())
            if (existing == null) false else existing.id != excludeId
        }

    suspend fun insertStudent(student: Student): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = studentDao.insertStudent(student)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentDao.updateStudent(student)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(student: Student): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentDao.deleteStudent(student)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudentById(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetWithSampleData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentDao.deleteAll()
            studentDao.insertAll(StudentDatabase.INITIAL_STUDENTS)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllStudents(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            studentDao.deleteAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateSqlDump(students: List<Student>): String {
        val sb = StringBuilder()
        sb.append("-- ===========================================\n")
        sb.append("-- MySQL Database Dump for student_db\n")
        sb.append("-- Table: students\n")
        sb.append("-- Generated on: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n")
        sb.append("-- ===========================================\n\n")
        sb.append("CREATE DATABASE IF NOT EXISTS `student_db`;\n")
        sb.append("USE `student_db`;\n\n")
        sb.append("DROP TABLE IF EXISTS `students`;\n")
        sb.append("CREATE TABLE `students` (\n")
        sb.append("  `id` INT(11) NOT NULL AUTO_INCREMENT,\n")
        sb.append("  `name` VARCHAR(100) NOT NULL,\n")
        sb.append("  `registration_number` VARCHAR(50) NOT NULL UNIQUE,\n")
        sb.append("  `email` VARCHAR(100) NOT NULL,\n")
        sb.append("  `course` VARCHAR(100) NOT NULL,\n")
        sb.append("  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n")
        sb.append("  PRIMARY KEY (`id`)\n")
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n\n")

        if (students.isNotEmpty()) {
            sb.append("INSERT INTO `students` (`id`, `name`, `registration_number`, `email`, `course`) VALUES\n")
            students.forEachIndexed { index, s ->
                val escapedName = s.name.replace("'", "''")
                val escapedReg = s.registrationNumber.replace("'", "''")
                val escapedEmail = s.email.replace("'", "''")
                val escapedCourse = s.course.replace("'", "''")
                sb.append("  (${s.id}, '$escapedName', '$escapedReg', '$escapedEmail', '$escapedCourse')")
                if (index == students.lastIndex) {
                    sb.append(";\n")
                } else {
                    sb.append(",\n")
                }
            }
        }
        return sb.toString()
    }
}
