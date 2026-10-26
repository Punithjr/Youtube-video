package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarTaskEntity
import com.example.data.ContentProjectEntity
import com.example.data.CreatorSettingsEntity
import com.example.data.MediaItemEntity
import com.example.data.VideoMetadataEntity
import com.example.generator.ContentGenerator
import com.example.i18n.I18n
import com.example.ui.components.*
import com.example.ui.theme.StudioCrimson
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioPurple
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class EditableScene(
    val sceneNumber: Int,
    var title: String,
    var voiceover: String,
    var visualPrompt: String,
    var onScreenText: String,
    var durationSec: Int = 10,
    var bRollTag: String = "Cinematic Studio B-Roll",
    var captionSyncMs: Long = 0L
)

data class EditorStateSnapshot(
    val scenes: List<EditableScene>,
    val filter: VideoFilterPreset,
    val transition: VideoTransitionEffect,
    val trimStartMs: Long,
    val trimEndMs: Long
)

@Composable
fun VideoEditorScreen(
    currentLanguage: String,
    settings: CreatorSettingsEntity,
    videoDrafts: List<VideoMetadataEntity> = emptyList(),
    onSaveProject: (ContentProjectEntity) -> Unit,
    onSaveVideoDraft: (VideoMetadataEntity) -> Unit = {},
    onUpdateDraftStatus: (Long, String) -> Unit = { _, _ -> },
    onDeleteDraft: (VideoMetadataEntity) -> Unit = {},
    onScheduleTask: (CalendarTaskEntity) -> Unit,
    onSyncToGitHub: (String, String) -> Unit = { _, _ -> },
    onShowGlobalProgress: (title: String, subtitle: String, actionType: String, progress: Float?, onCancel: (() -> Unit)?) -> Unit = { _, _, _, _, _ -> },
    onUpdateGlobalProgress: (progress: Float?, subtitle: String?) -> Unit = { _, _ -> },
    onHideGlobalProgress: () -> Unit = {},
    onAddMediaToLibrary: (MediaItemEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val isKn = currentLanguage == I18n.LANG_KN

    var showDraftsDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showTransitionsSheet by remember { mutableStateOf(false) }
    var showTemplatesSheet by remember { mutableStateOf(false) }
    var showThumbnailDialog by remember { mutableStateOf(false) }

    var topic by remember { mutableStateOf("Top 5 AI Tools That Will Replace Creators in 2026") }
    var selectedFormat by remember { mutableStateOf("16:9 Long-Form") }
    var selectedTone by remember { mutableStateOf("Cinematic & Fast-Paced") }

    // Generation / Rendering state
    var isGenerating by remember { mutableStateOf(false) }
    var isRendering by remember { mutableStateOf(false) }
    var progressStatusText by remember { mutableStateOf("") }
    var progressStepText by remember { mutableStateOf("") }
    var currentProgressFraction by remember { mutableStateOf<Float?>(null) }
    var actionType by remember { mutableStateOf("generate") }

    // Video preview URI
    var previewVideoUri by remember {
        mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    }

    // Video Trimming state
    var videoDurationMs by remember { mutableLongStateOf(60000L) }
    var trimStartMs by remember { mutableLongStateOf(0L) }
    var trimEndMs by remember { mutableLongStateOf(60000L) }
    var isTrimPreviewActive by remember { mutableStateOf(false) }
    var currentPlaybackPositionMs by remember { mutableLongStateOf(0L) }

    // Filter, Transition & Safe Zone state
    var selectedFilter by remember { mutableStateOf(VideoFilterPreset.NONE) }
    var selectedTransition by remember { mutableStateOf(VideoTransitionEffect.DISSOLVE) }
    var transitionDurationSec by remember { mutableFloatStateOf(0.6f) }
    var selectedSafeZone by remember { mutableStateOf(SafeZoneType.NONE) }
    var selectedTemplateId by remember { mutableStateOf<String?>(null) }

    // Audio & Keyframes state
    var masterVolume by remember { mutableFloatStateOf(0.85f) }
    var fadeInSec by remember { mutableFloatStateOf(1.0f) }
    var fadeOutSec by remember { mutableFloatStateOf(1.5f) }
    var isAutoDuckingEnabled by remember { mutableStateOf(true) }

    // Multi-track active tab
    var selectedToolTab by remember { mutableIntStateOf(0) } // 0: Scenes, 1: Trimmer, 2: Color Grading, 3: Audio & Ducking, 4: Multi-Track

    // Editable scenes list
    var scenes by remember {
        mutableStateOf(
            listOf(
                EditableScene(
                    sceneNumber = 1,
                    title = "The Viral Hook",
                    voiceover = "If you are still creating content manually in 2026, you are already falling behind. Here is why.",
                    visualPrompt = "Hyper-realistic futuristic creator studio with glowing holographic AI timelines, 8k resolution, cinematic lighting",
                    onScreenText = "STOP DOING THIS IN 2026 🛑",
                    durationSec = 5,
                    bRollTag = "Holographic Timeline Close-Up",
                    captionSyncMs = 0L
                ),
                EditableScene(
                    sceneNumber = 2,
                    title = "The Problem",
                    voiceover = "Top channels are publishing 10 videos a week because automated generative pipelines do 90% of the heavy lifting.",
                    visualPrompt = "Close-up of high-speed timeline scrubbing with AI sound waves and video cut points appearing dynamically",
                    onScreenText = "10 Videos / Week Formula 📈",
                    durationSec = 10,
                    bRollTag = "Fast Editing Screen Capture",
                    captionSyncMs = 5000L
                ),
                EditableScene(
                    sceneNumber = 3,
                    title = "The AI Solution",
                    voiceover = "Here are the 3 workflows you need: automated script synthesis, dynamic B-roll tagging, and automatic caption hooks.",
                    visualPrompt = "Split screen showing 3 AI video engines rendering simultaneously with clean UI metrics",
                    onScreenText = "The 3 AI Workflows ⚡",
                    durationSec = 15,
                    bRollTag = "Triple Split-Screen Demonstration",
                    captionSyncMs = 15000L
                ),
                EditableScene(
                    sceneNumber = 4,
                    title = "Call to Action",
                    voiceover = "Hit subscribe and check the link below to get the complete production package and GitHub repository.",
                    visualPrompt = "Cinematic YouTube subscribe button animation with glowing crimson accents and creator profile badge",
                    onScreenText = "SUBSCRIBE & LINK IN BIO 🔔",
                    durationSec = 8,
                    bRollTag = "Subscribe CTA & Outro Card",
                    captionSyncMs = 30000L
                )
            )
        )
    }

    var selectedSceneIndex by remember { mutableIntStateOf(0) }

    // Undo / Redo Stack Manager
    val undoStack = remember { mutableStateListOf<EditorStateSnapshot>() }
    val redoStack = remember { mutableStateListOf<EditorStateSnapshot>() }

    fun captureSnapshot() {
        val current = EditorStateSnapshot(
            scenes = scenes.map { it.copy() },
            filter = selectedFilter,
            transition = selectedTransition,
            trimStartMs = trimStartMs,
            trimEndMs = trimEndMs
        )
        undoStack.add(current)
        redoStack.clear()
        if (undoStack.size > 20) {
            undoStack.removeAt(0)
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val last = undoStack.removeAt(undoStack.lastIndex)
            val current = EditorStateSnapshot(
                scenes = scenes.map { it.copy() },
                filter = selectedFilter,
                transition = selectedTransition,
                trimStartMs = trimStartMs,
                trimEndMs = trimEndMs
            )
            redoStack.add(current)

            scenes = last.scenes
            selectedFilter = last.filter
            selectedTransition = last.transition
            trimStartMs = last.trimStartMs
            trimEndMs = last.trimEndMs
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            Toast.makeText(context, "Undone last edit", Toast.LENGTH_SHORT).show()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            val current = EditorStateSnapshot(
                scenes = scenes.map { it.copy() },
                filter = selectedFilter,
                transition = selectedTransition,
                trimStartMs = trimStartMs,
                trimEndMs = trimEndMs
            )
            undoStack.add(current)

            scenes = next.scenes
            selectedFilter = next.filter
            selectedTransition = next.transition
            trimStartMs = next.trimStartMs
            trimEndMs = next.trimEndMs
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            Toast.makeText(context, "Redone edit", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleAiGenerateVideo() {
        captureSnapshot()
        scope.launch {
            isGenerating = true
            actionType = "generate"
            progressStatusText = "AI Engine: Writing script & analyzing viral hooks..."
            progressStepText = "Step 1 of 3: Natural language generation"
            currentProgressFraction = 0.25f
            onShowGlobalProgress(
                "AI Video Generation API Request",
                "Writing multi-scene script & generating storyboard...",
                "generate",
                0.25f
            ) {
                isGenerating = false
                currentProgressFraction = null
                onHideGlobalProgress()
            }
            delay(900)

            progressStatusText = "AI Engine: Creating scene-by-scene storyboard prompts..."
            progressStepText = "Step 2 of 3: Synthesizing visual cues & B-Roll"
            currentProgressFraction = 0.65f
            onUpdateGlobalProgress(0.65f, "Creating scene-by-scene storyboard prompts & B-roll visual cues...")
            delay(1000)

            val autoContent = ContentGenerator.generateAutoVideo(
                topic = topic,
                platform = if (selectedFormat.contains("9:16")) "Instagram" else "YouTube",
                language = currentLanguage,
                format = selectedFormat
            )

            val generatedScenes = mutableListOf<EditableScene>()
            generatedScenes.add(
                EditableScene(
                    sceneNumber = 1,
                    title = "Hook Scene",
                    voiceover = autoContent.script.lines().firstOrNull() ?: "Attention creators!",
                    visualPrompt = autoContent.visualPrompts.getOrElse(0) { "Cinematic studio shot" },
                    onScreenText = "MUST WATCH 🔥",
                    durationSec = 6,
                    bRollTag = "Viral Opening Hook Visual",
                    captionSyncMs = 0L
                )
            )
            generatedScenes.add(
                EditableScene(
                    sceneNumber = 2,
                    title = "Core Breakdown",
                    voiceover = autoContent.description.take(160),
                    visualPrompt = autoContent.visualPrompts.getOrElse(1) { "Engaging tutorial view" },
                    onScreenText = "The Secret Trick 💡",
                    durationSec = 12,
                    bRollTag = "Tutorial Action & Workflow Cues",
                    captionSyncMs = 6000L
                )
            )
            generatedScenes.add(
                EditableScene(
                    sceneNumber = 3,
                    title = "Call to Action",
                    voiceover = "Follow for daily creator automation tools and tips!",
                    visualPrompt = autoContent.visualPrompts.getOrElse(2) { "Subscribe and like button" },
                    onScreenText = "Follow for More! 🚀",
                    durationSec = 6,
                    bRollTag = "Animated Subscribe & Like Button",
                    captionSyncMs = 18000L
                )
            )

            scenes = generatedScenes
            selectedSceneIndex = 0

            progressStatusText = "Finalizing video timeline..."
            progressStepText = "Step 3 of 3: Preparing interactive player preview"
            currentProgressFraction = 1.0f
            onUpdateGlobalProgress(1.0f, "Preparing interactive VideoPlayerView preview...")
            delay(400)

            isGenerating = false
            currentProgressFraction = null
            onHideGlobalProgress()
            Toast.makeText(context, "AI Video Script & Timeline Generated!", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleRenderVideo() {
        scope.launch {
            isRendering = true
            actionType = "render"
            progressStatusText = "Rendering Timeline: Compiling ${scenes.size} video scenes..."
            progressStepText = "Rendering audio track, captions & transitions"
            currentProgressFraction = 0.3f
            onShowGlobalProgress(
                "Video Rendering Engine API",
                "Compiling ${scenes.size} timeline scenes & rendering audio...",
                "render",
                0.3f
            ) {
                isRendering = false
                currentProgressFraction = null
                onHideGlobalProgress()
            }
            delay(1200)

            progressStatusText = "Applying color grading and audio loudness normalization..."
            progressStepText = "Encoding MP4 (1080p 60fps)"
            currentProgressFraction = 0.75f
            onUpdateGlobalProgress(0.75f, "Encoding MP4 (1080p 60fps) & applying color grading...")
            delay(1100)

            progressStatusText = "Render complete! Loading into VideoPlayerView..."
            progressStepText = "Ready for playback preview"
            currentProgressFraction = 1.0f
            onUpdateGlobalProgress(1.0f, "Render complete! Loading into Media3 VideoPlayerView...")
            delay(500)

            previewVideoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            isRendering = false
            currentProgressFraction = null
            onHideGlobalProgress()
            Toast.makeText(context, "🎬 Video Rendered! Preview active in PlayerView.", Toast.LENGTH_LONG).show()
        }
    }

    fun handleSaveProject() {
        val totalSec = scenes.sumOf { it.durationSec }
        val project = ContentProjectEntity(
            title = topic,
            topic = topic,
            platform = if (selectedFormat.contains("9:16")) "Instagram" else "YouTube",
            format = selectedFormat,
            status = "Production",
            scriptText = scenes.joinToString("\n\n") { "Scene ${it.sceneNumber} (${it.title}): ${it.voiceover}" },
            hookText = scenes.firstOrNull()?.voiceover ?: "",
            ctaText = scenes.lastOrNull()?.voiceover ?: "",
            captionText = "Watch full video on $topic. Created with Creator Studio AI.",
            hashtagsText = "#CreatorStudio #VideoEditor #AI #ContentCreation",
            duration = "${totalSec}s"
        )
        onSaveProject(project)
        Toast.makeText(context, "Project saved to Content Studio!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("video_editor_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Title Bar with Undo / Redo & Pro Badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isKn) "ವೀಡಿಯೊ ಸಂಪಾದಕ ಮತ್ತು AI ಜನರೇಟರ್" else "Video Editor & AI Generator",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ExoPlayer preview, dual-trimmer, ColorMatrix filters & multi-track",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Undo Button
                    IconButton(
                        onClick = { undo() },
                        enabled = undoStack.isNotEmpty(),
                        modifier = Modifier.size(32.dp).testTag("editor_undo_button")
                    ) {
                        Icon(
                            Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (undoStack.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Redo Button
                    IconButton(
                        onClick = { redo() },
                        enabled = redoStack.isNotEmpty(),
                        modifier = Modifier.size(32.dp).testTag("editor_redo_button")
                    ) {
                        Icon(
                            Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (redoStack.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = { showDraftsDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("open_video_drafts_button")
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Drafts (${videoDrafts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Circular progress indicator during video generation or rendering
        item {
            VideoGenerationProgressIndicator(
                isVisible = isGenerating || isRendering,
                statusText = progressStatusText,
                stepText = progressStepText,
                progress = currentProgressFraction,
                actionType = actionType,
                onCancel = {
                    isGenerating = false
                    isRendering = false
                }
            )
        }

        // Primary Video Player Preview (Media3 PlayerView + ColorMatrix + Safe Zone)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Live Video Preview (Media3 PlayerView)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Safe Zone Guide Dropdown Chip
                Surface(
                    onClick = {
                        val next = when (selectedSafeZone) {
                            SafeZoneType.NONE -> SafeZoneType.SHORTS
                            SafeZoneType.SHORTS -> SafeZoneType.REELS
                            SafeZoneType.REELS -> SafeZoneType.TIKTOK
                            SafeZoneType.TIKTOK -> SafeZoneType.NONE
                        }
                        selectedSafeZone = next
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = if (selectedSafeZone != SafeZoneType.NONE) StudioPurple.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("toggle_safe_zone_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.CropFree,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (selectedSafeZone != SafeZoneType.NONE) StudioPurple else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedSafeZone == SafeZoneType.NONE) "Safe Zones: Off" else selectedSafeZone.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSafeZone != SafeZoneType.NONE) StudioPurple else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            VideoPlayerView(
                videoUri = previewVideoUri,
                title = topic,
                aspectRatio = if (selectedFormat.contains("9:16")) 9f / 16f else 16f / 9f,
                autoPlay = false,
                filterPreset = selectedFilter,
                safeZoneType = selectedSafeZone,
                trimBounds = if (isTrimPreviewActive) Pair(trimStartMs, trimEndMs) else null,
                masterVolume = masterVolume,
                fadeInSec = fadeInSec,
                fadeOutSec = fadeOutSec,
                sceneMarkers = scenes.map { "S${it.sceneNumber}" },
                onPositionChange = { pos -> currentPlaybackPositionMs = pos },
                onPickLocalVideo = { localUri ->
                    previewVideoUri = localUri
                    Toast.makeText(context, "Loaded local video file!", Toast.LENGTH_SHORT).show()
                },
                onSceneSelect = { index ->
                    if (index in scenes.indices) {
                        selectedSceneIndex = index
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("editor_video_player")
            )
        }

        // Quick Tools Toolbar: Trimmer, Transitions, Templates, Thumbnail Grabber, Export
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Templates button
                FilledTonalButton(
                    onClick = { showTemplatesSheet = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("open_templates_button")
                ) {
                    Icon(Icons.Default.DashboardCustomize, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Templates", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Transitions button
                FilledTonalButton(
                    onClick = { showTransitionsSheet = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("open_transitions_button")
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(selectedTransition.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Thumbnail Grabber
                FilledTonalButton(
                    onClick = { showThumbnailDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("grab_thumbnail_button")
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thumbnail", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Export Settings Menu
                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("open_export_dialog_button")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tool Tabs (Scenes, Trimmer, Color Filter, Audio Envelopes, Multi-Track)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedToolTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                listOf(
                    "🎬 Scenes (${scenes.size})",
                    "✂️ Trimmer",
                    "🎨 Filters",
                    "🔊 Audio & Ducking",
                    "🎚️ Multi-Track"
                ).forEachIndexed { idx, label ->
                    Tab(
                        selected = selectedToolTab == idx,
                        onClick = {
                            selectedToolTab = idx
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        text = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedToolTab == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // TOOL TAB 0: SCENES & AI GENERATOR
        if (selectedToolTab == 0) {
            // AI Generator Configuration Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().testTag("ai_video_generator_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = StudioCrimson, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Generator Prompt",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("Video Topic / Core Idea") },
                            modifier = Modifier.fillMaxWidth().testTag("editor_topic_input"),
                            singleLine = false,
                            maxLines = 2
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("16:9 Long-Form", "9:16 Shorts/Reels").forEach { fmt ->
                                val isSel = selectedFormat == fmt
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedFormat = fmt },
                                    label = { Text(fmt, fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (fmt.contains("9:16")) Icons.Default.Smartphone else Icons.Default.Tv,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { handleAiGenerateVideo() },
                                enabled = !isGenerating && !isRendering,
                                colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                                modifier = Modifier.weight(1f).testTag("editor_ai_generate_button")
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generating...")
                                } else {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Generate")
                                }
                            }

                            FilledTonalButton(
                                onClick = { handleRenderVideo() },
                                enabled = !isGenerating && !isRendering,
                                modifier = Modifier.weight(1f).testTag("editor_render_video_button")
                            ) {
                                if (isRendering) {
                                    CircularProgressIndicator(color = StudioCrimson, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Rendering...")
                                } else {
                                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Render Video")
                                }
                            }
                        }
                    }
                }
            }

            // Scene Timeline Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Timeline Scenes (${scenes.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total Duration: ${scenes.sumOf { it.durationSec }}s",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("editor_timeline_row")
                ) {
                    itemsIndexed(scenes) { index, scene ->
                        val isSelected = selectedSceneIndex == index
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) StudioCrimson else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .width(130.dp)
                                .clickable {
                                    selectedSceneIndex = index
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .testTag("scene_chip_$index")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Scene ${scene.sceneNumber}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = scene.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${scene.durationSec}s",
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Active Scene Detail Editor
            if (selectedSceneIndex in scenes.indices) {
                val activeScene = scenes[selectedSceneIndex]
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth().testTag("active_scene_editor_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Editing Scene ${activeScene.sceneNumber}: ${activeScene.title}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StudioCrimson.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${activeScene.durationSec} sec",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioCrimson,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = activeScene.voiceover,
                                onValueChange = { newVo ->
                                    captureSnapshot()
                                    scenes = scenes.mapIndexed { idx, s ->
                                        if (idx == selectedSceneIndex) s.copy(voiceover = newVo) else s
                                    }
                                },
                                label = { Text("Voiceover Script") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )

                            OutlinedTextField(
                                value = activeScene.onScreenText,
                                onValueChange = { newText ->
                                    scenes = scenes.mapIndexed { idx, s ->
                                        if (idx == selectedSceneIndex) s.copy(onScreenText = newText) else s
                                    }
                                },
                                label = { Text("On-Screen Caption / Hook Text") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = activeScene.visualPrompt,
                                onValueChange = { newPrompt ->
                                    scenes = scenes.mapIndexed { idx, s ->
                                        if (idx == selectedSceneIndex) s.copy(visualPrompt = newPrompt) else s
                                    }
                                },
                                label = { Text("Visual Prompt for AI Video Generator") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )

                            // Duration Slider
                            Text(
                                text = "Scene Duration: ${activeScene.durationSec}s",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = activeScene.durationSec.toFloat(),
                                onValueChange = { newDur ->
                                    scenes = scenes.mapIndexed { idx, s ->
                                        if (idx == selectedSceneIndex) s.copy(durationSec = newDur.toInt()) else s
                                    }
                                },
                                valueRange = 3f..30f,
                                steps = 26,
                                colors = SliderDefaults.colors(thumbColor = StudioCrimson, activeTrackColor = StudioCrimson),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // TOOL TAB 1: TRIMMER
        if (selectedToolTab == 1) {
            item {
                VideoTrimmerView(
                    videoDurationMs = videoDurationMs,
                    trimStartMs = trimStartMs,
                    trimEndMs = trimEndMs,
                    onTrimChange = { start, end ->
                        trimStartMs = start
                        trimEndMs = end
                    },
                    onPreviewTrim = {
                        isTrimPreviewActive = true
                        Toast.makeText(context, "Looping within trimmed boundaries", Toast.LENGTH_SHORT).show()
                    },
                    onResetTrim = {
                        trimStartMs = 0L
                        trimEndMs = videoDurationMs
                        isTrimPreviewActive = false
                    }
                )
            }
        }

        // TOOL TAB 2: COLOR GRADING & FILTERS
        if (selectedToolTab == 2) {
            item {
                VideoFilterSelectionPanel(
                    selectedFilter = selectedFilter,
                    onSelectFilter = { filter ->
                        captureSnapshot()
                        selectedFilter = filter
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )
            }
        }

        // TOOL TAB 3: AUDIO & DUCKING
        if (selectedToolTab == 3) {
            item {
                AudioKeyframeEditor(
                    fadeInSec = fadeInSec,
                    fadeOutSec = fadeOutSec,
                    masterVolume = masterVolume,
                    isAutoDuckingEnabled = isAutoDuckingEnabled,
                    onChangeFadeIn = { fadeInSec = it },
                    onChangeFadeOut = { fadeOutSec = it },
                    onChangeMasterVolume = { masterVolume = it },
                    onToggleAutoDucking = { isAutoDuckingEnabled = it }
                )
            }
        }

        // TOOL TAB 4: MULTI-TRACK TIMELINE
        if (selectedToolTab == 4) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().testTag("multi_track_timeline_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = StudioCrimson, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Multi-Track Timeline Mixer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("5 Active Layers", fontSize = 11.sp, color = StudioEmerald, fontWeight = FontWeight.Bold)
                        }

                        // Track 1: Primary Video
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎬 V1: Primary Clip", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                            Text("Main 1080p Interview Footage", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Track 2: B-Roll Overlay
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StudioPurple.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎞️ V2: B-Roll", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StudioPurple, modifier = Modifier.width(110.dp))
                            Text("Layered Hologram Cues & Cuts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Track 3: Voiceover Audio
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StudioEmerald.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎙️ A1: Voiceover", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StudioEmerald, modifier = Modifier.width(110.dp))
                            Text("100% Vol • Dynamic Speech Track", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Track 4: Background Music
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF38BDF8).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎵 A2: Music", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7), modifier = Modifier.width(110.dp))
                            Text("${(masterVolume * 100).toInt()}% Vol • Auto-Ducked (-12dB)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Track 5: Captions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF59E0B).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💬 T1: Captions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706), modifier = Modifier.width(110.dp))
                            Text("Auto-Synced Karaoke Kinetic Subtitles", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Action Buttons: Save Project, Save Draft, Schedule
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { handleSaveProject() },
                    modifier = Modifier.weight(1f).testTag("editor_save_project_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save Project", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val totalSec = scenes.sumOf { it.durationSec }
                        val draft = VideoMetadataEntity(
                            title = topic,
                            description = "Scenes: ${scenes.joinToString(" | ") { it.title }}",
                            topic = topic,
                            format = selectedFormat,
                            resolution = "1080p",
                            fps = 60,
                            durationSec = totalSec,
                            fileUri = previewVideoUri,
                            platform = if (selectedFormat.contains("9:16")) "Instagram" else "YouTube",
                            draftStatus = "Draft",
                            isDraft = true,
                            completionProgress = 0.5f,
                            aspectRatio = if (selectedFormat.contains("9:16")) "9:16" else "16:9"
                        )
                        onSaveVideoDraft(draft)
                    },
                    modifier = Modifier.weight(1f).testTag("editor_save_draft_button")
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save Draft", fontSize = 12.sp)
                }

                FilledTonalButton(
                    onClick = {
                        val task = CalendarTaskEntity(
                            title = topic,
                            platform = if (selectedFormat.contains("9:16")) "Instagram" else "YouTube",
                            contentType = if (selectedFormat.contains("9:16")) "Reel" else "Video",
                            status = "Scheduled",
                            scheduledDate = "2026-10-20",
                            scheduledTime = "18:00",
                            notes = "Generated with AI Video Editor (${scenes.size} scenes)"
                        )
                        onScheduleTask(task)
                        Toast.makeText(context, "Added to Content Calendar!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).testTag("editor_schedule_calendar_button")
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule", fontSize = 12.sp)
                }
            }
        }
    }

    // Dialogs & Sheets
    if (showDraftsDialog) {
        VideoDraftsManagerDialog(
            videoDrafts = videoDrafts,
            currentTopic = topic,
            currentFormat = selectedFormat,
            onSaveCurrentAsDraft = { draft -> onSaveVideoDraft(draft) },
            onLoadDraft = { draft ->
                topic = draft.topic.ifBlank { draft.title }
                selectedFormat = draft.format
                if (draft.fileUri.isNotBlank()) {
                    previewVideoUri = draft.fileUri
                }
                Toast.makeText(context, "Loaded draft: \"${draft.title}\"", Toast.LENGTH_SHORT).show()
            },
            onUpdateDraftStatus = onUpdateDraftStatus,
            onDeleteDraft = onDeleteDraft,
            onDismiss = { showDraftsDialog = false }
        )
    }

    if (showExportDialog) {
        VideoExportDialog(
            videoTitle = topic,
            videoUriOrUrl = previewVideoUri,
            onDismiss = { showExportDialog = false }
        )
    }

    if (showTransitionsSheet) {
        VideoTransitionBottomSheet(
            currentTransition = selectedTransition,
            transitionDurationSec = transitionDurationSec,
            onSelectTransition = { trans ->
                captureSnapshot()
                selectedTransition = trans
                Toast.makeText(context, "Applied ${trans.displayName} transition!", Toast.LENGTH_SHORT).show()
            },
            onChangeDuration = { transitionDurationSec = it },
            onDismiss = { showTransitionsSheet = false }
        )
    }

    if (showTemplatesSheet) {
        TemplatesBottomSheet(
            selectedPresetId = selectedTemplateId,
            onSelectPreset = { preset ->
                captureSnapshot()
                selectedTemplateId = preset.id
                // Apply layout preset headline to active scene
                if (selectedSceneIndex in scenes.indices) {
                    scenes = scenes.mapIndexed { idx, s ->
                        if (idx == selectedSceneIndex) s.copy(onScreenText = preset.defaultText) else s
                    }
                }
                Toast.makeText(context, "Applied ${preset.title} preset!", Toast.LENGTH_SHORT).show()
                showTemplatesSheet = false
            },
            onDismiss = { showTemplatesSheet = false }
        )
    }

    if (showThumbnailDialog) {
        ThumbnailGrabberDialog(
            currentTimestampMs = currentPlaybackPositionMs,
            defaultTopic = topic,
            onSaveThumbnail = { media ->
                onAddMediaToLibrary(media)
            },
            onDismiss = { showThumbnailDialog = false }
        )
    }
}
