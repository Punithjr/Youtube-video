package com.example.data.github

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUser(
    val login: String = "",
    val id: Long = 0,
    @Json(name = "avatar_url") val avatarUrl: String = "",
    @Json(name = "html_url") val htmlUrl: String = "",
    val name: String? = null,
    val bio: String? = null,
    @Json(name = "public_repos") val publicRepos: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val email: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubRepo(
    val id: Long = 0,
    val name: String = "",
    @Json(name = "full_name") val fullName: String = "",
    val description: String? = null,
    @Json(name = "html_url") val htmlUrl: String = "",
    @Json(name = "stargazers_count") val stargazersCount: Int = 0,
    @Json(name = "forks_count") val forksCount: Int = 0,
    @Json(name = "default_branch") val defaultBranch: String = "main",
    @Json(name = "updated_at") val updatedAt: String? = null,
    val private: Boolean = false
)

@JsonClass(generateAdapter = true)
data class GitHubFileCommitRequest(
    val message: String,
    val content: String, // Base64 encoded content
    val branch: String = "main",
    val sha: String? = null // required if updating existing file
)

@JsonClass(generateAdapter = true)
data class GitHubCommitResponse(
    val content: GitHubCommitContent? = null,
    val commit: GitHubCommitInfo? = null
)

@JsonClass(generateAdapter = true)
data class GitHubCommitContent(
    val name: String = "",
    val path: String = "",
    val sha: String = "",
    @Json(name = "html_url") val htmlUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class GitHubCommitInfo(
    val sha: String = "",
    val message: String = "",
    @Json(name = "html_url") val htmlUrl: String = ""
)

data class GitHubSyncResult(
    val success: Boolean,
    val message: String,
    val fileUrl: String? = null,
    val sha: String? = null,
    val timestamp: String = ""
)
