package com.example.data

import kotlinx.coroutines.flow.Flow

class CreatorRepository(private val db: AppDatabase) {
    val settings: Flow<CreatorSettingsEntity?> = db.settingsDao().getSettings()
    val calendarTasks: Flow<List<CalendarTaskEntity>> = db.calendarTaskDao().getAllTasks()
    val projects: Flow<List<ContentProjectEntity>> = db.contentProjectDao().getAllProjects()
    val mediaItems: Flow<List<MediaItemEntity>> = db.mediaItemDao().getAllMedia()
    val savedScripts: Flow<List<SavedScriptEntity>> = db.savedScriptDao().getAllScripts()
    val autoVideoJobs: Flow<List<AutoVideoJobEntity>> = db.autoVideoJobDao().getAllJobs()
    val promptTemplates: Flow<List<PromptTemplateEntity>> = db.promptTemplateDao().getAllPrompts()

    suspend fun getSettingsOnce(): CreatorSettingsEntity? = db.settingsDao().getSettingsOnce()

    suspend fun saveSettings(settings: CreatorSettingsEntity) {
        db.settingsDao().insertOrUpdate(settings)
    }

    suspend fun addTask(task: CalendarTaskEntity): Long {
        return db.calendarTaskDao().insertTask(task)
    }

    suspend fun updateTask(task: CalendarTaskEntity) {
        db.calendarTaskDao().updateTask(task)
    }

    suspend fun deleteTask(task: CalendarTaskEntity) {
        db.calendarTaskDao().deleteTask(task)
    }

    suspend fun updateTaskStatus(taskId: Long, newStatus: String) {
        db.calendarTaskDao().updateStatus(taskId, newStatus)
    }

    suspend fun updateTaskDate(taskId: Long, newDate: String) {
        db.calendarTaskDao().updateDate(taskId, newDate)
    }

    suspend fun addProject(project: ContentProjectEntity): Long {
        return db.contentProjectDao().insertProject(project)
    }

    suspend fun updateProject(project: ContentProjectEntity) {
        db.contentProjectDao().updateProject(project)
    }

    suspend fun deleteProject(project: ContentProjectEntity) {
        db.contentProjectDao().deleteProject(project)
    }

    suspend fun addMedia(item: MediaItemEntity): Long {
        return db.mediaItemDao().insertMedia(item)
    }

    suspend fun deleteMedia(item: MediaItemEntity) {
        db.mediaItemDao().deleteMedia(item)
    }

    suspend fun addScript(script: SavedScriptEntity): Long {
        return db.savedScriptDao().insertScript(script)
    }

    suspend fun deleteScript(script: SavedScriptEntity) {
        db.savedScriptDao().deleteScript(script)
    }

    suspend fun addAutoVideoJob(job: AutoVideoJobEntity): Long {
        return db.autoVideoJobDao().insertJob(job)
    }

    suspend fun updateAutoVideoJob(job: AutoVideoJobEntity) {
        db.autoVideoJobDao().updateJob(job)
    }

    suspend fun deleteAutoVideoJob(job: AutoVideoJobEntity) {
        db.autoVideoJobDao().deleteJob(job)
    }

    suspend fun clearAllAutoVideoJobs() {
        db.autoVideoJobDao().clearAllJobs()
    }

    suspend fun addPromptTemplate(prompt: PromptTemplateEntity): Long {
        return db.promptTemplateDao().insertPrompt(prompt)
    }

    suspend fun updatePromptTemplate(prompt: PromptTemplateEntity) {
        db.promptTemplateDao().updatePrompt(prompt)
    }

    suspend fun deletePromptTemplate(prompt: PromptTemplateEntity) {
        db.promptTemplateDao().deletePrompt(prompt)
    }

    suspend fun incrementPromptUsage(id: Long) {
        db.promptTemplateDao().incrementUsage(id)
    }
}
