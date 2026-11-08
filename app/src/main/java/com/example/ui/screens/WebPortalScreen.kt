package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Student
import com.example.data.source.WebPortalGenerator

class AndroidBridge(
    private val onAdd: (name: String, regNo: String, email: String, course: String) -> Unit,
    private val onUpdate: (id: Long, name: String, regNo: String, email: String, course: String) -> Unit,
    private val onDelete: (id: Long) -> Unit
) {
    @JavascriptInterface
    fun addStudent(name: String, regNo: String, email: String, course: String) {
        onAdd(name, regNo, email, course)
    }

    @JavascriptInterface
    fun updateStudent(id: Long, name: String, regNo: String, email: String, course: String) {
        onUpdate(id, name, regNo, email, course)
    }

    @JavascriptInterface
    fun deleteStudent(id: Long) {
        onDelete(id)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalScreen(
    students: List<Student>,
    onAddStudent: (name: String, regNo: String, email: String, course: String) -> Unit,
    onUpdateStudent: (id: Long, name: String, regNo: String, email: String, course: String) -> Unit,
    onDeleteStudent: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val htmlContent = remember(students) {
        WebPortalGenerator.generateHtml(students)
    }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webViewClient = WebViewClient()
            val bridge = AndroidBridge(onAddStudent, onUpdateStudent, onDeleteStudent)
            addJavascriptInterface(bridge, "AndroidBridge")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Portal Banner info
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Web Portal",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Live Web & PHP Portal",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Interactive HTML/CSS/JS frontend connected to student_db",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = {
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.testTag("btn_refresh_web_portal")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Live WebView
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .testTag("webview_portal_card"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            AndroidView(
                factory = {
                    webView.apply {
                        loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    }
                },
                update = { view ->
                    view.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
