package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AutoVideoJobEntity
import com.example.data.CalendarTaskEntity
import com.example.data.ContentProjectEntity
import com.example.data.CreatorSettingsEntity
import com.example.i18n.I18n
import com.example.ui.components.PlatformBadge
import com.example.ui.components.ProjectStatusBadge
import com.example.ui.components.QuickActionsBar
import com.example.ui.components.TaskStatusBadge
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    settings: CreatorSettingsEntity,
    currentLanguage: String,
    projects: List<ContentProjectEntity>,
    calendarTasks: List<CalendarTaskEntity>,
    autoVideoJobs: List<AutoVideoJobEntity> = emptyList(),
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isKn = currentLanguage == I18n.LANG_KN
    var previewVideoJob by remember { mutableStateOf<AutoVideoJobEntity?>(null) }

    fun openBrowserUrl(url: String, service: String) {
        val trimmed = url.trim()
        if (trimmed.isBlank() || trimmed == "https://") {
            Toast.makeText(context, "Configure $service URL in Settings", Toast.LENGTH_SHORT).show()
            onNavigate("settings")
            return
        }
        val formatted = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else trimmed
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formatted))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF831843)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = StudioAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isKn) "ಕ್ರಿಯೇಟರ್ ಕಂಟ್ರೋಲ್ ಸೆಂಟರ್" else "ALL-IN-ONE CREATOR STUDIO",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isKn) "ನಿಮ್ಮ ಕಂಟೆಂಟ್. ಒಂದೇ ಸ್ಟುಡಿಯೋ." else "YOUR CONTENT. ONE CREATOR STUDIO.",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isKn) "YouTube, Instagram ಮತ್ತು Facebook ವಿಷಯಗಳನ್ನು ಒಂದೇ ಸ್ಥಳದಿಂದ ನಿರ್ವಹಿಸಿ."
                            else "Create, organize, and manage YouTube, Instagram, and Facebook content from one central place.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onNavigate("create_video") },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_create_video_button")
                            ) {
                                Icon(Icons.Default.MovieCreation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isKn) "ವೀಡಿಯೊ" else "CREATE VIDEO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { onNavigate("shorts_reels") },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_create_short_button")
                            ) {
                                Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isKn) "ರೀಲ್ಸ್" else "CREATE SHORT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            FilledTonalButton(
                                onClick = { openBrowserUrl(settings.youtubeChannelUrl, "YouTube") },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("hero_my_channel_button")
                            ) {
                                Text(
                                    text = if (isKn) "ಚಾನೆಲ್" else "MY CHANNEL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onNavigate("manga_studio") },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_manga_studio_button")
                            ) {
                                Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isKn) "📖 ಮಂಗಾ ಸ್ಟುಡಿಯೋ AI" else "📖 MANGA STORY STUDIO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            FilledTonalButton(
                                onClick = { onNavigate("auto_generator") },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_auto_video_button")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isKn) "⚡ ಆಟೋ ವೀಡಿಯೊ" else "⚡ 10-MIN AUTO VIDEO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Three Large Platform Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // YouTube Card
                PlatformOverviewCard(
                    title = "YouTube",
                    subtitle = if (isKn) "ಚಾನೆಲ್ & ಸ್ಟುಡಿಯೋ" else "Channel & Studio",
                    color = YouTubeRed,
                    icon = Icons.Default.PlayArrow,
                    statText = "${settings.youtubeSubscribers / 1000}k Subs",
                    buttonText = if (isKn) "ಚಾನೆಲ್ ತೆರೆಯಿರಿ" else "Open Channel",
                    onOpen = { openBrowserUrl(settings.youtubeChannelUrl, "YouTube") },
                    testTag = "platform_card_youtube",
                    modifier = Modifier.weight(1f)
                )

                // Instagram Card
                PlatformOverviewCard(
                    title = "Instagram",
                    subtitle = if (isKn) "ರೀಲ್ಸ್ & ಪ್ರೊಫೈಲ್" else "Reels & Feed",
                    color = InstagramGradientPink,
                    icon = Icons.Default.CameraAlt,
                    statText = "${settings.instagramFollowers / 1000}k Followers",
                    buttonText = if (isKn) "Instagram ತೆರೆಯಿರಿ" else "Open Instagram",
                    onOpen = { openBrowserUrl(settings.instagramUrl, "Instagram") },
                    testTag = "platform_card_instagram",
                    modifier = Modifier.weight(1f)
                )

                // Facebook Card
                PlatformOverviewCard(
                    title = "Facebook",
                    subtitle = if (isKn) "ಪೇಜ್ & ಸಮುದಾಯ" else "Page & Videos",
                    color = FacebookBlue,
                    icon = Icons.Default.ThumbUp,
                    statText = "${settings.facebookFollowers / 1000}k Followers",
                    buttonText = if (isKn) "Facebook ತೆರೆಯಿರಿ" else "Open Facebook",
                    onOpen = { openBrowserUrl(settings.facebookUrl, "Facebook") },
                    testTag = "platform_card_facebook",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. 10-Minute Auto Video Generator & Direct Upload Spotlight Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate("auto_generator") }
                    .testTag("dashboard_10min_auto_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(StudioCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isKn) "10-ನಿಮಿಷದ ಆಟೋ ವೀಡಿಯೊ ಜನರೇಟರ್" else "10-MIN AUTO VIDEO GENERATOR",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StudioAmber)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("AUTO UPLOAD", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                            Text(
                                text = if (isKn) "10 ದೃಶ್ಯಗಳು, ಟೈಮ್‌ಸ್ಟ್ಯಾಂಪ್‌ಗಳು, B-Roll ಮತ್ತು YouTube/IG/FB ಗೆ ನೇರ ಅಪ್‌ಲೋಡ್"
                                else "Automated scheduler, Gemini script engine, 3 visual prompts & direct upload to YouTube/FB/IG",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { onNavigate("auto_generator") }) {
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = "Open", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 4. Quick Actions Bar (Mandated in user request for one-click access to Script, Caption, Hashtags, and GitHub Sync)
        item {
            QuickActionsBar(
                currentLanguage = currentLanguage,
                onOpenScriptGenerator = { onNavigate("script_gen") },
                onOpenCaptionGenerator = { onNavigate("instagram") },
                onOpenHashtagGenerator = { onNavigate("ai_tools") },
                onOpenGitHubSync = { onNavigate("github_link") }
            )
        }

        // GitHub Data Link Card on Dashboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("github_link") }
                    .testTag("dashboard_github_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF24292E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isKn) "ಗಿಟ್‌ಹಬ್ ಡೇಟಾ ಲಿಂಕ್" else "GITHUB DATA LINK",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF2EA44F).copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("SYNC READY", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF2EA44F))
                                }
                            }
                            Text(
                                text = "Repo: ${settings.gitHubUsername}/${settings.gitHubRepo} (${settings.gitHubBranch}) • Last synced: ${settings.gitHubLastSync}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { onNavigate("github_link") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("dashboard_open_github_sync")
                    ) {
                        Text(if (isKn) "ಸಿಂಕ್ ಮಾಡಿ" else "Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Live DB Metrics Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryCard(
                    label = if (isKn) "ಒಟ್ಟು ಪ್ರಾಜೆಕ್ಟ್‌ಗಳು" else "Total Projects",
                    value = "${projects.size}",
                    color = StudioCrimson,
                    icon = Icons.Default.Folder,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    label = if (isKn) "ನಿಗದಿತ ಕಂಟೆಂಟ್" else "Scheduled Posts",
                    value = "${calendarTasks.count { it.status.equals("Scheduled", ignoreCase = true) }}",
                    color = StudioAmber,
                    icon = Icons.Default.Schedule,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    label = if (isKn) "ಪ್ರಕಟಿಸಲಾಗಿದೆ" else "Published",
                    value = "${calendarTasks.count { it.status.equals("Published", ignoreCase = true) }}",
                    color = StudioEmerald,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Dashboard Studio Tools (12 Cards)
        item {
            Text(
                text = if (isKn) "ಸ್ಟುಡಿಯೋ ಪರಿಕರಗಳು (Studio Tools)" else "STUDIO TOOLS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Studio Tool Grid Cards
        item {
            val toolCards = listOf(
                StudioTool("manga_studio", if (isKn) "📖 ಮಂಗಾ ಸ್ಟುಡಿಯೋ AI" else "📖 Manga Story Studio", "Manga stories, chapters, audio, video & slides", StudioCrimson, Icons.Default.AutoStories),
                StudioTool("auto_generator", if (isKn) "⚡ ಆಟೋ ವೀಡಿಯೊ" else "⚡ 10-Min Auto Video", "Automated scheduler, prompts & auto-upload", StudioCrimson, Icons.Default.Bolt),
                StudioTool("video_editor", if (isKn) "🎬 ವೀಡಿಯೊ ಎಡಿಟರ್ & AI" else "🎬 Video Editor & AI", "Media3 PlayerView live preview, AI timeline", StudioCrimson, Icons.Default.MovieFilter),
                StudioTool("github_link", if (isKn) "🐙 ಗಿಟ್‌ಹಬ್ ಡೇಟಾ ಲಿಂಕ್" else "🐙 GitHub Data Link", "Sync app projects, scripts & calendar to Git", Color(0xFF24292E), Icons.Default.Code),
                StudioTool("create_video", I18n.t("card_create_video", currentLanguage), "16:9 Long-Form, scene breakdown, music", StudioPurple, Icons.Default.MovieCreation),
                StudioTool("shorts_reels", I18n.t("card_shorts_reels", currentLanguage), "9:16 Vertical, hook retention, 30s/60s", StudioCyan, Icons.Default.Smartphone),
                StudioTool("thumbnail_creator", I18n.t("card_thumbnail", currentLanguage), "Visual Canvas preview, badges, export", StudioAmber, Icons.Default.Image),
                StudioTool("script_gen", I18n.t("card_script", currentLanguage), "Kannada & English viral script generator", StudioCyan, Icons.Default.Create),
                StudioTool("instagram", I18n.t("card_caption", currentLanguage), "Captions for YT, IG, FB with emojis", Color(0xFFEC4899), Icons.Default.EditNote),
                StudioTool("ai_tools", I18n.t("card_hashtags", currentLanguage), "Trending & high-reach tags generator", Color(0xFF10B981), Icons.Default.Tag),
                StudioTool("calendar", I18n.t("card_calendar", currentLanguage), "Status badges, search, CSV export", Color(0xFF3B82F6), Icons.Default.CalendarMonth),
                StudioTool("media", I18n.t("card_media", currentLanguage), "Organize clips, audio, covers, scripts", Color(0xFF8B5CF6), Icons.Default.PhotoLibrary),
                StudioTool("analytics", I18n.t("card_analytics", currentLanguage), "Views, followers, performance track", Color(0xFFF97316), Icons.Default.BarChart),
                StudioTool("social_channels", I18n.t("card_channels", currentLanguage), "Social profile links & handle tracker", Color(0xFF06B6D4), Icons.Default.Share),
                StudioTool("projects", I18n.t("card_publish", currentLanguage), "Projects, multi-platform preview", Color(0xFF14B8A6), Icons.Default.Publish),
                StudioTool("settings", I18n.t("card_settings", currentLanguage), "Channel URLs, language, studio theme", Color(0xFF64748B), Icons.Default.Settings)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in toolCards.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (tool in row) {
                            StudioToolCard(
                                tool = tool,
                                onClick = { onNavigate(tool.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 6. Upcoming Calendar Items with Visual Status Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isKn) "ಮುಂಬರುವ ಕಂಟೆಂಟ್ (Upcoming Schedule)" else "UPCOMING CONTENT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { onNavigate("calendar") }) {
                    Text(text = if (isKn) "ಕ್ಯಾಲೆಂಡರ್ ತೆರೆಯಿರಿ" else "View All Calendar", fontSize = 12.sp)
                }
            }
        }

        items(calendarTasks.take(4)) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("calendar") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PlatformBadge(platform = task.platform)
                            Text(
                                text = "${task.scheduledDate} • ${task.scheduledTime}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TaskStatusBadge(status = task.status)
                }
            }
        }

        // 7. Recent Projects
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isKn) "ಇತ್ತೀಚಿನ ಪ್ರಾಜೆಕ್ಟ್‌ಗಳು (Recent Projects)" else "RECENT PROJECTS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { onNavigate("projects") }) {
                    Text(text = if (isKn) "ಎಲ್ಲವೂ ನೋಡಿ" else "View Projects", fontSize = 12.sp)
                }
            }
        }

        items(projects.take(3)) { project ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("projects") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${project.platform} • ${project.format} • ${project.topic}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ProjectStatusBadge(status = project.status)
                }
            }
        }

        // 8. Previously Generated Videos & Past Content Feed (MongoDB & AI Generator Tracking)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(StudioCrimson)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isKn) "ಹಿಂದೆ ರಚಿಸಲಾದ ವೀಡಿಯೊಗಳು (Past Generated Content)"
                        else "PREVIOUSLY GENERATED VIDEOS (${autoVideoJobs.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                TextButton(onClick = { onNavigate("auto_generator") }) {
                    Text(text = if (isKn) "ಎಲ್ಲಾ ವೀಡಿಯೊಗಳು" else "View All / Generator", fontSize = 12.sp)
                }
            }
        }

        if (autoVideoJobs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("empty_past_content_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MovieFilter, contentDescription = null, tint = StudioCrimson, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isKn) "ಇನ್ನೂ ಯಾವುದೇ ವೀಡಿಯೊ ರಚಿಸಲಾಗಿಲ್ಲ" else "No Videos Generated Yet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isKn) "ವೀಡಿಯೊ ರಚಿಸಲು 'ವೀಡಿಯೊ ಎಡಿಟರ್' ಅಥವಾ '10-ನಿಮಿಷದ ಆಟೋ ವೀಡಿಯೊ' ಬಳಸಿ"
                            else "Click 'Video Editor & AI' or '10-Min Auto Video' above to generate your first video.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(autoVideoJobs.take(5)) { job ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { previewVideoJob = job }
                    .testTag("dashboard_past_video_${job.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                text = job.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${job.platform} • ${job.format} • ${job.durationSec}s",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (job.description.isNotBlank()) {
                            Text(
                                text = job.description.take(80) + "...",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (job.status == "Uploaded") Color(0xFF2EA44F).copy(alpha = 0.15f) else StudioCrimson.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = job.status.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (job.status == "Uploaded") Color(0xFF2EA44F) else StudioCrimson,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { previewVideoJob = job },
                            modifier = Modifier.size(32.dp).testTag("preview_job_${job.id}")
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = "Preview", tint = StudioCrimson, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal to preview previously generated video with VideoPlayerView
    if (previewVideoJob != null) {
        val job = previewVideoJob!!
        Dialog(onDismissRequest = { previewVideoJob = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().testTag("dashboard_video_preview_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = job.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "${job.platform} • ${job.format} • Status: ${job.status}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { previewVideoJob = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    VideoPlayerView(
                        videoUri = if (job.uploadUrl.startsWith("http") && job.uploadUrl.endsWith(".mp4")) job.uploadUrl else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        title = job.title,
                        aspectRatio = if (job.format.contains("9:16")) 9f / 16f else 16f / 9f,
                        autoPlay = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (job.scriptText.isNotBlank()) {
                        Text(
                            text = "Generated AI Script:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = job.scriptText.take(180) + if (job.scriptText.length > 180) "..." else "",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (job.uploadUrl.isNotBlank()) {
                        Button(
                            onClick = { openBrowserUrl(job.uploadUrl, job.platform) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open on ${job.platform}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class StudioTool(
    val id: String,
    val title: String,
    val desc: String,
    val accent: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun StudioToolCard(
    tool: StudioTool,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("tool_card_${tool.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tool.accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tool.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tool.desc,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun PlatformOverviewCard(
    title: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    statText: String,
    buttonText: String,
    onOpen: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = statText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onOpen,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun MetricSummaryCard(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
