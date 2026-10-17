package com.example.generator

import com.example.i18n.I18n

object ContentGenerator {

    data class GeneratedScript(
        val title: String,
        val openingHook: String,
        val mainContent: String,
        val storyExplanation: String,
        val ending: String,
        val callToAction: String
    ) {
        fun fullText(): String = """
🎬 $title

🎯 OPENING HOOK (0-5s):
$openingHook

💡 MAIN CONTENT:
$mainContent

📖 STORY / EXPLANATION:
$storyExplanation

🏁 ENDING:
$ending

🚀 CALL TO ACTION:
$callToAction
        """.trimIndent()
    }

    data class VideoScene(
        val sceneNumber: Int,
        val visual: String,
        val voiceover: String,
        val onScreenText: String,
        val bRoll: String
    )

    data class VideoPlan(
        val title: String,
        val ideaSummary: String,
        val scenes: List<VideoScene>,
        val musicSuggestion: String,
        val thumbnailPrompt: String,
        val youtubeDescription: String,
        val tags: String,
        val hashtags: String
    )

    data class ShortReelPlan(
        val title: String,
        val hook: String,
        val script30s: String,
        val script60s: String,
        val audioIdea: String,
        val onScreenText: String,
        val cta: String,
        val hashtags: String,
        val coverIdea: String
    )

    fun generateScript(
        topic: String,
        platform: String,
        language: String,
        duration: String,
        tone: String,
        modifier: String = "standard"
    ): GeneratedScript {
        val isKn = language == I18n.LANG_KN
        val cleanTopic = if (topic.isBlank()) {
            if (isKn) "ಸಾಮಾಜಿಕ ಮಾಧ್ಯಮ ಬೆಳವಣಿಗೆಯ ರಹಸ್ಯಗಳು" else "Social Media Growth Secrets"
        } else topic

        val hook = when {
            isKn && modifier == "viral" -> "ನೀವು ಈ ಒಂದು ತಪ್ಪು ಮಾಡುತ್ತಿದ್ದರೆ, ನಿಮ್ಮ ವೀಕ್ಷಕರು ತಕ್ಷಣವೇ ವೀಡಿಯೊ ಬಿಟ್ಟು ಹೋಗುತ್ತಾರೆ!"
            isKn && modifier == "emotional" -> "ನಾನು ಈ ಪ್ರಯಾಣ ಆರಂಭಿಸಿದಾಗ ಯಾರೂ ನಂಬಲಿಲ್ಲ, ಆದರೆ ಈ ಒಂದು ಸೂತ್ರ ಎಲ್ಲವನ್ನೂ ಬದಲಿಸಿತು."
            isKn -> "ವೀಡಿಯೊವನ್ನು ಸ್ಕ್ರಾಲ್ ಮಾಡುವ ಮೊದಲು ಈ 10 ಸೆಕೆಂಡುಗಳನ್ನು ಗಮನವಿಟ್ಟು ನೋಡಿ."
            modifier == "viral" -> "Stop scrolling! 99% of creators make this exact mistake in the first 3 seconds."
            modifier == "emotional" -> "I almost quit last year until I discovered this one truth about content creation."
            modifier == "professional" -> "In this breakdown, we examine the proven analytical framework behind: $cleanTopic."
            else -> "Here is the exact strategy to master $cleanTopic in under $duration."
        }

        val main = when {
            isKn -> "ಮೊದಲನೆಯದಾಗಿ, ವಿಷಯದ ಗುಣಮಟ್ಟ ಮತ್ತು ಮೊದಲ 3 ಸೆಕೆಂಡಿನ ಹುಕ್ ಅತ್ಯಂತ ಮುಖ್ಯ. ಎರಡನೆಯದಾಗಿ, ನಿಮ್ಮ ಪ್ರೇಕ್ಷಕರ ಸಮಸ್ಯೆಗಳನ್ನು ನೇರವಾಗಿ ಗುರುತಿಸಿ ಪರಿಹಾರ ನೀಡಿ."
            modifier == "shorter" -> "1. Nail your initial hook.\n2. Cut every filler word.\n3. Deliver the promised value before 20s."
            modifier == "longer" -> "Phase 1: Diagnosis. Identify why conventional methods fail for $cleanTopic.\nPhase 2: Execution. Step-by-step framework.\nPhase 3: Optimization. Retaining audience attention throughout."
            else -> "Point 1: The core principle behind $cleanTopic.\nPoint 2: Practical implementation without expensive equipment.\nPoint 3: The secret retention loop top creators use."
        }

        val story = when {
            isKn -> "ಕಳೆದ ತಿಂಗಳು ನಾನು ಈ ವಿಧಾನವನ್ನು ಪ್ರಯೋಗಿಸಿದಾಗ ನಿರೀಕ್ಷೆಗಿಂತ 3 ಪಟ್ಟು ಹೆಚ್ಚು ಪ್ರತಿಕ್ರಿಯೆ ಮತ್ತು ವೀಕ್ಷಣೆಗಳು ಲಭಿಸಿದವು. ಸ್ಥಿರತೆಯೇ ಮುಖ್ಯ."
            else -> "When top creators shifted from generic tutorials to this storytelling angle, average watch time jumped from 35% to 78%."
        }

        val ending = when {
            isKn -> "ಗುಣಮಟ್ಟದ ಕಂಟೆಂಟ್ ಮತ್ತು ಪ್ರೇಕ್ಷಕರ ಸಂಪರ್ಕವೇ ಯಶಸ್ಸಿನ ಹೆಬ್ಬಾಗಿಲು."
            else -> "The creators winning this year aren't working harder; they're packaging their insights smarter."
        }

        val cta = when {
            isKn -> "ಈ ವೀಡಿಯೊ ಉಪಯುಕ್ತವೆನಿಸಿದರೆ ಲೈಕ್ ಮಾಡಿ, ಸಬ್‌ಸ್ಕ್ರೈಬ್ ಆಗಿ ಮತ್ತು ನಿಮ್ಮ ಅನಿಸಿಕೆಯನ್ನು ಕಮೆಂಟ್‌ನಲ್ಲಿ ಹಂಚಿಕೊಳ್ಳಿ!"
            platform == "Instagram" -> "Save this reel for your next batch creation session & drop a 🔥 in the comments!"
            platform == "YouTube" -> "Hit subscribe for weekly creator breakdowns, and tap the bell icon so you never miss an upload."
            else -> "Share this with a fellow creator who needs to see this today!"
        }

        return GeneratedScript(
            title = if (isKn) "$cleanTopic ಕುರಿತು ಪರಿಪೂರ್ಣ ಸ್ಕ್ರಿಪ್ಟ್" else "Mastering $cleanTopic Blueprint",
            openingHook = hook,
            mainContent = main,
            storyExplanation = story,
            ending = ending,
            callToAction = cta
        )
    }

    fun generateVideoPlan(topic: String, style: String, language: String): VideoPlan {
        val isKn = language == I18n.LANG_KN
        val cleanTopic = if (topic.isBlank()) (if (isKn) "ಕ್ರಿಯೇಟರ್ ಜೀವನಶೈಲಿ" else "Creator Studio Breakthrough") else topic

        val scenes = listOf(
            VideoScene(
                sceneNumber = 1,
                visual = "Fast cut close-up of host speaking with dramatic studio backlighting",
                voiceover = if (isKn) "ಈ ರಹಸ್ಯ ನಿಮಗೆ ತಿಳಿದರೆ ನಿಮ್ಮ ಕಂಟೆಂಟ್ ಸೃಷ್ಟಿ ಸಂಪೂರ್ಣ ಬದಲಾಗುತ್ತದೆ." else "If you only learn one thing about $cleanTopic this year, make it this.",
                onScreenText = "THE TRUTH ABOUT $cleanTopic",
                bRoll = "Time-lapse of studio editing timeline scrubbing quickly"
            ),
            VideoScene(
                sceneNumber = 2,
                visual = "Side-angle camera with graphics illustrating key data points",
                voiceover = if (isKn) "ಹಂತ 1: ಪ್ರೇಕ್ಷಕರಿಗೆ ಏನು ಬೇಕು ಎಂಬುದನ್ನು ನಿಖರವಾಗಿ ಅರಿಯಿರಿ." else "Step 1: The common myth that keeps everyone stuck.",
                onScreenText = "MISTAKE #1 EXPOSED",
                bRoll = "Screen capture of analytics showing viral retention spike"
            ),
            VideoScene(
                sceneNumber = 3,
                visual = "Demonstration / live walkthrough on desk setup",
                voiceover = if (isKn) "ಹಂತ 2: ಕೇವಲ 3 ನಿಯಮಗಳನ್ನು ಪಾಲಿಸಿ ಗುಣಮಟ್ಟ ಹೆಚ್ಚಿಸಿ." else "Step 2: The exact 3-part blueprint we use every single week.",
                onScreenText = "THE 3-STEP SYSTEM",
                bRoll = "Macro camera shot of microphone and camera dials turning"
            ),
            VideoScene(
                sceneNumber = 4,
                visual = "Host direct to lens with confident warm smile",
                voiceover = if (isKn) "ಚಾನೆಲ್ ಸಬ್‌ಸ್ಕ್ರೈಬ್ ಮಾಡಲು ಮರೆಯಬೇಡಿ!" else "Try this in your next upload, and let me know your results.",
                onScreenText = "SUBSCRIBE FOR PART 2",
                bRoll = "End screen card with related playlist recommendations"
            )
        )

        return VideoPlan(
            title = if (isKn) "$cleanTopic: ಸಂಪೂರ್ಣ ಮಾರ್ಗದರ್ಶಿ ($style ಶೈಲಿ)" else "$cleanTopic: The Definitive Guide ($style)",
            ideaSummary = if (isKn) "$style ಶೈಲಿಯಲ್ಲಿ ರಚಿಸಲಾದ ಆಕರ್ಷಕ ದೀರ್ಘಾವಧಿ ವೀಡಿಯೊ ಯೋಜನೆ." else "Full 16:9 production package formatted for high retention and viewer engagement.",
            scenes = scenes,
            musicSuggestion = "Ambient Lo-Fi beat at 95 BPM with uplifting crescendo for final segment",
            thumbnailPrompt = "Hyper-realistic high-contrast 16:9 thumbnail, expressive face on right side, bold neon yellow text '$cleanTopic', dark cinematic studio background",
            youtubeDescription = """
In this video, we break down everything you need to know about $cleanTopic.

Timestamps:
0:00 - The Big Problem
1:15 - Mistake Most People Make
3:45 - The Step-by-Step Fix
7:10 - Actionable Takeaways & Next Steps

🔔 Subscribe for new creator episodes every week!
            """.trimIndent(),
            tags = "creator, youtube studio, $cleanTopic, tutorial, editing, production, retention",
            hashtags = "#creator #studio #youtube #filmmaking #viralcontent"
        )
    }

    fun generateTenMinuteVideoPlan(topic: String, style: String, language: String): VideoPlan {
        val isKn = language == I18n.LANG_KN
        val cleanTopic = if (topic.isBlank()) (if (isKn) "ಕಂಟೆಂಟ್ ರಚನೆಯ 10 ನಿಮಿಷದ ಮಾಸ್ಟರ್‌ಕ್ಲಾಸ್" else "10-Minute Masterclass: $topic") else topic

        val tenMinuteScenes = listOf(
            VideoScene(
                sceneNumber = 1,
                visual = "[0:00 - 1:00] Dramatic opening close-up with sound riser and quick montage of common failures.",
                voiceover = if (isKn) "ಸ್ವಾಗತ! ಇಂದಿನ 10 ನಿಮಿಷಗಳ ವೀಡಿಯೊದಲ್ಲಿ, $cleanTopic ಕುರಿತು ಯಾರೂ ಬಹಿರಂಗಪಡಿಸದ ರಹಸ್ಯಗಳನ್ನು ತಿಳಿಯಲಿದ್ದೀರಿ." else "Welcome. Over the next 10 minutes, we are exposing the truth behind $cleanTopic that top industry insiders avoid discussing.",
                onScreenText = "00:00 - THE HARSH TRUTH ABOUT $cleanTopic",
                bRoll = "Rapid 0.5s visual cuts of studio setup, waveform timeline, and viewer retention graph."
            ),
            VideoScene(
                sceneNumber = 2,
                visual = "[1:00 - 2:00] Wide desk shot with key questions displayed on floating screen graphics.",
                voiceover = if (isKn) "ಮೊದಲ ನಿಮಿಷ: ಜನರು ಸಾಮಾನ್ಯವಾಗಿ ಎಡವುವ 3 ತಪ್ಪು ಕಲ್ಪನೆಗಳನ್ನು ಗುರುತಿಸಿ." else "Minute 1: The standard advice you see online actually damages your organic reach. Here is why.",
                onScreenText = "01:00 - WHY TRADITIONAL ADVICE FAILS",
                bRoll = "Split screen showing conventional approach vs modern algorithmic pattern."
            ),
            VideoScene(
                sceneNumber = 3,
                visual = "[2:00 - 3:00] Side profile angle demonstrating core workflow breakdown.",
                voiceover = if (isKn) "ಎರಡನೇ ನಿಮಿಷ: ಮೂಲಭೂತ ಅಡಿಪಾಯ. ಬಜೆಟ್ ಉಪಕರಣಗಳಿಂದಲೇ ಅತ್ಯುತ್ತಮ ಫಲಿತಾಂಶ ಪಡೆಯುವುದು ಹೇಗೆ." else "Minute 2: The foundation. Before touching any expensive tool, set up your three core pillars.",
                onScreenText = "02:00 - PILLAR 1: THE FOUNDATION",
                bRoll = "Close-up macro lens panning over microphone capsule, audio interface dials, and LED panel."
            ),
            VideoScene(
                sceneNumber = 4,
                visual = "[3:00 - 4:00] Screen recording demonstration with step-by-step cursor highlights.",
                voiceover = if (isKn) "ಮೂರನೇ ನಿಮಿಷ: ಸ್ಕ್ರಿಪ್ಟಿಂಗ್ ಮತ್ತು ವೇಗದ ಕಥೆ ಹೇಳುವ ಕಲೆ." else "Minute 3: The pacing framework. Notice how every 30 seconds we introduce a micro-revelation.",
                onScreenText = "03:00 - PILLAR 2: RETENTION PACING",
                bRoll = "Timeline scrubbing showcasing color-coded sound effect layers and dynamic J-cuts."
            ),
            VideoScene(
                sceneNumber = 5,
                visual = "[4:00 - 5:00] Real-world case study walkthrough with verified creator analytics.",
                voiceover = if (isKn) "ನಾಲ್ಕನೇ ನಿಮಿಷ: ನೈಜ ಉದಾಹರಣೆ. ಈ ಬದಲಾವಣೆಯು ವೀಕ್ಷಕರ ಸಂಖ್ಯೆಯನ್ನು ಹೇಗೆ 3 ಪಟ್ಟು ಹೆಚ್ಚಿಸಿತು." else "Minute 4: Let us look at a live channel case study where applying this jumped average view duration from 2:15 to 7:40.",
                onScreenText = "04:00 - LIVE CASE STUDY BREAKDOWN",
                bRoll = "Full-screen analytics graph showing smooth plateau retention instead of steep drop-off."
            ),
            VideoScene(
                sceneNumber = 6,
                visual = "[5:00 - 6:00] Host leaning into camera with energetic, authoritative delivery.",
                voiceover = if (isKn) "ಐದನೇ ನಿಮಿಷ: ಅರ್ಧ ದಾರಿಯಲ್ಲಿ ನೀವು ತಿಳಿಯಬೇಕಾದ ಅತ್ಯಂತ ಮಹತ್ವದ ತಂತ್ರಜ್ಞಾನ ರಹಸ್ಯ." else "Minute 5: We are halfway through, and this is the turning point: the compounding feedback engine.",
                onScreenText = "05:00 - HALFWAY BREAKTHROUGH: THE ENGINE",
                bRoll = "Animated diagram illustrating how viewers transition from one upload into binge sessions."
            ),
            VideoScene(
                sceneNumber = 7,
                visual = "[6:00 - 7:00] Direct comparison chart side-by-side with host gestures.",
                voiceover = if (isKn) "ಆರನೇ ನಿಮಿಷ: ಸಮಯ ಉಳಿಸುವ ಶಾರ್ಟ್‌ಕಟ್‌ಗಳು ಮತ್ತು ಆಟೊಮೇಷನ್ ಪರಿಕರಗಳು." else "Minute 6: Production efficiency. How to cut your total creation time down from 12 hours to under 2 hours.",
                onScreenText = "06:00 - THE 2-HOUR PRODUCTION BLUEPRINT",
                bRoll = "Batch recording session footage with multiple shirts/angles filmed in one sitting."
            ),
            VideoScene(
                sceneNumber = 8,
                visual = "[7:00 - 8:00] Warning sign overlay with host pointing to dangerous mistakes.",
                voiceover = if (isKn) "ಏಳನೇ ನಿಮಿಷ: ಎಚ್ಚರಿಕೆ! ಈ ಒಂದು ತಪ್ಪು ಮಾಡಿದರೆ ಅಲ್ಗಾರಿದಮ್ ನಿಮ್ಮ ವೀಡಿಯೊವನ್ನು ನಿಲ್ಲಿಸಬಹುದು." else "Minute 7: Warning. Do not fall into the trap of over-optimizing for clickbait without delivering fulfillment.",
                onScreenText = "07:00 - THE #1 FATAL CREATOR MISTAKE",
                bRoll = "Visual demonstration of unfulfilled hook leading to immediate viewer drop."
            ),
            VideoScene(
                sceneNumber = 9,
                visual = "[8:00 - 9:00] Interactive summary checklist with checkboxes clicking on-screen.",
                voiceover = if (isKn) "ಎಂಟನೇ ನಿಮಿಷ: ತ್ವರಿತ ಪರಿಶೀಲನಾ ಪಟ್ಟಿ (Checklist) ನಿಮ್ಮ ಮುಂದಿನ ವೀಡಿಯೊಗಾಗಿ." else "Minute 8: Here is your 5-point upload checklist. Take a screenshot right now.",
                onScreenText = "08:00 - THE 5-POINT ACTION CHECKLIST",
                bRoll = "Clean, bold on-screen graphic with numbered bullets: Hook, Value, Pacing, CTA, Thumbnail."
            ),
            VideoScene(
                sceneNumber = 10,
                visual = "[9:00 - 10:00] Host smiling warmly, end-screen card popups, subscribe button animation.",
                voiceover = if (isKn) "ಒಂಬತ್ತನೇ & ಹತ್ತನೇ ನಿಮಿಷ: ಅಂತಿಮ ಸಂದೇಶ, ಸಬ್‌ಸ್ಕ್ರೈಬ್ ಮಾಡಿ ಮತ್ತು ಮುಂದಿನ ಸಂಚಿಕೆ ನೋಡಿ!" else "Minute 9-10: That wraps up our 10-minute masterclass on $cleanTopic. Drop your thoughts below and subscribe for next week's deep dive!",
                onScreenText = "09:00 - FINAL RECAP & COMMUNITY DISCUSSION",
                bRoll = "End screen animated cards with playlist link and subscribe watermark button."
            )
        )

        return VideoPlan(
            title = if (isKn) "10 ನಿಮಿಷಗಳ ಸಂಪೂರ್ಣ ಮಾಸ್ಟರ್‌ಕ್ಲಾಸ್: $cleanTopic" else "10-MINUTE COMPLETE MASTERCLASS: $cleanTopic",
            ideaSummary = if (isKn) "ಸಂಪೂರ್ಣ 10 ನಿಮಿಷಗಳ 16:9 ದೀರ್ಘಾವಧಿ ವೀಡಿಯೊ ಸ್ಕ್ರಿಪ್ಟ್ (10 ಪ್ರತ್ಯೇಕ ದೃಶ್ಯಗಳು, B-Roll, ಆಡಿಯೋ, ಮತ್ತು ಟೈಮ್‌ಸ್ಟ್ಯಾಂಪ್‌ಗಳು)." else "Comprehensive 10-minute long-form 16:9 video production masterclass with complete scene timings, narration, and visual cues.",
            scenes = tenMinuteScenes,
            musicSuggestion = "10-Minute Dynamic Score: Ambient Lo-Fi intro (0-3m) -> Driving Synthwave (3-7m) -> Cinematic Crescendo (7-10m)",
            thumbnailPrompt = "16:9 High contrast cinematic thumbnail, creator holding hourglass or clock with '10 MINUTE BLUEPRINT' bold text, vibrant neon studio rim lighting",
            youtubeDescription = """
Welcome to the definitive 10-minute masterclass on $cleanTopic!

TIMESTAMPS:
0:00 - The Harsh Truth About $cleanTopic
1:00 - Why Traditional Advice Fails
2:00 - Pillar 1: The Foundation
3:00 - Pillar 2: Retention Pacing
4:00 - Live Case Study Breakdown
5:00 - The Halfway Breakthrough
6:00 - The 2-Hour Production Blueprint
7:00 - The Fatal Creator Mistake
8:00 - The 5-Point Action Checklist
9:00 - Final Summary & Community Discussion

🔔 Subscribe for weekly 10-minute masterclasses!
            """.trimIndent(),
            tags = "10 minute video, masterclass, $cleanTopic, youtube tutorial, long form, creator studio, editing guide",
            hashtags = "#10minutevideo #masterclass #youtube #contentcreator #filmmaking #tutorial"
        )
    }

    fun generateShortReel(topic: String, platform: String, language: String): ShortReelPlan {
        val isKn = language == I18n.LANG_KN
        val cleanTopic = if (topic.isBlank()) (if (isKn) "3 ತ್ವರಿತ ಸಲಹೆಗಳು" else "3 Quick Hacks") else topic

        return ShortReelPlan(
            title = if (isKn) "$cleanTopic - ಶಾರ್ಟ್ಸ್/ರೀಲ್ಸ್" else "$cleanTopic - Fast Short / Reel",
            hook = if (isKn) "ಕೇವಲ 30 ಸೆಕೆಂಡಿನಲ್ಲಿ $cleanTopic ಕಲಿಯಿರಿ!" else "Wait! Don't scroll until you hear this about $cleanTopic.",
            script30s = if (isKn) """
[0-3s] ನೀವು $cleanTopic ಬಗ್ಗೆ ತಿಳಿದುಕೊಳ್ಳಲು ಬಯಸುವಿರಾ?
[3-15s] ಸಲಹೆ 1: ಸರಳವಾಗಿ ಆರಂಭಿಸಿ. ಸಲಹೆ 2: ದಿನಂಪ್ರತಿ 15 ನಿಮಿಷ ಮೀಸಲಿಡಿ.
[15-25s] ಮೂರನೆಯದಾಗಿ: ಪ್ರೇಕ್ಷಕರೊಂದಿಗೆ ಸದಾ ಸಂವಹನ ನಡೆಸಿ.
[25-30s] ಹೆಚ್ಚಿನ ಮಾಹಿತಿಗಾಗಿ ಪ್ರೊಫೈಲ್ ಫಾಲೋ ಮಾಡಿ!
            """.trimIndent() else """
[0-3s] The #1 shortcut to $cleanTopic nobody talks about.
[3-15s] Hack 1: Stop overcomplicating your setup. Focus on clear audio.
[15-25s] Hack 2: Write your hook BEFORE you film a single scene.
[25-30s] Follow for daily creator hacks & save this reel!
            """.trimIndent(),
            script60s = if (isKn) """
[0-5s] ನಿಮ್ಮ ವೀಡಿಯೊಗಳಿಗೆ ಹೆಚ್ಚು ವೀಕ್ಷಣೆ ಬೇಕೆ?
[5-20s] ಇಲ್ಲಿ 3 ಹಂತಗಳ ಪರಿಹಾರವಿದೆ. ಮೊದಲನೆಯದಾಗಿ ಶೀರ್ಷಿಕೆ ಕುತೂಹಲಕಾರಿಯಾಗಿರಬೇಕು.
[20-40s] ಎರಡನೆಯದಾಗಿ ವೇಗದ ಕಟ್‌ಗಳು ಮತ್ತು ಆಕರ್ಷಕ ಉಪಶೀರ್ಷಿಕೆಗಳನ್ನು ಸೇರಿಸಿ.
[40-55s] ಮೂರನೆಯದಾಗಿ ಕಮೆಂಟ್‌ಗಳಿಗೆ ಸದಾ ಉತ್ತರ ನೀಡಿ ಸಮುದಾಯ ಬೆಳೆಸಿ.
[55-60s] ಲೈಕ್ ಮಾಡಿ ಮತ್ತು ಸ್ನೇಹಿತರೊಂದಿಗೆ ಹಂಚಿಕೊಳ್ಳಿ!
            """.trimIndent() else """
[0-5s] If your retention drops off after 5 seconds, watch this now.
[5-25s] Step 1: Deliver visual change every 2.5 seconds (B-roll, zooms, sound effect).
[25-45s] Step 2: Remove all pauses and breath sounds from your audio track.
[45-55s] Step 3: Always pose a question at the end to trigger comments.
[55-60s] Double tap if this helped and share with a friend!
            """.trimIndent(),
            audioIdea = "Trending upbeat synth percussion (120 BPM, royalty-free creator remix)",
            onScreenText = "HOOK: NEVER DO THIS ❌ -> DO THIS INSTEAD ✅",
            cta = if (isKn) "ರೀಲ್ ಸೇವ್ ಮಾಡಿ & ಫಾಲೋ ಮಾಡಿ 🔥" else "Save this reel & follow for more tips! 🚀",
            hashtags = "#shorts #reels #creator #trending #viral #growth",
            coverIdea = "Vertical 9:16 frame with split comparison and high contrast text overlay"
        )
    }

    fun generateHashtags(topic: String, platform: String): List<String> {
        val baseTopic = topic.lowercase().replace(" ", "")
        val core = if (baseTopic.isNotBlank()) listOf("#$baseTopic", "#${baseTopic}tips", "#${baseTopic}creator") else emptyList()
        val trending = listOf("#creator", "#contentcreator", "#viralvideo", "#trendingnow", "#reelsindia", "#creatorsgonnacreate")
        val platformSpecific = when (platform) {
            "YouTube" -> listOf("#youtuber", "#youtubeshorts", "#vlog", "#subscribe", "#smallyoutuber")
            "Instagram" -> listOf("#reelsinstagram", "#explorepage", "#instareels", "#fyp", "#contentcreation")
            "Facebook" -> listOf("#facebookreels", "#creatorpage", "#facebookcreator", "#share")
            else -> listOf("#socialmedia", "#contentstrategy", "#digitalcreator")
        }
        val highReach = listOf("#growth", "#hacks", "#tipsandtricks", "#viralreels", "#dailyreels")
        return (core + platformSpecific + trending + highReach).distinct()
    }

    fun generateCaptions(topic: String, platform: String, language: String): String {
        val isKn = language == I18n.LANG_KN
        val cleanTopic = if (topic.isBlank()) (if (isKn) "ಹೊಸ ವಿಷಯ" else "Fresh Content") else topic
        return when (platform) {
            "Instagram" -> """
✨ $cleanTopic

Here is everything you need to know to take your content to the next level today. 👇

💡 Key Takeaways:
• Always lead with value
• Hook viewers in the first 3 seconds
• Consistency beats perfection every single time

💬 What is your biggest hurdle with this right now? Drop a comment below!

Save this reel so you have it ready when creating! 🔖
${generateHashtags(cleanTopic, "Instagram").take(15).joinToString(" ")}
            """.trimIndent()

            "YouTube" -> """
In this video, we dive deep into $cleanTopic! If you are a creator looking to scale your channel and streamline your production, this breakdown is built for you.

⏰ Timestamps:
0:00 - Introduction & Overview
1:20 - Step 1: The Foundation
3:45 - Step 2: Implementation
6:10 - Step 3: Pro Tips & Traps
8:00 - Final Thoughts & Community Q&A

🔗 Connect With Me:
Subscribe to the channel for weekly tutorials!
Drop your thoughts and questions in the comments below.

${generateHashtags(cleanTopic, "YouTube").take(8).joinToString(" ")}
            """.trimIndent()

            else -> """
📢 Creator Update: $cleanTopic

Sharing my latest findings and strategies with the community today. Whether you are just getting started or scaling up, these principles make all the difference.

What do you think of this approach? Let's discuss in the comments!

${generateHashtags(cleanTopic, "Facebook").take(10).joinToString(" ")}
            """.trimIndent()
        }
    }

    data class AutoVideoContent(
        val title: String,
        val description: String,
        val script: String,
        val visualPrompts: List<String>,
        val hashtags: String,
        val uploadId: String,
        val durationSec: Int
    )

    fun generateAutoVideo(
        topic: String,
        platform: String,
        language: String,
        format: String = "9:16 Shorts"
    ): AutoVideoContent {
        val isKn = language == I18n.LANG_KN
        val cleanTopic = if (topic.isBlank()) {
            if (isKn) "ಸಾಮಾಜಿಕ ಮಾಧ್ಯಮದಲ್ಲಿ ವೈರಲ್ ಆಗುವ ವಿಧಾನ" else "Secret Hacks to Go Viral Fast"
        } else topic.trim()

        val randId = (1000..9999).random()
        val uploadPrefix = when (platform.lowercase()) {
            "instagram" -> "IG-REEL"
            "facebook" -> "FB-VID"
            else -> "YT-AUTO"
        }
        val uploadId = "$uploadPrefix-$randId"

        val title = when {
            isKn -> "$cleanTopic | ಕೇವಲ 60 ಸೆಕೆಂಡುಗಳಲ್ಲಿ ತಿಳಿಯಿರಿ #Shorts #ಕನ್ನಡ"
            platform.contains("Instagram", ignoreCase = true) -> "$cleanTopic 🤯 Stop scrolling and watch this! #Reels"
            platform.contains("Facebook", ignoreCase = true) -> "Must-Watch: $cleanTopic (Full Guide)"
            else -> "The $cleanTopic Secret Nobody Is Telling You #Shorts #Tech"
        }

        val hashtagsList = generateHashtags(cleanTopic, platform)
        val hashtags = hashtagsList.take(6).joinToString(" ")

        val script = if (isKn) {
            "ನಮಸ್ಕಾರ ಸ್ನೇಹಿತರೆ! ನೀವು $cleanTopic ಬಗ್ಗೆ ತಿಳಿದುಕೊಳ್ಳಲು ಬಯಸಿದರೆ, ಈ ವಿಡಿಯೋ ನಿಮಗಾಗಿ. ಮೊದಲನೆಯದಾಗಿ ನಿಮ್ಮ ಗಮನ ಮುಖ್ಯ. ಎರಡನೆಯದಾಗಿ ಪ್ರತಿದಿನದ ಸತತ ಪ್ರಯತ್ನ. ಈ ಟ್ರಿಕ್ ಅನ್ನು ಇಂದೇ ಟ್ರೈ ಮಾಡಿ ಮತ್ತು ನಿಮ್ಮ ಚಾನೆಲ್ ಬೆಳೆಸಿಕೊಳ್ಳಿ!"
        } else {
            "Here is the single most important rule about $cleanTopic that will instantly 10x your results. First, hook your audience in 2 seconds. Second, cut out all dead air. Third, deliver immediate value before asking for a follow. Save this video right now!"
        }

        val description = if (isKn) {
            "ಇಂದಿನ ವೀಡಿಯೊದಲ್ಲಿ ನಾವು $cleanTopic ಕುರಿತು ವಿವರವಾಗಿ ಚರ್ಚಿಸಿದ್ದೇವೆ. ಇನ್ನಷ್ಟು ಉಪಯುಕ್ತ ವಿಷಯಗಳಿಗಾಗಿ ಚಾನೆಲ್ ಸಬ್‌ಸ್ಕ್ರೈಬ್ ಮಾಡಿ! $hashtags"
        } else {
            "In this video we break down everything you need to know about $cleanTopic. Don't forget to like, subscribe and turn on notifications for daily creator updates! $hashtags"
        }

        val visualPrompts = listOf(
            "Ultra photorealistic cinematic close-up of dynamic neon creator studio focused on $cleanTopic, 8k resolution, volumetric lighting",
            "High tech holographic data HUD overlay illustrating key concept of $cleanTopic with bold glowing typography and vibrant particles",
            "High-energy expressive creator in modern studio setup gesturing excitedly toward floating subscribe graphics, studio bokeh background"
        )

        return AutoVideoContent(
            title = title,
            description = description,
            script = script,
            visualPrompts = visualPrompts,
            hashtags = hashtags,
            uploadId = uploadId,
            durationSec = if (format.contains("16:9")) 600 else 54
        )
    }
}
