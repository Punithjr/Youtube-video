package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creator_settings")
data class CreatorSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val youtubeChannelUrl: String = "https://www.youtube.com",
    val youtubeStudioUrl: String = "https://studio.youtube.com",
    val instagramUrl: String = "https://www.instagram.com",
    val facebookUrl: String = "https://www.facebook.com",
    val channelName: String = "My Creator Channel",
    val defaultLanguage: String = "en",
    val theme: String = "dark",
    val wizardCompleted: Boolean = false,
    val youtubeSubscribers: Int = 12500,
    val instagramFollowers: Int = 8400,
    val facebookFollowers: Int = 5100,
    val monthlyViews: Long = 184500,
    val engagementRate: Float = 6.4f,
    val isYouTubeConnected: Boolean = true,
    val youTubeEmail: String = "creator@gmail.com",
    val youTubeHandle: String = "@ProCreator",
    val isInstagramConnected: Boolean = true,
    val instagramHandle: String = "@creator_official",
    val isFacebookConnected: Boolean = true,
    val facebookPageName: String = "Pro Creator Community"
)

@Entity(tableName = "calendar_tasks")
data class CalendarTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val platform: String, // "YouTube", "Instagram", "Facebook", "All"
    val contentType: String, // "Video", "Short / Reel", "Post", "Story"
    val status: String = "Draft", // "Draft", "Scheduled", "Published"
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTime: String = "18:00",
    val notes: String = "",
    val projectId: Long? = null
)

@Entity(tableName = "content_projects")
data class ContentProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val topic: String,
    val platform: String = "YouTube",
    val format: String = "Video",
    val status: String = "Idea", // "Idea", "Script Ready", "Production", "Thumbnail Ready", "Ready to Publish", "Published"
    val scriptText: String = "",
    val hookText: String = "",
    val ctaText: String = "",
    val scenesJson: String = "",
    val captionText: String = "",
    val hashtagsText: String = "",
    val thumbnailTitle: String = "",
    val thumbnailStyle: String = "YouTube Viral",
    val thumbnailBadge: String = "NEW",
    val scheduledDate: String = "",
    val duration: String = "60s",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Videos", "Shorts", "Images", "Thumbnails", "Audio", "Scripts"
    val fileType: String,
    val uriOrPath: String,
    val sizeFormatted: String = "2.4 MB",
    val durationFormatted: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_scripts")
data class SavedScriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val topic: String,
    val platform: String,
    val language: String = "en",
    val duration: String = "60s",
    val tone: String = "Engaging",
    val hook: String = "",
    val bodyContent: String = "",
    val cta: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "auto_video_jobs")
data class AutoVideoJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val topic: String,
    val platform: String = "YouTube", // "YouTube", "Instagram", "Facebook", "All"
    val status: String = "Uploaded", // "Generating", "Rendered", "Uploaded", "Scheduled", "Failed"
    val scriptText: String = "",
    val description: String = "",
    val visualPrompt1: String = "",
    val visualPrompt2: String = "",
    val visualPrompt3: String = "",
    val format: String = "9:16 Shorts", // "9:16 Shorts", "16:9 Long-Form"
    val durationSec: Int = 60,
    val uploadId: String = "",
    val uploadUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prompt_templates")
data class PromptTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Viral Shorts", "10-Min Video", "Hooks", "YouTube SEO", "Captions", "Kannada Culture", "Visual Prompts", "Tech & AI"
    val templateText: String,
    val sampleTopic: String = "",
    val platform: String = "All",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val usageCount: Int = 0
)
