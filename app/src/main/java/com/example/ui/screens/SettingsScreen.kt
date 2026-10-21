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

@Composable
fun SettingsScreen(
    settings: CreatorSettingsEntity,
    currentLanguage: String,
    onSaveSettings: (CreatorSettingsEntity) -> Unit,
    onOpenWizard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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

        // GitHub Data Link Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GITHUB DATA LINK CONFIGURATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    OutlinedTextField(
                        value = gitHubUsername,
                        onValueChange = { gitHubUsername = it },
                        label = { Text("GitHub Username / Org") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = gitHubRepo,
                        onValueChange = { gitHubRepo = it },
                        label = { Text("Target Sync Repository") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = gitHubToken,
                        onValueChange = { gitHubToken = it },
                        label = { Text("Personal Access Token (for remote commit)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { handleSave() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE GITHUB CONFIG", fontWeight = FontWeight.Bold)
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
}
