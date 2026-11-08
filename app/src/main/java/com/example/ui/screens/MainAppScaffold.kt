package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.StudentDetailDialog
import com.example.ui.components.StudentFormDialog
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    viewModel: StudentViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val activeTab by viewModel.activeTab.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val filteredStudents by viewModel.filteredStudents.collectAsState()
    val courses by viewModel.courseList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCourse by viewModel.selectedCourseFilter.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val studentForDetail by viewModel.studentForDetail.collectAsState()
    val studentToDelete by viewModel.studentToDelete.collectAsState()
    val selectedSourceFile by viewModel.selectedSourceFile.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Hub",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.testTag("topbar_more_options")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export MySQL Dump (.sql)") },
                            leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                val dump = viewModel.getExportSqlDump()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("student_db.sql", dump)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "MySQL dump copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset Sample Records") },
                            leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.resetToSampleData()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Purge All Records") },
                            onClick = {
                                showMenu = false
                                viewModel.clearAllStudents()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = activeTab == NavTab.STUDENTS,
                    onClick = { viewModel.setActiveTab(NavTab.STUDENTS) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavTab.STUDENTS) Icons.Filled.Groups else Icons.Outlined.Groups,
                            contentDescription = "Students"
                        )
                    },
                    label = { Text("Students") },
                    modifier = Modifier.testTag("nav_item_students")
                )

                NavigationBarItem(
                    selected = activeTab == NavTab.WEB_PORTAL,
                    onClick = { viewModel.setActiveTab(NavTab.WEB_PORTAL) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavTab.WEB_PORTAL) Icons.Filled.Public else Icons.Outlined.Public,
                            contentDescription = "Web Portal"
                        )
                    },
                    label = { Text("Web Portal") },
                    modifier = Modifier.testTag("nav_item_web_portal")
                )

                NavigationBarItem(
                    selected = activeTab == NavTab.CODE_SUITE,
                    onClick = { viewModel.setActiveTab(NavTab.CODE_SUITE) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavTab.CODE_SUITE) Icons.Filled.Code else Icons.Outlined.Code,
                            contentDescription = "PHP Code"
                        )
                    },
                    label = { Text("PHP Code") },
                    modifier = Modifier.testTag("nav_item_code_suite")
                )

                NavigationBarItem(
                    selected = activeTab == NavTab.LAB_GUIDE,
                    onClick = { viewModel.setActiveTab(NavTab.LAB_GUIDE) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavTab.LAB_GUIDE) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = "Lab Guide"
                        )
                    },
                    label = { Text("Lab Guide") },
                    modifier = Modifier.testTag("nav_item_lab_guide")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                NavTab.STUDENTS -> {
                    StudentsScreen(
                        students = filteredStudents,
                        totalStudentCount = allStudents.size,
                        searchQuery = searchQuery,
                        onSearchChange = viewModel::setSearchQuery,
                        courses = courses,
                        selectedCourse = selectedCourse,
                        onSelectCourse = viewModel::setCourseFilter,
                        viewMode = viewMode,
                        onToggleViewMode = viewModel::setViewMode,
                        onOpenAddForm = viewModel::openAddForm,
                        onViewStudent = viewModel::openDetail,
                        onEditStudent = viewModel::openEditForm,
                        onDeleteStudent = viewModel::promptDelete
                    )
                }
                NavTab.WEB_PORTAL -> {
                    WebPortalScreen(
                        students = allStudents,
                        onAddStudent = viewModel::addStudentDirect,
                        onUpdateStudent = viewModel::updateStudentDirect,
                        onDeleteStudent = viewModel::deleteById
                    )
                }
                NavTab.CODE_SUITE -> {
                    CodeSuiteScreen(
                        selectedFile = selectedSourceFile,
                        onSelectFile = viewModel::selectSourceFile
                    )
                }
                NavTab.LAB_GUIDE -> {
                    LabGuideScreen()
                }
            }
        }
    }

    // Modal Form Dialog (Add / Edit)
    StudentFormDialog(
        formState = formState,
        courseOptions = courses,
        onFieldChange = viewModel::updateFormField,
        onSubmit = viewModel::submitForm,
        onDismiss = viewModel::closeForm
    )

    // View Student Details Modal
    StudentDetailDialog(
        student = studentForDetail,
        onDismiss = viewModel::closeDetail,
        onEdit = viewModel::openEditForm,
        onDelete = viewModel::promptDelete
    )

    // Delete Confirmation Dialog
    DeleteConfirmDialog(
        student = studentToDelete,
        onConfirm = viewModel::confirmDelete,
        onDismiss = viewModel::dismissDelete
    )
}
