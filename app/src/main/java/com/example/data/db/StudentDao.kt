package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE LOWER(registration_number) = LOWER(:regNo) LIMIT 1")
    suspend fun getStudentByRegNo(regNo: String): Student?

    @Query("""
        SELECT * FROM students 
        WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' 
           OR LOWER(registration_number) LIKE '%' || LOWER(:query) || '%' 
           OR LOWER(email) LIKE '%' || LOWER(:query) || '%' 
           OR LOWER(course) LIKE '%' || LOWER(:query) || '%' 
        ORDER BY id DESC
    """)
    fun searchStudents(query: String): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student): Int

    @Delete
    suspend fun deleteStudent(student: Student): Int

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM students")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>
}
