package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarTaskEntity
import com.example.data.ContentProjectEntity
import com.example.data.CreatorSettingsEntity
import com.example.data.VideoMetadataEntity
import com.example.generator.ContentGenerator
import com.example.i18n.I18n
import com.example.ui.components.VideoDraftsManagerDialog
import com.example.ui.components.VideoGenerationProgressIndicator
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.StudioCrimson
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class EditableScene(
    val sceneNumber: Int,
    var title: String,
    var voiceover: String,
    var visualPrompt: String,
    var onScreenText: String,
    var durationSec: Int = 10
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isKn = currentLanguage == I18n.LANG_KN

    var showDraftsDialog by remember { mutableStateOf(false) }
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
                    durationSec = 5
                ),
                EditableScene(
                    sceneNumber = 2,
                    title = "The Problem",
                    voiceover = "Top channels are publishing 10 videos a week because automated generative pipelines do 90% of the heavy lifting.",
                    visualPrompt = "Close-up of high-speed timeline scrubbing with AI sound waves and video cut points appearing dynamically",
                    onScreenText = "10 Videos / Week Formula 📈",
                    durationSec = 10
                ),
                EditableScene(
                    sceneNumber = 3,
                    title = "The AI Solution",
                    voiceover = "Here are the 3 workflows you need: automated script synthesis, dynamic B-roll tagging, and automatic caption hooks.",
                    visualPrompt = "Split screen showing 3 AI video engines rendering simultaneously with clean UI metrics",
                    onScreenText = "The 3 AI Workflows ⚡",
                    durationSec = 15
                ),
                EditableScene(
                    sceneNumber = 4,
                    title = "Call to Action",
                    voiceover = "Hit subscribe and check the link below to get the complete production package and GitHub repository.",
                    visualPrompt = "Cinematic YouTube subscribe button animation with glowing crimson accents and creator profile badge",
                    onScreenText = "SUBSCRIBE & LINK IN BIO 🔔",
                    durationSec = 8
                )
            )
        )
    }

    var selectedSceneIndex by remember { mutableIntStateOf(0) }

    fun handleAiGenerateVideo() {
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
                    durationSec = 6
                )
            )
            generatedScenes.add(
                EditableScene(
                    sceneNumber = 2,
                    title = "Core Breakdown",
                    voiceover = autoContent.description.take(160),
                    visualPrompt = autoContent.visualPrompts.getOrElse(1) { "Engaging tutorial view" },
                    onScreenText = "The Secret Trick 💡",
                    durationSec = 12
                )
            )
            generatedScenes.add(
                EditableScene(
                    sceneNumber = 3,
                    title = "Call to Action",
                    voiceover = "Follow for daily creator automation tools and tips!",
                    visualPrompt = autoContent.visualPrompts.getOrElse(2) { "Subscribe and like button" },
                    onScreenText = "Follow for More! 🚀",
                    durationSec = 6
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

            // Switch or update preview URL
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
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isKn) "ವೀಡಿಯೊ ಸಂಪಾದಕ ಮತ್ತು AI ಜನರೇಟರ್" else "Video Editor & AI Generator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isKn) "Media3 PlayerView ನೊಂದಿಗೆ ಲೈವ್ ಮುನ್ನೋಟ ಮತ್ತು AI ಟೈಮ್‌ಲೈನ್" else "Media3 PlayerView live preview, AI script generation & scene editor",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showDraftsDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("open_video_drafts_button")
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Drafts (${videoDrafts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioCrimson.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "STUDIO PRO",
                            color = StudioCrimson,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
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

        // Component 1: Media3 VideoPlayerView for previewing generated video
        item {
            Text(
                text = "Live Video Preview (Media3 PlayerView)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            VideoPlayerView(
                videoUri = previewVideoUri,
                title = topic,
                aspectRatio = if (selectedFormat.contains("9:16")) 9f / 16f else 16f / 9f,
                autoPlay = false,
                sceneMarkers = scenes.map { "S${it.sceneNumber}" },
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
                        // Format Selector
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
                            .clickable { selectedSceneIndex = index }
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

    if (showDraftsDialog) {
        VideoDraftsManagerDialog(
            videoDrafts = videoDrafts,
            currentTopic = topic,
            currentFormat = selectedFormat,
            onSaveCurrentAsDraft = { draft ->
                onSaveVideoDraft(draft)
            },
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
}

