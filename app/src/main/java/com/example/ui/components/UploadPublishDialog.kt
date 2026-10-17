package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CalendarTaskEntity
import com.example.data.CreatorSettingsEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UploadPublishDialog(
    initialTitle: String = "10-Minute Viral Masterclass",
    initialDescription: String = "",
    initialPlatform: String = "YouTube",
    settings: CreatorSettingsEntity,
    onPublishSuccess: (CalendarTaskEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPlatform by remember { mutableStateOf(initialPlatform) }
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var privacy by remember { mutableStateOf("Public") } // Public, Unlisted, Private
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var uploadStatusText by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    fun startAutoUpload() {
        isUploading = true
        coroutineScope.launch {
            uploadStatusText = "Connecting to $selectedPlatform API..."
            uploadProgress = 0.15f
            delay(600)

            uploadStatusText = "Encoding 1080p 60fps MP4 & audio..."
            uploadProgress = 0.45f
            delay(800)

            uploadStatusText = "Uploading video chunks to $selectedPlatform..."
            uploadProgress = 0.85f
            delay(900)

            uploadStatusText = "Applying thumbnail & tags..."
            uploadProgress = 1.0f
            delay(500)

            isUploading = false
            isCompleted = true
            uploadStatusText = "Published successfully to $selectedPlatform!"

            val publishedTask = CalendarTaskEntity(
                title = title,
                platform = selectedPlatform,
                contentType = if (selectedPlatform == "YouTube") "Video" else "Reel",
                status = "Published",
                scheduledDate = "2026-10-06",
                scheduledTime = "Just now",
                notes = "Auto-uploaded & published via Creator Studio."
            )
            onPublishSuccess(publishedTask)
            Toast.makeText(context, "Video published to $selectedPlatform!", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareDirectToApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$description")
        }
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Upload video to $selectedPlatform"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("upload_publish_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Auto Video Uploader",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Direct publish to YouTube, Instagram & Facebook",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                if (!isCompleted) {
                    // Platform selector
                    Text(text = "Target Platform:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("YouTube", "Instagram", "Facebook").forEach { p ->
                            FilterChip(
                                selected = selectedPlatform == p,
                                onClick = { selectedPlatform = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Video Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isUploading
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Tags") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        enabled = !isUploading
                    )

                    // Privacy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Visibility:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Public", "Unlisted", "Private").forEach { priv ->
                                FilterChip(
                                    selected = privacy == priv,
                                    onClick = { privacy = priv },
                                    label = { Text(priv, fontSize = 10.sp) },
                                    enabled = !isUploading
                                )
                            }
                        }
                    }

                    // Progress indicator if uploading
                    if (isUploading) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            LinearProgressIndicator(
                                progress = { uploadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = StudioEmerald
                            )
                            Text(
                                text = uploadStatusText,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { startAutoUpload() },
                            enabled = !isUploading && title.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).testTag("confirm_upload_button")
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUploading) "Uploading..." else "AUTO UPLOAD")
                        }

                        OutlinedButton(
                            onClick = { shareDirectToApp() },
                            enabled = !isUploading,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open App", fontSize = 11.sp)
                        }
                    }
                } else {
                    // Success View
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StudioEmerald,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Published to $selectedPlatform!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Status updated to 'Published' in Content Calendar.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }

                if (!isUploading && !isCompleted) {
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
