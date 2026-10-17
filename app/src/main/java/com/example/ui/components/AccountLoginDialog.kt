package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.CreatorSettingsEntity
import com.example.i18n.I18n
import com.example.ui.theme.*

@Composable
fun AccountLoginDialog(
    platform: String, // "YouTube", "Instagram", "Facebook"
    settings: CreatorSettingsEntity,
    currentLanguage: String,
    onSaveSettings: (CreatorSettingsEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isKn = currentLanguage == I18n.LANG_KN

    val (brandColor, brandIcon) = when (platform) {
        "YouTube" -> Pair(YouTubeRed, Icons.Default.PlayArrow)
        "Instagram" -> Pair(InstagramGradientPink, Icons.Default.CameraAlt)
        else -> Pair(FacebookBlue, Icons.Default.ThumbUp)
    }

    val isConnected = when (platform) {
        "YouTube" -> settings.isYouTubeConnected
        "Instagram" -> settings.isInstagramConnected
        else -> settings.isFacebookConnected
    }

    var handleInput by remember {
        mutableStateOf(
            when (platform) {
                "YouTube" -> settings.youTubeHandle
                "Instagram" -> settings.instagramHandle
                else -> settings.facebookPageName
            }
        )
    }

    var emailInput by remember {
        mutableStateOf(
            when (platform) {
                "YouTube" -> settings.youTubeEmail
                else -> "creator@example.com"
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("account_login_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(brandColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(brandIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "$platform Account Login",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isConnected) "● Connected & Authorized" else "○ Not Connected",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) StudioEmerald else StudioAmber
                        )
                    }
                }

                HorizontalDivider()

                if (isConnected) {
                    // Connected Profile Details
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Active Account Handle:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = handleInput, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            if (platform == "YouTube") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Google Email:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = emailInput, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Permissions:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Upload Video, Direct Publish, Read Analytics", fontSize = 10.sp, color = StudioEmerald, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val updated = when (platform) {
                                "YouTube" -> settings.copy(isYouTubeConnected = false)
                                "Instagram" -> settings.copy(isInstagramConnected = false)
                                else -> settings.copy(isFacebookConnected = false)
                            }
                            onSaveSettings(updated)
                            Toast.makeText(context, "Disconnected $platform account", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Disconnect $platform")
                    }
                } else {
                    // Connect Form
                    Text(
                        text = "Sign in to authorize auto-uploading and 1-click publishing for $platform:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = handleInput,
                        onValueChange = { handleInput = it },
                        label = { Text("$platform Handle / Channel Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (platform == "YouTube") {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Google Account Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            val updated = when (platform) {
                                "YouTube" -> settings.copy(
                                    isYouTubeConnected = true,
                                    youTubeHandle = handleInput.ifBlank { "@Creator" },
                                    youTubeEmail = emailInput.ifBlank { "creator@gmail.com" }
                                )
                                "Instagram" -> settings.copy(
                                    isInstagramConnected = true,
                                    instagramHandle = handleInput.ifBlank { "@creator_studio" }
                                )
                                else -> settings.copy(
                                    isFacebookConnected = true,
                                    facebookPageName = handleInput.ifBlank { "Creator Page" }
                                )
                            }
                            onSaveSettings(updated)
                            Toast.makeText(context, "$platform connected successfully! Ready to auto-upload.", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("confirm_login_button")
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In & Connect $platform", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(I18n.t("close", currentLanguage))
                }
            }
        }
    }
}
