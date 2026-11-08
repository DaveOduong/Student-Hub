package com.example

import com.example.data.source.AssignmentCodeRepository
import com.example.data.source.AssignmentGuideRepository
import com.example.domain.validation.StudentValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testStudentValidationRequiredFields() {
    val result = StudentValidator.validateForm(
      name = "",
      regNo = "",
      email = "",
      course = ""
    )
    assertFalse(result.isValid)
    assertEquals("Student name is required and cannot be empty.", result.nameError)
    assertEquals("Registration number is required and cannot be empty.", result.regNoError)
    assertEquals("Email address is required and cannot be empty.", result.emailError)
    assertEquals("Course is required and cannot be empty.", result.courseError)
  }

  @Test
  fun testEmailFormatValidation() {
    val invalidEmails = listOf("plainaddress", "@missingusername.com", "username@.com", "user@site")
    for (email in invalidEmails) {
      val res = StudentValidator.validateEmail(email)
      assertFalse("Expected '$email' to be invalid", res.isValid)
    }

    val validEmails = listOf("student@university.edu", "john.doe@college.ac.ke", "admin@school.org")
    for (email in validEmails) {
      val res = StudentValidator.validateEmail(email)
      assertTrue("Expected '$email' to be valid", res.isValid)
    }
  }

  @Test
  fun testDuplicateRegistrationNumber() {
    val duplicateResult = StudentValidator.validateRegistrationNumber("CIT-221-001/2023", isDuplicate = true)
    assertFalse(duplicateResult.isValid)
    assertEquals("This registration number already exists in student_db.", duplicateResult.errorMessage)

    val validResult = StudentValidator.validateRegistrationNumber("CIT-221-001/2023", isDuplicate = false)
    assertTrue(validResult.isValid)
  }

  @Test
  fun testAssignmentSourceFilesIncluded() {
    val files = AssignmentCodeRepository.files
    assertTrue(files.any { it.fileName == "README.md" })
    assertTrue(files.any { it.fileName == "database.sql" })
    assertTrue(files.any { it.fileName == "db_connect.php" })
    assertTrue(files.any { it.fileName == "index.php" })
    assertTrue(files.any { it.fileName == "create.php" })
    assertTrue(files.any { it.fileName == "update.php" })
    assertTrue(files.any { it.fileName == "delete.php" })
    assertTrue(files.any { it.fileName == "validation.js" })
    assertTrue(files.any { it.fileName == "style.css" })
  }

  @Test
  fun testAssignmentQuestionsCoverage() {
    val questions = AssignmentGuideRepository.questions
    assertEquals(7, questions.size)
  }
}
