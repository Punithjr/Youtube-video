package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.data.github.GitHubRepo
import com.example.data.github.GitHubRepository
import com.example.data.github.GitHubSyncResult
import com.example.data.github.GitHubUser
import com.example.i18n.I18n
import com.example.ui.components.VideoGenerationProgressIndicator
import com.example.ui.theme.StudioCrimson
import kotlinx.coroutines.launch

@Composable
fun GitHubLinkScreen(
    settings: CreatorSettingsEntity,
    currentLanguage: String,
    projects: List<ContentProjectEntity>,
    calendarTasks: List<CalendarTaskEntity>,
    savedScripts: List<SavedScriptEntity>,
    onSaveSettings: (CreatorSettingsEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isKn = currentLanguage == I18n.LANG_KN
    val gitHubRepoManager = remember { GitHubRepository() }

    var usernameInput by remember { mutableStateOf(settings.gitHubUsername) }
    var repoInput by remember { mutableStateOf(settings.gitHubRepo) }
    var branchInput by remember { mutableStateOf(settings.gitHubBranch) }
    var tokenInput by remember { mutableStateOf(settings.gitHubToken) }

    var isSyncing by remember { mutableStateOf(false) }
    var syncStatusText by remember { mutableStateOf("") }
    var syncResult by remember { mutableStateOf<GitHubSyncResult?>(null) }
    var userProfile by remember { mutableStateOf<GitHubUser?>(null) }
    var userRepos by remember { mutableStateOf<List<GitHubRepo>>(emptyList()) }
    var isLoadingProfile by remember { mutableStateOf(false) }

    // Initial load of profile & repos
    LaunchedEffect(settings.gitHubUsername) {
        isLoadingProfile = true
        gitHubRepoManager.fetchUserProfile(settings.gitHubUsername, settings.gitHubToken)
            .onSuccess { userProfile = it }
        gitHubRepoManager.fetchUserRepos(settings.gitHubUsername, settings.gitHubToken)
            .onSuccess { userRepos = it }
        isLoadingProfile = false
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open URL: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleSaveConfig() {
        val updated = settings.copy(
            isGitHubConnected = true,
            gitHubUsername = usernameInput.trim(),
            gitHubRepo = repoInput.trim(),
            gitHubBranch = branchInput.trim(),
            gitHubToken = tokenInput.trim()
        )
        onSaveSettings(updated)
        Toast.makeText(context, "GitHub configuration saved!", Toast.LENGTH_SHORT).show()
        scope.launch {
            isLoadingProfile = true
            gitHubRepoManager.fetchUserProfile(updated.gitHubUsername, updated.gitHubToken)
                .onSuccess { userProfile = it }
            gitHubRepoManager.fetchUserRepos(updated.gitHubUsername, updated.gitHubToken)
                .onSuccess { userRepos = it }
            isLoadingProfile = false
        }
    }

    fun handleSyncAllData() {
        scope.launch {
            isSyncing = true
            syncStatusText = "Generating creator repository package..."

            // Compile all app data into Markdown format
            val markdownBuilder = StringBuilder()
            markdownBuilder.append("# Creator Studio Backup & Data Sync\n")
            markdownBuilder.append("Auto-synced from Creator Studio Android App on ${java.util.Date()}\n\n")

            markdownBuilder.append("## Linked Creator Profile\n")
            markdownBuilder.append("- Channel: ${settings.channelName}\n")
            markdownBuilder.append("- Google Account: ${settings.googleAccountName} (${settings.youTubeEmail})\n")
            markdownBuilder.append("- YouTube Handle: ${settings.youTubeHandle}\n")
            markdownBuilder.append("- Instagram: ${settings.instagramHandle}\n\n")

            markdownBuilder.append("## Content Projects (${projects.size})\n")
            projects.forEach { p ->
                markdownBuilder.append("### ${p.title} [${p.format} - ${p.platform}]\n")
                markdownBuilder.append("- Status: ${p.status}\n")
                markdownBuilder.append("- Hook: ${p.hookText}\n")
                markdownBuilder.append("- Script: ${p.scriptText.take(200)}...\n\n")
            }

            markdownBuilder.append("## Content Calendar Tasks (${calendarTasks.size})\n")
            calendarTasks.forEach { t ->
                markdownBuilder.append("- [${t.status}] **${t.title}** (${t.platform} - ${t.scheduledDate} ${t.scheduledTime})\n")
            }

            val timestamp = System.currentTimeMillis()
            val fileName = "content_studio_sync_${timestamp}.md"
            val path = "creator-studio/$fileName"

            val result = gitHubRepoManager.syncProjectToRepo(
                owner = usernameInput.trim(),
                repo = repoInput.trim(),
                branch = branchInput.trim(),
                path = path,
                content = markdownBuilder.toString(),
                commitMessage = "feat: Auto-sync Creator Studio data ($fileName)",
                token = tokenInput.trim().ifBlank { null }
            )

            isSyncing = false
            syncResult = result

            if (result.success) {
                val now = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())
                onSaveSettings(settings.copy(gitHubLastSync = now))
                Toast.makeText(context, "App data synced to GitHub repository!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Sync notice: ${result.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("github_link_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF24292E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isKn) "ಗಿಟ್‌ಹಬ್ ಡೇಟಾ ಲಿಂಕ್" else "GitHub Data Link",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isKn) "ನಿಮ್ಮ ಅಪ್ಲಿಕೇಶನ್ ಯೋಜನೆಗಳು ಮತ್ತು ಸ್ಕ್ರಿಪ್ಟ್‌ಗಳನ್ನು GitHub ನೊಂದಿಗೆ ಸಿಂಕ್ ಮಾಡಿ" else "Sync video scripts, prompts & calendar data directly to your GitHub repo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (settings.isGitHubConnected) Color(0xFF2EA44F).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = if (settings.isGitHubConnected) "LINKED" else "UNLINKED",
                        color = if (settings.isGitHubConnected) Color(0xFF2EA44F) else MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Circular progress indicator during sync
        item {
            VideoGenerationProgressIndicator(
                isVisible = isSyncing,
                statusText = syncStatusText,
                stepText = "Pushing markdown packages & scripts to GitHub...",
                actionType = "upload",
                onCancel = { isSyncing = false }
            )
        }

        // Profile Details Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().testTag("github_profile_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24292E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userProfile?.name ?: settings.googleAccountName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "@${userProfile?.login ?: settings.gitHubUsername}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = { openUrl("https://github.com/${settings.gitHubUsername}") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("github_view_profile_button")
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Profile", fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = userProfile?.bio ?: settings.gitHubBio,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${userProfile?.publicRepos ?: settings.gitHubPublicRepos}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Public Repos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${userProfile?.followers ?: 42}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Followers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = settings.gitHubLastSync.take(10), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Last Synced", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("github_config_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Repository & Credentials",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("GitHub Username / Organization") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("github_username_input"),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = repoInput,
                            onValueChange = { repoInput = it },
                            label = { Text("Target Repository") },
                            leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                            modifier = Modifier.weight(1f).testTag("github_repo_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = branchInput,
                            onValueChange = { branchInput = it },
                            label = { Text("Branch") },
                            modifier = Modifier.width(95.dp).testTag("github_branch_input"),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        label = { Text("Personal Access Token (PAT) [Optional]") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        supportingText = { Text("Leave blank to test local simulation, or enter 'ghp_...' for live commits") },
                        modifier = Modifier.fillMaxWidth().testTag("github_token_input"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { handleSaveConfig() },
                            modifier = Modifier.weight(1f).testTag("github_save_config_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Config")
                        }

                        FilledTonalButton(
                            onClick = { openUrl("https://github.com/${usernameInput.trim()}/${repoInput.trim()}") },
                            modifier = Modifier.testTag("github_open_repo_button")
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Repo")
                        }
                    }
                }
            }
        }

        // Sync Action Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().testTag("github_sync_action_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Push App Data to GitHub",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Commits ${projects.size} projects, ${calendarTasks.size} tasks, and scripts",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = { handleSyncAllData() },
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                            modifier = Modifier.testTag("github_push_data_button")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Now")
                            }
                        }
                    }

                    if (syncResult != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (syncResult?.success == true) "✅ ${syncResult?.message}" else "❌ ${syncResult?.message}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (syncResult?.sha != null) {
                                    Text(
                                        text = "Commit SHA: ${syncResult?.sha}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Public Repositories List
        item {
            Text(
                text = "Your GitHub Repositories",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(userRepos) { repo ->
            val isCurrent = repo.name.equals(repoInput, ignoreCase = true)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isCurrent) 1.5.dp else 0.5.dp,
                        color = if (isCurrent) StudioCrimson else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        repoInput = repo.name
                        branchInput = repo.defaultBranch
                        handleSaveConfig()
                    }
                    .testTag("github_repo_item_${repo.name}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = repo.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StudioCrimson.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = StudioCrimson,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        if (!repo.description.isNullOrBlank()) {
                            Text(
                                text = repo.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(text = "⭐ ${repo.stargazersCount}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "🍴 ${repo.forksCount}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "🌿 ${repo.defaultBranch}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    IconButton(onClick = { openUrl(repo.htmlUrl) }) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "View Repo", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
