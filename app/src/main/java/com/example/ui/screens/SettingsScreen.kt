package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.data.CreatorSettingsEntity
import com.example.i18n.I18n
import com.example.ui.theme.*

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalClipboardManager

@Composable
fun SettingsScreen(
    settings: CreatorSettingsEntity,
    currentLanguage: String,
    onSaveSettings: (CreatorSettingsEntity) -> Unit,
    onOpenWizard: () -> Unit,
    onRevokeGitHubPAT: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isKn = currentLanguage == I18n.LANG_KN

    var channelName by remember(settings) { mutableStateOf(settings.channelName) }
    var ytUrl by remember(settings) { mutableStateOf(settings.youtubeChannelUrl) }
    var ytStudioUrl by remember(settings) { mutableStateOf(settings.youtubeStudioUrl) }
    var igUrl by remember(settings) { mutableStateOf(settings.instagramUrl) }
    var fbUrl by remember(settings) { mutableStateOf(settings.facebookUrl) }
    var googleAccountName by remember(settings) { mutableStateOf(settings.googleAccountName) }
    var youTubeEmail by remember(settings) { mutableStateOf(settings.youTubeEmail) }
    var youTubeHandle by remember(settings) { mutableStateOf(settings.youTubeHandle) }
    var gitHubUsername by remember(settings) { mutableStateOf(settings.gitHubUsername) }
    var gitHubRepo by remember(settings) { mutableStateOf(settings.gitHubRepo) }
    var gitHubToken by remember(settings) { mutableStateOf(settings.gitHubToken) }
    var isTokenMasked by remember { mutableStateOf(true) }
    var showRevokeConfirmation by remember { mutableStateOf(false) }
    var selectedLang by remember(settings) { mutableStateOf(settings.defaultLanguage) }
    var selectedTheme by remember(settings) { mutableStateOf(settings.theme) }

    fun handleSave() {
        val updated = settings.copy(
            channelName = channelName,
            youtubeChannelUrl = ytUrl,
            youtubeStudioUrl = ytStudioUrl,
            instagramUrl = igUrl,
            facebookUrl = fbUrl,
            googleAccountName = googleAccountName,
            youTubeEmail = youTubeEmail,
            youTubeHandle = youTubeHandle,
            gitHubUsername = gitHubUsername,
            gitHubRepo = gitHubRepo,
            gitHubToken = gitHubToken,
            defaultLanguage = selectedLang,
            theme = selectedTheme
        )
        onSaveSettings(updated)
        Toast.makeText(context, "Settings saved! Links updated.", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = I18n.t("nav_settings", currentLanguage),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isKn) "ಸಾಮಾಜಿಕ ಮಾಧ್ಯಮ URLಗಳು, ಭಾಷೆ ಮತ್ತು ಆದ್ಯತೆಗಳನ್ನು ನಿರ್ವಹಿಸಿ" else "Channel URLs, profile destinations, language & studio preferences",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Social Media URL Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SOCIAL MEDIA CHANNEL LINKS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = StudioCrimson
                    )

                    OutlinedTextField(
                        value = channelName,
                        onValueChange = { channelName = it },
                        label = { Text("Channel / Brand Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ytUrl,
                        onValueChange = { ytUrl = it },
                        label = { Text("YouTube Channel URL") },
                        leadingIcon = { Icon(Icons.Default.PlayCircle, contentDescription = null, tint = YouTubeRed) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_youtube_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ytStudioUrl,
                        onValueChange = { ytStudioUrl = it },
                        label = { Text("YouTube Studio URL") },
                        leadingIcon = { Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = YouTubeRed) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_youtube_studio_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = igUrl,
                        onValueChange = { igUrl = it },
                        label = { Text("Instagram Profile URL") },
                        leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = InstagramGradientPink) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_instagram_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = fbUrl,
                        onValueChange = { fbUrl = it },
                        label = { Text("Facebook Page / Profile URL") },
                        leadingIcon = { Icon(Icons.Default.ThumbUp, contentDescription = null, tint = FacebookBlue) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_facebook_input"),
                        singleLine = true
                    )

                    Button(
                        onClick = { handleSave() },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_settings_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(I18n.t("save", currentLanguage).uppercase(), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Google Profile & YouTube Account Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "GOOGLE & YOUTUBE PROFILE LINK DETAILS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = YouTubeRed
                    )

                    OutlinedTextField(
                        value = googleAccountName,
                        onValueChange = { googleAccountName = it },
                        label = { Text("Google Account Name") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = youTubeEmail,
                        onValueChange = { youTubeEmail = it },
                        label = { Text("Google Account Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = youTubeHandle,
                        onValueChange = { youTubeHandle = it },
                        label = { Text("YouTube Channel Handle") },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // GitHub Data Link Card with Security Mask & Revoke
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings_github_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GITHUB DATA LINK CONFIGURATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Mask Security Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isTokenMasked) Color(0xFF2EA44F).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTokenMasked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (isTokenMasked) Color(0xFF2EA44F) else Color(0xFFF59E0B),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTokenMasked) "MASKED" else "VISIBLE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTokenMasked) Color(0xFF2EA44F) else Color(0xFFF59E0B)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = gitHubUsername,
                        onValueChange = { gitHubUsername = it },
                        label = { Text("GitHub Username / Org") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_github_username_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = gitHubRepo,
                        onValueChange = { gitHubRepo = it },
                        label = { Text("Target Sync Repository") },
                        leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("settings_github_repo_input"),
                        singleLine = true
                    )

                    // Stored GitHub PAT Field with Mask / Unmask Toggle
                    OutlinedTextField(
                        value = gitHubToken,
                        onValueChange = { gitHubToken = it },
                        label = { Text("Stored GitHub Personal Access Token (PAT)") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Key,
                                contentDescription = null,
                                tint = if (gitHubToken.isNotBlank()) Color(0xFF2EA44F) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { isTokenMasked = !isTokenMasked },
                                    modifier = Modifier.testTag("settings_github_token_mask_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (isTokenMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (isTokenMasked) "Reveal Token" else "Mask Token",
                                        tint = if (isTokenMasked) MaterialTheme.colorScheme.primary else Color(0xFFF59E0B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                if (gitHubToken.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            gitHubToken = ""
                                            Toast.makeText(context, "Token field cleared", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.testTag("settings_github_clear_token_btn")
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear Token", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        },
                        visualTransformation = if (isTokenMasked) PasswordVisualTransformation() else VisualTransformation.None,
                        supportingText = {
                            if (gitHubToken.isNotBlank()) {
                                Text(
                                    text = if (isTokenMasked) "🔒 Token is securely masked when not in use" else "⚠️ Plaintext visible • Mask before sharing screen",
                                    color = if (isTokenMasked) Color(0xFF2EA44F) else Color(0xFFF59E0B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text("No PAT currently configured. Add token to sync remote repositories.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("settings_github_token_input"),
                        singleLine = true
                    )

                    // Dedicated Security Setting: Mask Token Toggle Switch
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mask PAT When Not in Use",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Obscures characters with security dots to prevent shoulder-surfing and accidental exposure",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isTokenMasked,
                                onCheckedChange = { isTokenMasked = it },
                                modifier = Modifier.testTag("settings_github_token_mask_switch")
                            )
                        }
                    }

                    // Quick Actions Row: Paste & Demo Token
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    gitHubToken = clip.trim()
                                    Toast.makeText(context, "Pasted token into settings!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Clipboard empty. Copy your GitHub PAT first.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("settings_github_paste_token_btn")
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste Token", fontSize = 11.sp)
                        }

                        FilledTonalButton(
                            onClick = {
                                gitHubToken = "ghp_creator_studio_punithjr2_token"
                                Toast.makeText(context, "Loaded test PAT for testing!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("settings_github_demo_token_btn")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Demo Token", fontSize = 11.sp)
                        }
                    }

                    // Save Button
                    Button(
                        onClick = { handleSave() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("settings_save_github_config_button")
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE GITHUB CONFIG", fontWeight = FontWeight.Bold)
                    }

                    // Revoke Stored PAT Button (Requirement 9)
                    if (gitHubToken.isNotBlank() || settings.isGitHubConnected) {
                        OutlinedButton(
                            onClick = { showRevokeConfirmation = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("settings_github_revoke_token_button")
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("REVOKE STORED GITHUB PAT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Language & Theme Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "STUDIO PREFERENCES", fontSize = 12.sp, fontWeight = FontWeight.Black)

                    Text(text = "Language / ಭಾಷೆ:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedLang == I18n.LANG_KN,
                            onClick = {
                                selectedLang = I18n.LANG_KN
                                handleSave()
                            },
                            label = { Text("ಕನ್ನಡ (Kannada)") }
                        )
                        FilterChip(
                            selected = selectedLang == I18n.LANG_EN,
                            onClick = {
                                selectedLang = I18n.LANG_EN
                                handleSave()
                            },
                            label = { Text("English") }
                        )
                    }

                    Text(text = "Appearance Theme:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedTheme == "dark",
                            onClick = {
                                selectedTheme = "dark"
                                handleSave()
                            },
                            label = { Text("Dark Theme 🌙") }
                        )
                        FilterChip(
                            selected = selectedTheme == "light",
                            onClick = {
                                selectedTheme = "light"
                                handleSave()
                            },
                            label = { Text("Light Theme ☀️") }
                        )
                    }
                }
            }
        }

        // Wizard & API Integration Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "FIRST-RUN WIZARD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Need to re-run the Welcome Setup onboarding wizard?",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = onOpenWizard,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Re-open Welcome Wizard")
                    }

                    HorizontalDivider()

                    Text(text = "API & PUBLISHING STATUS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "• Manual Direct Links: Ready & Functional\n• Room SQLite Database: Active & Persisted\n• OAuth Direct Publish: Connect in Secrets panel for live token ingestion.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showRevokeConfirmation) {
        AlertDialog(
            onDismissRequest = { showRevokeConfirmation = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Revoke Stored GitHub PAT?") },
            text = {
                Text("This will immediately delete your GitHub Personal Access Token from local secure storage, disconnect the GitHub sync integration, and clear all cached repository metadata.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRevokeConfirmation = false
                        gitHubToken = ""
                        onRevokeGitHubPAT()
                        val updated = settings.copy(
                            gitHubToken = "",
                            isGitHubConnected = false,
                            gitHubLastSync = "Token Revoked"
                        )
                        onSaveSettings(updated)
                        Toast.makeText(context, "GitHub PAT revoked and cache cleared.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("settings_github_confirm_revoke_dialog")
                ) {
                    Text("Revoke Token & Clear Cache")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRevokeConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

