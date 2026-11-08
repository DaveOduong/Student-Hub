package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        const val DATABASE_NAME = "student_db"

        @Volatile
        private var INSTANCE: StudentDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): StudentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(StudentDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val INITIAL_STUDENTS = listOf(
            Student(
                id = 1,
                name = "Alice Wambui",
                registrationNumber = "CIT-221-001/2023",
                email = "alice.wambui@student.edu",
                course = "Computer Science"
            ),
            Student(
                id = 2,
                name = "Brian Omondi",
                registrationNumber = "BIT-221-045/2023",
                email = "brian.omondi@student.edu",
                course = "Information Technology"
            ),
            Student(
                id = 3,
                name = "Catherine Mwangi",
                registrationNumber = "BSE-102-019/2024",
                email = "catherine.mwangi@student.edu",
                course = "Software Engineering"
            ),
            Student(
                id = 4,
                name = "David Kiprop",
                registrationNumber = "CDS-301-082/2022",
                email = "david.kiprop@student.edu",
                course = "Data Science"
            ),
            Student(
                id = 5,
                name = "Emily Cherotich",
                registrationNumber = "BBC-110-064/2024",
                email = "emily.cherotich@student.edu",
                course = "Business Computing"
            )
        )
    }

    private class StudentDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.studentDao()
                    dao.insertAll(INITIAL_STUDENTS)
                }
            }
        }
    }
}
