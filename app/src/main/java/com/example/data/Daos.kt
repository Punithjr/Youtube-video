package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM creator_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<CreatorSettingsEntity?>

    @Query("SELECT * FROM creator_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): CreatorSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: CreatorSettingsEntity)
}

@Dao
interface CalendarTaskDao {
    @Query("SELECT * FROM calendar_tasks ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getAllTasks(): Flow<List<CalendarTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CalendarTaskEntity): Long

    @Update
    suspend fun updateTask(task: CalendarTaskEntity)

    @Delete
    suspend fun deleteTask(task: CalendarTaskEntity)

    @Query("UPDATE calendar_tasks SET status = :newStatus WHERE id = :taskId")
    suspend fun updateStatus(taskId: Long, newStatus: String)

    @Query("UPDATE calendar_tasks SET scheduledDate = :newDate WHERE id = :taskId")
    suspend fun updateDate(taskId: Long, newDate: String)
}

@Dao
interface ContentProjectDao {
    @Query("SELECT * FROM content_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ContentProjectEntity>>

    @Query("SELECT * FROM content_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ContentProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ContentProjectEntity): Long

    @Update
    suspend fun updateProject(project: ContentProjectEntity)

    @Delete
    suspend fun deleteProject(project: ContentProjectEntity)
}

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items ORDER BY createdAt DESC")
    fun getAllMedia(): Flow<List<MediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(item: MediaItemEntity): Long

    @Delete
    suspend fun deleteMedia(item: MediaItemEntity)
}

@Dao
interface SavedScriptDao {
    @Query("SELECT * FROM saved_scripts ORDER BY createdAt DESC")
    fun getAllScripts(): Flow<List<SavedScriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: SavedScriptEntity): Long

    @Delete
    suspend fun deleteScript(script: SavedScriptEntity)
}

@Dao
interface AutoVideoJobDao {
    @Query("SELECT * FROM auto_video_jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<AutoVideoJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: AutoVideoJobEntity): Long

    @Update
    suspend fun updateJob(job: AutoVideoJobEntity)

    @Delete
    suspend fun deleteJob(job: AutoVideoJobEntity)

    @Query("DELETE FROM auto_video_jobs")
    suspend fun clearAllJobs()
}

@Dao
interface PromptTemplateDao {
    @Query("SELECT * FROM prompt_templates ORDER BY isFavorite DESC, usageCount DESC, id ASC")
    fun getAllPrompts(): Flow<List<PromptTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prompts: List<PromptTemplateEntity>)

    @Update
    suspend fun updatePrompt(prompt: PromptTemplateEntity)

    @Delete
    suspend fun deletePrompt(prompt: PromptTemplateEntity)

    @Query("UPDATE prompt_templates SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementUsage(id: Long)
}
