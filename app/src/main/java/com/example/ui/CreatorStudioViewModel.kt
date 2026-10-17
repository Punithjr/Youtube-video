package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CreatorStudioViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = CreatorRepository(db)

    val settings: StateFlow<CreatorSettingsEntity> = repository.settings
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CreatorSettingsEntity()
        )

    val calendarTasks: StateFlow<List<CalendarTaskEntity>> = repository.calendarTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val projects: StateFlow<List<ContentProjectEntity>> = repository.projects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val mediaItems: StateFlow<List<MediaItemEntity>> = repository.mediaItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedScripts: StateFlow<List<SavedScriptEntity>> = repository.savedScripts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val autoVideoJobs: StateFlow<List<AutoVideoJobEntity>> = repository.autoVideoJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val promptTemplates: StateFlow<List<PromptTemplateEntity>> = repository.promptTemplates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 10-Minute Auto Video Generator Scheduler State
    private val _isAutoSchedulerActive = MutableStateFlow(true)
    val isAutoSchedulerActive: StateFlow<Boolean> = _isAutoSchedulerActive.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(600) // 10 minutes default (600s)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _isGeneratingNow = MutableStateFlow(false)
    val isGeneratingNow: StateFlow<Boolean> = _isGeneratingNow.asStateFlow()

    private var schedulerJob: kotlinx.coroutines.Job? = null

    init {
        startSchedulerLoop()
    }

    private fun startSchedulerLoop() {
        schedulerJob?.cancel()
        schedulerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                if (_isAutoSchedulerActive.value) {
                    if (_countdownSeconds.value > 1) {
                        _countdownSeconds.value -= 1
                    } else {
                        _countdownSeconds.value = 600
                        // Auto trigger scheduled video generation!
                        trigger10MinuteAutoVideoRun(
                            topic = "AI & Creator Studio Trends",
                            platform = "YouTube",
                            format = "9:16 Shorts",
                            language = "en"
                        )
                    }
                }
            }
        }
    }

    fun toggleAutoScheduler(active: Boolean) {
        _isAutoSchedulerActive.value = active
        val msg = if (active) "10-Minute Auto Video Scheduler ACTIVE" else "10-Minute Scheduler PAUSED"
        _toastMessage.tryEmit(msg)
    }

    fun resetCountdown() {
        _countdownSeconds.value = 600
    }

    // Navigation state
    private val _currentRoute = MutableStateFlow("dashboard")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // Toast event flow (Explicit requirement: "Implement a toast notification system that alerts the user when a content task changes status in the Content Calendar, such as transitioning from 'Draft' to 'Scheduled'")
    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun saveSettings(updated: CreatorSettingsEntity) {
        viewModelScope.launch {
            repository.saveSettings(updated)
        }
    }

    fun addCalendarTask(task: CalendarTaskEntity) {
        viewModelScope.launch {
            repository.addTask(task)
            _toastMessage.tryEmit("Task scheduled: ${task.title}")
        }
    }

    fun updateCalendarTask(task: CalendarTaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            _toastMessage.tryEmit("Task updated: ${task.title}")
        }
    }

    fun deleteCalendarTask(task: CalendarTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _toastMessage.tryEmit("Task removed from schedule")
        }
    }

    fun updateCalendarTaskStatus(task: CalendarTaskEntity, newStatus: String) {
        val oldStatus = task.status
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, newStatus)
            // Emit toast alert as required: "transitioning from 'Draft' to 'Scheduled'"
            _toastMessage.tryEmit("Status changed: '$oldStatus' ➔ '$newStatus' for \"${task.title}\"")
        }
    }

    fun updateCalendarTaskDate(task: CalendarTaskEntity, newDate: String) {
        viewModelScope.launch {
            repository.updateTaskDate(task.id, newDate)
            _toastMessage.tryEmit("Rescheduled \"${task.title}\" to $newDate")
        }
    }

    fun addProject(project: ContentProjectEntity) {
        viewModelScope.launch {
            repository.addProject(project)
            _toastMessage.tryEmit("Project created: ${project.title}")
        }
    }

    fun updateProject(project: ContentProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project)
            _toastMessage.tryEmit("Project updated")
        }
    }

    fun deleteProject(project: ContentProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            _toastMessage.tryEmit("Project deleted")
        }
    }

    fun addMedia(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.addMedia(item)
            _toastMessage.tryEmit("Saved to Media Library: ${item.title}")
        }
    }

    fun deleteMedia(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.deleteMedia(item)
            _toastMessage.tryEmit("Media file removed")
        }
    }

    fun addScript(script: SavedScriptEntity) {
        viewModelScope.launch {
            repository.addScript(script)
            _toastMessage.tryEmit("Script saved to Library")
        }
    }

    fun trigger10MinuteAutoVideoRun(
        topic: String,
        platform: String,
        format: String,
        language: String
    ) {
        viewModelScope.launch {
            _isGeneratingNow.value = true
            _toastMessage.tryEmit("🎬 AI Studio Engine running: Generating script & 3 visual scenes...")
            kotlinx.coroutines.delay(1200)

            val autoContent = com.example.generator.ContentGenerator.generateAutoVideo(
                topic = topic,
                platform = platform,
                language = language,
                format = format
            )

            val uploadUrl = when (platform.lowercase()) {
                "instagram" -> "https://www.instagram.com/reel/${autoContent.uploadId}"
                "facebook" -> "https://www.facebook.com/watch/${autoContent.uploadId}"
                else -> "https://studio.youtube.com/video/${autoContent.uploadId}/edit"
            }

            val job = AutoVideoJobEntity(
                title = autoContent.title,
                topic = topic,
                platform = platform,
                status = "Uploaded",
                scriptText = autoContent.script,
                description = autoContent.description,
                visualPrompt1 = autoContent.visualPrompts.getOrElse(0) { "" },
                visualPrompt2 = autoContent.visualPrompts.getOrElse(1) { "" },
                visualPrompt3 = autoContent.visualPrompts.getOrElse(2) { "" },
                format = format,
                durationSec = autoContent.durationSec,
                uploadId = autoContent.uploadId,
                uploadUrl = uploadUrl
            )
            repository.addAutoVideoJob(job)

            // Also auto-add to Content Calendar as Scheduled/Published
            repository.addTask(
                CalendarTaskEntity(
                    title = autoContent.title,
                    platform = platform,
                    contentType = if (format.contains("16:9")) "Video" else "Short / Reel",
                    status = "Published",
                    scheduledDate = "2026-10-06",
                    scheduledTime = "Auto-Published",
                    notes = "Automated 10-Minute Video Engine Upload: ${autoContent.uploadId}"
                )
            )

            // Add to Content Projects
            repository.addProject(
                ContentProjectEntity(
                    title = autoContent.title,
                    topic = topic,
                    platform = platform,
                    format = format,
                    status = "Published",
                    scriptText = autoContent.script,
                    captionText = autoContent.description,
                    hashtagsText = autoContent.hashtags,
                    duration = "${autoContent.durationSec}s"
                )
            )

            _isGeneratingNow.value = false
            _toastMessage.tryEmit("✅ Video rendered & auto-uploaded to $platform: ${autoContent.uploadId}")
        }
    }

    fun deleteAutoVideoJob(job: AutoVideoJobEntity) {
        viewModelScope.launch {
            repository.deleteAutoVideoJob(job)
            _toastMessage.tryEmit("Auto video record removed")
        }
    }

    fun clearAllAutoVideoJobs() {
        viewModelScope.launch {
            repository.clearAllAutoVideoJobs()
            _toastMessage.tryEmit("Auto video history cleared")
        }
    }

    fun addPromptTemplate(prompt: PromptTemplateEntity) {
        viewModelScope.launch {
            repository.addPromptTemplate(prompt)
            _toastMessage.tryEmit("Prompt added to Library!")
        }
    }

    fun deletePromptTemplate(prompt: PromptTemplateEntity) {
        viewModelScope.launch {
            repository.deletePromptTemplate(prompt)
            _toastMessage.tryEmit("Prompt removed from Library")
        }
    }

    fun incrementPromptUsage(id: Long) {
        viewModelScope.launch {
            repository.incrementPromptUsage(id)
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.addTask(
                CalendarTaskEntity(
                    title = "How Top Creators Edit Videos Faster in 2026",
                    platform = "YouTube",
                    contentType = "Video",
                    status = "Scheduled",
                    scheduledDate = "2026-10-18",
                    scheduledTime = "18:00",
                    notes = "16:9 Long-Form breakdown with timeline shortcuts."
                )
            )
            repository.addTask(
                CalendarTaskEntity(
                    title = "Stop Making These 3 Reel Mistakes",
                    platform = "Instagram",
                    contentType = "Reel",
                    status = "Draft",
                    scheduledDate = "2026-10-19",
                    scheduledTime = "14:00",
                    notes = "Fast cut 9:16 vertical hook."
                )
            )
            _toastMessage.tryEmit("Sample creator items populated!")
        }
    }
}
