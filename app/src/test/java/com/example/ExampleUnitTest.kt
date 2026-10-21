package com.example

import com.example.generator.ContentGenerator
import com.example.i18n.I18n
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testContentGeneratorScriptCreation() {
        val script = ContentGenerator.generateScript(
            topic = "YouTube Editing Workflow",
            platform = "YouTube",
            language = I18n.LANG_EN,
            duration = "60s",
            tone = "Viral"
        )
        assertNotNull(script.openingHook)
        assertTrue(script.openingHook.isNotBlank())
        assertTrue(script.mainContent.isNotBlank())
        assertTrue(script.callToAction.isNotBlank())
    }

    @Test
    fun testHashtagGeneration() {
        val hashtags = ContentGenerator.generateHashtags("Creator", "YouTube")
        assertTrue(hashtags.isNotEmpty())
        assertTrue(hashtags.any { it.contains("creator", ignoreCase = true) })
    }

    @Test
    fun testTenMinuteVideoPlanGeneration() {
        val plan = ContentGenerator.generateTenMinuteVideoPlan(
            topic = "The 10 Minute Viral Masterclass",
            style = "Cinematic",
            language = I18n.LANG_EN
        )
        assertNotNull(plan)
        assertEquals(10, plan.scenes.size)
        assertTrue(plan.scenes.first().onScreenText.contains("00:00"))
        assertTrue(plan.scenes.last().onScreenText.contains("09:00"))
        assertTrue(plan.youtubeDescription.contains("TIMESTAMPS"))
    }

    @Test
    fun testGitHubAndVideoEditorTranslations() {
        assertEquals("Video Editor & AI", I18n.t("nav_video_editor", I18n.LANG_EN))
        assertEquals("GitHub Data Link", I18n.t("nav_github_link", I18n.LANG_EN))
        assertEquals("ವೀಡಿಯೊ ಸಂಪಾದಕ & AI", I18n.t("nav_video_editor", I18n.LANG_KN))
        assertEquals("ಗಿಟ್‌ಹಬ್ ಡೇಟಾ ಲಿಂಕ್", I18n.t("nav_github_link", I18n.LANG_KN))
    }

    @Test
    fun testCreatorSettingsDefaultGitHubAndGoogleFields() {
        val settings = com.example.data.CreatorSettingsEntity()
        assertEquals("punithjr2@gmail.com", settings.youTubeEmail)
        assertEquals("Punith JR", settings.googleAccountName)
        assertEquals("109283746501928374", settings.googleAccountId)
        assertTrue(settings.isGitHubConnected)
        assertEquals("punithjr2", settings.gitHubUsername)
        assertEquals("creator-studio-content", settings.gitHubRepo)
        assertEquals("main", settings.gitHubBranch)
    }
}
