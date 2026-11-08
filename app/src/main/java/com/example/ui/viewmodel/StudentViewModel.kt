package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.StudentDatabase
import com.example.data.model.Student
import com.example.data.repository.StudentRepository
import com.example.data.source.AssignmentCodeRepository
import com.example.data.source.AssignmentGuideRepository
import com.example.data.source.QuestionGuide
import com.example.data.source.SourceFile
import com.example.domain.validation.FormValidationResult
import com.example.domain.validation.StudentValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ViewMode {
    CARDS,
    TABLE
}

enum class NavTab {
    STUDENTS,
    WEB_PORTAL,
    CODE_SUITE,
    LAB_GUIDE
}

data class FormState(
    val isOpen: Boolean = false,
    val isEditMode: Boolean = false,
    val studentId: Long? = null,
    val name: String = "",
    val regNo: String = "",
    val email: String = "",
    val course: String = "",
    val validationErrors: FormValidationResult = FormValidationResult(true),
    val isSaving: Boolean = false
)

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository

    init {
        val database = StudentDatabase.getInstance(application, viewModelScope)
        repository = StudentRepository(database.studentDao())
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCourseFilter = MutableStateFlow<String?>(null)
    val selectedCourseFilter: StateFlow<String?> = _selectedCourseFilter.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.TABLE)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _activeTab = MutableStateFlow(NavTab.STUDENTS)
    val activeTab: StateFlow<NavTab> = _activeTab.asStateFlow()

    private val _selectedSourceFile = MutableStateFlow(AssignmentCodeRepository.files.first())
    val selectedSourceFile: StateFlow<SourceFile> = _selectedSourceFile.asStateFlow()

    private val _selectedQuestion = MutableStateFlow<QuestionGuide?>(null)
    val selectedQuestion: StateFlow<QuestionGuide?> = _selectedQuestion.asStateFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    private val _studentForDetail = MutableStateFlow<Student?>(null)
    val studentForDetail: StateFlow<Student?> = _studentForDetail.asStateFlow()

    private val _studentToDelete = MutableStateFlow<Student?>(null)
    val studentToDelete: StateFlow<Student?> = _studentToDelete.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Filtered students flow
    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredStudents: StateFlow<List<Student>> = combine(
        allStudents,
        _searchQuery,
        _selectedCourseFilter
    ) { students, query, courseFilter ->
        students.filter { student ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                student.name.contains(query, ignoreCase = true) ||
                        student.registrationNumber.contains(query, ignoreCase = true) ||
                        student.email.contains(query, ignoreCase = true) ||
                        student.course.contains(query, ignoreCase = true)
            }
            val matchesCourse = courseFilter == null || student.course.equals(courseFilter, ignoreCase = true)
            matchesQuery && matchesCourse
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courseList: StateFlow<List<String>> = allStudents.combine(_selectedCourseFilter) { students, _ ->
        val predefined = listOf(
            "Computer Science",
            "Information Technology",
            "Software Engineering",
            "Data Science",
            "Business Computing"
        )
        (predefined + students.map { it.course }).distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCourseFilter(course: String?) {
        _selectedCourseFilter.value = course
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setActiveTab(tab: NavTab) {
        _activeTab.value = tab
    }

    fun selectSourceFile(file: SourceFile) {
        _selectedSourceFile.value = file
    }

    fun selectQuestionGuide(guide: QuestionGuide?) {
        _selectedQuestion.value = guide
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    // Detail dialog
    fun openDetail(student: Student) {
        _studentForDetail.value = student
    }

    fun closeDetail() {
        _studentForDetail.value = null
    }

    // Delete dialog
    fun promptDelete(student: Student) {
        _studentToDelete.value = student
    }

    fun dismissDelete() {
        _studentToDelete.value = null
    }

    fun confirmDelete() {
        val student = _studentToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteStudent(student)
                .onSuccess {
                    _snackbarMessage.value = "Student '${student.name}' removed from student_db."
                }
                .onFailure {
                    _snackbarMessage.value = "Failed to delete student: ${it.localizedMessage}"
                }
            _studentToDelete.value = null
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            repository.deleteStudentById(id)
                .onSuccess {
                    _snackbarMessage.value = "Student #$id deleted from student_db."
                }
                .onFailure {
                    _snackbarMessage.value = "Delete failed: ${it.localizedMessage}"
                }
        }
    }

    // Form management
    fun openAddForm() {
        _formState.value = FormState(
            isOpen = true,
            isEditMode = false,
            course = "Computer Science"
        )
    }

    fun openEditForm(student: Student) {
        _formState.value = FormState(
            isOpen = true,
            isEditMode = true,
            studentId = student.id,
            name = student.name,
            regNo = student.registrationNumber,
            email = student.email,
            course = student.course
        )
    }

    fun updateFormField(
        name: String? = null,
        regNo: String? = null,
        email: String? = null,
        course: String? = null
    ) {
        _formState.value = _formState.value.copy(
            name = name ?: _formState.value.name,
            regNo = regNo ?: _formState.value.regNo,
            email = email ?: _formState.value.email,
            course = course ?: _formState.value.course,
            validationErrors = FormValidationResult(true) // Clear errors on typing
        )
    }

    fun closeForm() {
        _formState.value = FormState(isOpen = false)
    }

    fun submitForm() {
        val current = _formState.value
        viewModelScope.launch {
            // Check uniqueness of registration number
            val isDuplicate = repository.isRegistrationNumberTaken(
                regNo = current.regNo,
                excludeId = current.studentId
            )

            val validation = StudentValidator.validateForm(
                name = current.name,
                regNo = current.regNo,
                email = current.email,
                course = current.course,
                isDuplicateRegNo = isDuplicate
            )

            if (!validation.isValid) {
                _formState.value = current.copy(validationErrors = validation)
                return@launch
            }

            _formState.value = current.copy(isSaving = true)

            if (current.isEditMode && current.studentId != null) {
                // Update
                val updated = Student(
                    id = current.studentId,
                    name = current.name.trim(),
                    registrationNumber = current.regNo.trim(),
                    email = current.email.trim(),
                    course = current.course.trim()
                )
                repository.updateStudent(updated)
                    .onSuccess {
                        _snackbarMessage.value = "Student #${current.studentId} updated successfully."
                        closeForm()
                    }
                    .onFailure {
                        _snackbarMessage.value = "Update error: ${it.localizedMessage}"
                        _formState.value = current.copy(isSaving = false)
                    }
            } else {
                // Insert (Question 4)
                val newStudent = Student(
                    name = current.name.trim(),
                    registrationNumber = current.regNo.trim(),
                    email = current.email.trim(),
                    course = current.course.trim()
                )
                repository.insertStudent(newStudent)
                    .onSuccess { newId ->
                        _snackbarMessage.value = "Student registered successfully (ID: #$newId)."
                        closeForm()
                    }
                    .onFailure {
                        _snackbarMessage.value = "Registration error: ${it.localizedMessage}"
                        _formState.value = current.copy(isSaving = false)
                    }
            }
        }
    }

    fun addStudentDirect(name: String, regNo: String, email: String, course: String) {
        viewModelScope.launch {
            val isDuplicate = repository.isRegistrationNumberTaken(regNo)
            val validation = StudentValidator.validateForm(name, regNo, email, course, isDuplicate)
            if (!validation.isValid) {
                val error = validation.nameError ?: validation.regNoError ?: validation.emailError ?: validation.courseError
                _snackbarMessage.value = "Validation error: $error"
                return@launch
            }

            val student = Student(
                name = name.trim(),
                registrationNumber = regNo.trim(),
                email = email.trim(),
                course = course.trim()
            )
            repository.insertStudent(student)
                .onSuccess { id ->
                    _snackbarMessage.value = "Added student #$id via Web Portal."
                }
                .onFailure {
                    _snackbarMessage.value = "Failed to add student: ${it.localizedMessage}"
                }
        }
    }

    fun updateStudentDirect(id: Long, name: String, regNo: String, email: String, course: String) {
        viewModelScope.launch {
            val isDuplicate = repository.isRegistrationNumberTaken(regNo, excludeId = id)
            val validation = StudentValidator.validateForm(name, regNo, email, course, isDuplicate)
            if (!validation.isValid) {
                val error = validation.nameError ?: validation.regNoError ?: validation.emailError ?: validation.courseError
                _snackbarMessage.value = "Validation error: $error"
                return@launch
            }

            val student = Student(
                id = id,
                name = name.trim(),
                registrationNumber = regNo.trim(),
                email = email.trim(),
                course = course.trim()
            )
            repository.updateStudent(student)
                .onSuccess {
                    _snackbarMessage.value = "Updated student #$id via Web Portal."
                }
                .onFailure {
                    _snackbarMessage.value = "Update failed: ${it.localizedMessage}"
                }
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
                .onSuccess {
                    _snackbarMessage.value = "Reset student_db with default sample records."
                }
                .onFailure {
                    _snackbarMessage.value = "Reset failed: ${it.localizedMessage}"
                }
        }
    }

    fun clearAllStudents() {
        viewModelScope.launch {
            repository.clearAllStudents()
                .onSuccess {
                    _snackbarMessage.value = "All records purged from student_db."
                }
                .onFailure {
                    _snackbarMessage.value = "Purge failed: ${it.localizedMessage}"
                }
        }
    }

    fun getExportSqlDump(): String {
        return repository.generateSqlDump(allStudents.value)
    }
}
