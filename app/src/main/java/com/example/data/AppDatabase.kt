package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CreatorSettingsEntity::class,
        CalendarTaskEntity::class,
        ContentProjectEntity::class,
        MediaItemEntity::class,
        SavedScriptEntity::class,
        AutoVideoJobEntity::class,
        PromptTemplateEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun calendarTaskDao(): CalendarTaskDao
    abstract fun contentProjectDao(): ContentProjectDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun savedScriptDao(): SavedScriptDao
    abstract fun autoVideoJobDao(): AutoVideoJobDao
    abstract fun promptTemplateDao(): PromptTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "creator_studio.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate initial starter data
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            prepopulate(database)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulate(db: AppDatabase) {
            val settingsDao = db.settingsDao()
            val calendarDao = db.calendarTaskDao()
            val projectDao = db.contentProjectDao()
            val mediaDao = db.mediaItemDao()
            val scriptDao = db.savedScriptDao()

            // Default Settings
            settingsDao.insertOrUpdate(
                CreatorSettingsEntity(
                    id = 1,
                    youtubeChannelUrl = "https://www.youtube.com",
                    youtubeStudioUrl = "https://studio.youtube.com",
                    instagramUrl = "https://www.instagram.com",
                    facebookUrl = "https://www.facebook.com",
                    channelName = "Pro Creator Hub",
                    defaultLanguage = "en",
                    theme = "dark",
                    wizardCompleted = false,
                    youtubeSubscribers = 24500,
                    instagramFollowers = 18200,
                    facebookFollowers = 9800,
                    monthlyViews = 340000,
                    engagementRate = 7.8f
                )
            )

            // Starter Calendar Tasks with Draft, Scheduled, Published
            calendarDao.insertTask(
                CalendarTaskEntity(
                    title = "Viral Editing Workflow in 2026",
                    platform = "YouTube",
                    contentType = "Video",
                    status = "Scheduled",
                    scheduledDate = "2026-10-08",
                    scheduledTime = "17:00",
                    notes = "Hook: 3 mistakes killing your watch time. 16:9 ratio."
                )
            )
            calendarDao.insertTask(
                CalendarTaskEntity(
                    title = "Behind the Scenes Studio Tour",
                    platform = "Instagram",
                    contentType = "Reel",
                    status = "Draft",
                    scheduledDate = "2026-10-09",
                    scheduledTime = "12:30",
                    notes = "Fast cuts with trending audio and sticker CTA."
                )
            )
            calendarDao.insertTask(
                CalendarTaskEntity(
                    title = "My Biggest Creator Milestone Ever!",
                    platform = "Facebook",
                    contentType = "Post",
                    status = "Published",
                    scheduledDate = "2026-10-05",
                    scheduledTime = "19:00",
                    notes = "Photo gallery with community appreciation message."
                )
            )
            calendarDao.insertTask(
                CalendarTaskEntity(
                    title = "5 Camera Settings You Must Turn Off",
                    platform = "YouTube",
                    contentType = "Short",
                    status = "Draft",
                    scheduledDate = "2026-10-10",
                    scheduledTime = "15:00",
                    notes = "Vertical 9:16 format with auto captions."
                )
            )
            calendarDao.insertTask(
                CalendarTaskEntity(
                    title = "Weekly Creator Q&A Live Session",
                    platform = "YouTube",
                    contentType = "Video",
                    status = "Scheduled",
                    scheduledDate = "2026-10-12",
                    scheduledTime = "20:00",
                    notes = "Answer questions from community comments."
                )
            )

            // Starter Projects
            projectDao.insertProject(
                ContentProjectEntity(
                    title = "Ultimate Setup Guide for New Creators",
                    topic = "Audio, Lighting, and Camera Gear",
                    platform = "YouTube",
                    format = "Video",
                    status = "Production",
                    scriptText = "Hook: Stop wasting thousands on gear you don't need.\nScene 1: Natural light vs softbox...\nScene 2: Dynamic vs Condenser mic...\nCTA: Subscribe for weekly creator gear tests.",
                    hookText = "Stop wasting money on camera gear until you watch this.",
                    ctaText = "Drop your current setup in the comments below!",
                    captionText = "The honest truth about what you actually need to start your channel in 2026. Link in bio!",
                    hashtagsText = "#creator #studio #youtube #filmmaking #techgear",
                    thumbnailTitle = "DON'T BUY THIS!",
                    thumbnailStyle = "YouTube Viral",
                    thumbnailBadge = "4K",
                    duration = "8m 30s"
                )
            )

            // Starter Media items
            mediaDao.insertMedia(
                MediaItemEntity(
                    title = "Studio B-Roll Master Clip 01",
                    category = "Videos",
                    fileType = "video/mp4",
                    uriOrPath = "local://broll_studio.mp4",
                    sizeFormatted = "48 MB",
                    durationFormatted = "0:45",
                    tags = "broll, camera, lighting"
                )
            )
            mediaDao.insertMedia(
                MediaItemEntity(
                    title = "Neon YouTube Thumbnail Concept",
                    category = "Thumbnails",
                    fileType = "image/png",
                    uriOrPath = "local://thumb_neon.png",
                    sizeFormatted = "1.8 MB",
                    tags = "thumbnail, 16:9, high-ctr"
                )
            )

            // Starter Script
            scriptDao.insertScript(
                SavedScriptEntity(
                    title = "Hook Strategy for Shorts and Reels",
                    topic = "First 3 Seconds Retention",
                    platform = "YouTube",
                    language = "en",
                    duration = "45s",
                    tone = "Energetic",
                    hook = "If your viewers leave in 3 seconds, here is the exact reason why.",
                    bodyContent = "Most creators start with an intro. Instead, show the climax first, give 3 rapid visual cuts, and state the payoff.",
                    cta = "Save this reel and try this structure on your next video."
                )
            )

            // Starter Prompt Templates ("add all prompt this app")
            val promptDao = db.promptTemplateDao()
            val starterPrompts = listOf(
                PromptTemplateEntity(
                    title = "Viral Short Retention Hook",
                    category = "Viral Shorts",
                    templateText = "Create a viral short-form video script about: \"{topic}\". Return JSON with title, SEO description, spoken voiceover script under 50 words, and 3 distinct visual generation prompts.",
                    sampleTopic = "Secret productivity hacks used by top creators",
                    platform = "YouTube Shorts",
                    tags = "viral, hook, retention",
                    isFavorite = true,
                    usageCount = 14
                ),
                PromptTemplateEntity(
                    title = "10-Minute Deep-Dive Masterclass",
                    category = "10-Min Video",
                    templateText = "Write a complete 10-minute long-form YouTube video plan about \"{topic}\" with 10 structured scenes, B-roll cues, on-screen graphics, and mid-roll retention hooks.",
                    sampleTopic = "How to build a profitable creator business in 2026",
                    platform = "YouTube",
                    tags = "10min, long-form, masterclass",
                    isFavorite = true,
                    usageCount = 9
                ),
                PromptTemplateEntity(
                    title = "High-Converting Instagram Reel",
                    category = "Reels",
                    templateText = "Generate an Instagram Reel caption and spoken voiceover script with a question hook, 3 key takeaways with emojis, and a save/share call-to-action.",
                    sampleTopic = "3 camera settings every smartphone creator must turn on",
                    platform = "Instagram",
                    tags = "instagram, reels, viral",
                    isFavorite = true,
                    usageCount = 12
                ),
                PromptTemplateEntity(
                    title = "Kannada Viral Tech & Culture Short",
                    category = "Kannada Culture",
                    templateText = "ಕನ್ನಡದಲ್ಲಿ ಆಕರ್ಷಕ ಮತ್ತು ವೈರಲ್ ಶಾರ್ಟ್ಸ್ ಸ್ಕ್ರಿಪ್ಟ್ ರಚಿಸಿ: \"{topic}\". ಪ್ರಾರಂಭದ ಹುಕ್, ಮುಖ್ಯ ವಿಷಯ, ಮತ್ತು ಕೊನೆಯ ಕಾಲ್-ಟು-ಆಕ್ಷನ್ ಒಳಗೊಂಡಿರಬೇಕು.",
                    sampleTopic = "ಕನ್ನಡದಲ್ಲಿ AI ಬಳಸಿಕೊಂಡು ವೀಡಿಯೊ ರಚಿಸುವುದು ಹೇಗೆ",
                    platform = "YouTube Shorts",
                    tags = "kannada, local, regional",
                    isFavorite = true,
                    usageCount = 18
                ),
                PromptTemplateEntity(
                    title = "3 Visual Scene Cinematic Prompts",
                    category = "Visual Prompts",
                    templateText = "Generate 3 photorealistic AI image generation prompts for video scenes about \"{topic}\": Scene 1 neon studio master wide-angle, Scene 2 close-up macro action, Scene 3 vibrant high-tech hero frame.",
                    sampleTopic = "Cyberpunk content studio setup with ambient lighting",
                    platform = "All",
                    tags = "visual, scene, image-gen",
                    isFavorite = false,
                    usageCount = 6
                ),
                PromptTemplateEntity(
                    title = "High-CTR YouTube Title & SEO Optimizer",
                    category = "YouTube SEO",
                    templateText = "Generate 5 high CTR YouTube titles under 60 characters with power words, curiosity gap, and search-volume keywords, plus 15 high-ranking tags.",
                    sampleTopic = "Best microphone for YouTube under 5000",
                    platform = "YouTube",
                    tags = "seo, titles, ctr",
                    isFavorite = false,
                    usageCount = 11
                ),
                PromptTemplateEntity(
                    title = "Facebook Community Engagement Post",
                    category = "Facebook",
                    templateText = "Write an engaging Facebook post that sparks comments and debate about \"{topic}\", formatted with clear paragraph breaks and a discussion question.",
                    sampleTopic = "Will AI replace video editors in the next 2 years?",
                    platform = "Facebook",
                    tags = "facebook, discussion, community",
                    isFavorite = false,
                    usageCount = 4
                )
            )
            promptDao.insertAll(starterPrompts)

            // Starter Auto Video Jobs (Simulated 10-Min Auto Generator Runs)
            val autoJobDao = db.autoVideoJobDao()
            autoJobDao.insertJob(
                AutoVideoJobEntity(
                    title = "The 3 Camera Secrets That Changed My Views Forever #Shorts",
                    topic = "Camera Settings for Creators",
                    platform = "YouTube",
                    status = "Uploaded",
                    scriptText = "Stop shooting in auto mode! Turn off auto white balance, lock your exposure, and set shutter speed to 1/100. Watch your video quality double instantly.",
                    description = "Master these 3 smartphone camera settings in 60 seconds. Subscribe for daily creator tips! #CreatorTips #YouTubeShorts #TechHacks",
                    visualPrompt1 = "Close-up of camera lens with neon cyan reflections and cinematic bokeh",
                    visualPrompt2 = "Side-by-side comparison of blurry video vs ultra sharp 4k footage",
                    visualPrompt3 = "Creator smiling in modern studio pointing to subscribe button with graphics",
                    format = "9:16 Shorts",
                    durationSec = 52,
                    uploadId = "YT-AUTO-8921",
                    uploadUrl = "https://studio.youtube.com/video/YT-AUTO-8921/edit"
                )
            )
            autoJobDao.insertJob(
                AutoVideoJobEntity(
                    title = "Stop Making This Audio Mistake in 2026",
                    topic = "Audio & Microphone Setup",
                    platform = "Instagram",
                    status = "Uploaded",
                    scriptText = "Good audio beats good video every single time. Here is the 1-second filter that removes background room echo from any cheap microphone.",
                    description = "Save this audio hack for your next reel! Tag a friend who needs cleaner mic audio. #ReelsTips #AudioHacks #CreatorGrowth",
                    visualPrompt1 = "Microphone waveform glowing in ambient purple studio",
                    visualPrompt2 = "Sound equalizer bouncing on smartphone interface",
                    visualPrompt3 = "Creator wearing headphones nodding with high satisfaction",
                    format = "9:16 Shorts",
                    durationSec = 45,
                    uploadId = "IG-REEL-4402",
                    uploadUrl = "https://www.instagram.com/reel/IG-REEL-4402"
                )
            )
        }
    }
}
