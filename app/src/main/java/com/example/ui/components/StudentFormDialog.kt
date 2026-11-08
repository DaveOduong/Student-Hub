package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.FormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormDialog(
    formState: FormState,
    courseOptions: List<String>,
    onFieldChange: (name: String?, regNo: String?, email: String?, course: String?) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!formState.isOpen) return

    val errors = formState.validationErrors
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (formState.isEditMode) "Update Student Record" else "Register New Student",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (formState.isEditMode) "Editing details for ID #${formState.studentId}" else "Fill in all required fields to save into student_db",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Student Name
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = { onFieldChange(it, null, null, null) },
                    label = { Text("Student Name *") },
                    placeholder = { Text("e.g. John Doe") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Name")
                    },
                    isError = errors.nameError != null,
                    supportingText = errors.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_student_name")
                )

                // Registration Number
                OutlinedTextField(
                    value = formState.regNo,
                    onValueChange = { onFieldChange(null, it, null, null) },
                    label = { Text("Registration Number *") },
                    placeholder = { Text("e.g. CIT-221-001/2023") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = "Reg Number")
                    },
                    isError = errors.regNoError != null,
                    supportingText = errors.regNoError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_registration_number")
                )

                // Email Address
                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { onFieldChange(null, null, it, null) },
                    label = { Text("Email Address *") },
                    placeholder = { Text("e.g. student@university.edu") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    },
                    isError = errors.emailError != null,
                    supportingText = errors.emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email")
                )

                // Course Selection
                ExposedDropdownMenuBox(
                    expanded = courseDropdownExpanded,
                    onExpandedChange = { courseDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = formState.course,
                        onValueChange = { onFieldChange(null, null, null, it) },
                        label = { Text("Course of Study *") },
                        placeholder = { Text("Select or type course") },
                        leadingIcon = {
                            Icon(Icons.Default.School, contentDescription = "Course")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded)
                        },
                        isError = errors.courseError != null,
                        supportingText = errors.courseError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                            .testTag("input_course")
                    )

                    ExposedDropdownMenu(
                        expanded = courseDropdownExpanded,
                        onDismissRequest = { courseDropdownExpanded = false }
                    ) {
                        courseOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onFieldChange(null, null, null, option)
                                    courseDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !formState.isSaving,
                modifier = Modifier.testTag("btn_submit_student")
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (formState.isEditMode) "Save Changes" else "Save Student")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !formState.isSaving,
                modifier = Modifier.testTag("btn_cancel_student")
            ) {
                Text("Cancel")
            }
        }
    )
}
