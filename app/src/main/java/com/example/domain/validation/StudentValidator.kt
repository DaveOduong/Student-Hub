package com.example.domain.validation

import android.util.Patterns

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

data class FormValidationResult(
    val isValid: Boolean,
    val nameError: String? = null,
    val regNoError: String? = null,
    val emailError: String? = null,
    val courseError: String? = null
)

object StudentValidator {

    fun validateName(name: String): ValidationResult {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Student name is required and cannot be empty.")
            trimmed.length < 2 -> ValidationResult(false, "Name must be at least 2 characters long.")
            else -> ValidationResult(true)
        }
    }

    fun validateRegistrationNumber(
        regNo: String,
        isDuplicate: Boolean = false
    ): ValidationResult {
        val trimmed = regNo.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Registration number is required and cannot be empty.")
            trimmed.length < 3 -> ValidationResult(false, "Registration number must be at least 3 characters.")
            isDuplicate -> ValidationResult(false, "This registration number already exists in student_db.")
            else -> ValidationResult(true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$".toRegex()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Email address is required and cannot be empty.")
            !emailPattern.matches(trimmed) ->
                ValidationResult(false, "Please enter a valid email address (e.g. student@university.edu).")
            else -> ValidationResult(true)
        }
    }

    fun validateCourse(course: String): ValidationResult {
        val trimmed = course.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Course is required and cannot be empty.")
            else -> ValidationResult(true)
        }
    }

    fun validateForm(
        name: String,
        regNo: String,
        email: String,
        course: String,
        isDuplicateRegNo: Boolean = false
    ): FormValidationResult {
        val nameRes = validateName(name)
        val regRes = validateRegistrationNumber(regNo, isDuplicateRegNo)
        val emailRes = validateEmail(email)
        val courseRes = validateCourse(course)

        val isValid = nameRes.isValid && regRes.isValid && emailRes.isValid && courseRes.isValid

        return FormValidationResult(
            isValid = isValid,
            nameError = nameRes.errorMessage,
            regNoError = regRes.errorMessage,
            emailError = emailRes.errorMessage,
            courseError = courseRes.errorMessage
        )
    }
}
