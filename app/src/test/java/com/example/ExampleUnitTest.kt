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
}
