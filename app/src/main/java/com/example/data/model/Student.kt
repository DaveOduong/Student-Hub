package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a student record in the MySQL/Room database `student_db`.
 * Corresponds to table `students`.
 *
 * Fields captured:
 * - Student Name
 * - Registration Number
 * - Email
 * - Course
 */
@Entity(
    tableName = "students",
    indices = [Index(value = ["registration_number"], unique = true)]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "registration_number")
    val registrationNumber: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "course")
    val course: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
