package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CreatorSettingsEntity
import com.example.i18n.I18n
import com.example.ui.components.AccountLoginDialog
import com.example.ui.components.SidebarDrawerContent
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.UserLoginDialog
import com.example.ui.screens.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CreatorStudioApp(
    viewModel: CreatorStudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val calendarTasks by viewModel.calendarTasks.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val mediaItems by viewModel.mediaItems.collectAsStateWithLifecycle()
    val savedScripts by viewModel.savedScripts.collectAsStateWithLifecycle()
    val autoVideoJobs by viewModel.autoVideoJobs.collectAsStateWithLifecycle()
    val promptTemplates by viewModel.promptTemplates.collectAsStateWithLifecycle()
    val isAutoSchedulerActive by viewModel.isAutoSchedulerActive.collectAsStateWithLifecycle()
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val isGeneratingNow by viewModel.isGeneratingNow.collectAsStateWithLifecycle()
    val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()

    var showWizardDialog by remember { mutableStateOf(false) }
    var showUserLoginDialog by remember { mutableStateOf(false) }
    var selectedPlatformForLogin by remember { mutableStateOf<String?>(null) }

    // First run wizard trigger: if wizardCompleted is false, show wizard
    LaunchedEffect(settings.wizardCompleted) {
        if (!settings.wizardCompleted) {
            showWizardDialog = true
        }
    }

    // Toast notification receiver (Toast alert system required for Content Calendar status transitions)
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Back handling: pop back to dashboard if in secondary screen
    if (currentRoute != "dashboard") {
        BackHandler {
            viewModel.navigateTo("dashboard")
        }
    }

    val currentLang = settings.defaultLanguage
    val isDark = settings.theme == "dark"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarDrawerContent(
                currentRoute = currentRoute,
                currentLanguage = currentLang,
                creatorName = settings.channelName,
                creatorEmail = settings.youTubeEmail,
                onOpenUserLogin = {
                    showUserLoginDialog = true
                    scope.launch { drawerState.close() }
                },
                onSelectRoute = { route ->
                    viewModel.navigateTo(route)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopHeaderBar(
                    settings = settings,
                    currentLanguage = currentLang,
                    isDarkTheme = isDark,
                    onToggleLanguage = {
                        val nextLang = if (currentLang == I18n.LANG_KN) I18n.LANG_EN else I18n.LANG_KN
                        viewModel.saveSettings(settings.copy(defaultLanguage = nextLang))
                    },
                    onToggleTheme = {
                        val nextTheme = if (isDark) "light" else "dark"
                        viewModel.saveSettings(settings.copy(theme = nextTheme))
                    },
                    onOpenNavDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    onOpenSettings = {
                        viewModel.navigateTo("settings")
                    },
                    onOpenUserLogin = {
                        showUserLoginDialog = true
                    },
                    onOpenPlatformLogin = { platform ->
                        selectedPlatformForLogin = platform
                    }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    "dashboard" -> DashboardScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        projects = projects,
                        calendarTasks = calendarTasks,
                        onNavigate = { route -> viewModel.navigateTo(route) }
                    )

                    "auto_generator" -> AutoVideoSchedulerScreen(
                        currentLanguage = currentLang,
                        settings = settings,
                        autoVideoJobs = autoVideoJobs,
                        promptTemplates = promptTemplates,
                        isSchedulerActive = isAutoSchedulerActive,
                        countdownSeconds = countdownSeconds,
                        isGeneratingNow = isGeneratingNow,
                        onToggleScheduler = { viewModel.toggleAutoScheduler(it) },
                        onTriggerAutoRun = { topic, platform, format, lang ->
                            viewModel.trigger10MinuteAutoVideoRun(topic, platform, format, lang)
                        },
                        onDeleteJob = { viewModel.deleteAutoVideoJob(it) },
                        onClearAllJobs = { viewModel.clearAllAutoVideoJobs() },
                        onAddPrompt = { viewModel.addPromptTemplate(it) },
                        onDeletePrompt = { viewModel.deletePromptTemplate(it) },
                        onIncrementPromptUsage = { viewModel.incrementPromptUsage(it) },
                        onSaveSettings = { viewModel.saveSettings(it) }
                    )

                    "create_video" -> VideoCreatorScreen(
                        currentLanguage = currentLang,
                        settings = settings,
                        onSaveProject = { viewModel.addProject(it) },
                        onPublishSuccess = { task -> viewModel.addCalendarTask(task) }
                    )

                    "shorts_reels" -> ShortsReelsScreen(
                        currentLanguage = currentLang,
                        onSaveProject = { viewModel.addProject(it) }
                    )

                    "script_gen" -> ScriptGeneratorScreen(
                        currentLanguage = currentLang,
                        onSaveScript = { viewModel.addScript(it) }
                    )

                    "thumbnail_creator" -> ThumbnailCreatorScreen(
                        currentLanguage = currentLang,
                        onSaveToMediaLibrary = { viewModel.addMedia(it) }
                    )

                    "youtube" -> YouTubeToolScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        onPublishSuccess = { task -> viewModel.addCalendarTask(task) }
                    )

                    "instagram" -> InstagramToolScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        onPublishSuccess = { task -> viewModel.addCalendarTask(task) }
                    )

                    "facebook" -> FacebookToolScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        onPublishSuccess = { task -> viewModel.addCalendarTask(task) }
                    )

                    "projects" -> ContentProjectsScreen(
                        currentLanguage = currentLang,
                        projects = projects,
                        onAddProject = { viewModel.addProject(it) },
                        onUpdateProject = { viewModel.updateProject(it) },
                        onDeleteProject = { viewModel.deleteProject(it) }
                    )

                    "calendar" -> ContentCalendarScreen(
                        currentLanguage = currentLang,
                        tasks = calendarTasks,
                        onAddTask = { viewModel.addCalendarTask(it) },
                        onUpdateTask = { viewModel.updateCalendarTask(it) },
                        onDeleteTask = { viewModel.deleteCalendarTask(it) },
                        onStatusChanged = { task, newStatus ->
                            viewModel.updateCalendarTaskStatus(task, newStatus)
                        },
                        onDateChanged = { task, newDate ->
                            viewModel.updateCalendarTaskDate(task, newDate)
                        }
                    )

                    "media" -> MediaLibraryScreen(
                        currentLanguage = currentLang,
                        mediaItems = mediaItems,
                        onAddMedia = { viewModel.addMedia(it) },
                        onDeleteMedia = { viewModel.deleteMedia(it) }
                    )

                    "analytics" -> AnalyticsScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        onUpdateSettings = { viewModel.saveSettings(it) }
                    )

                    "ai_tools" -> AiToolsScreen(
                        currentLanguage = currentLang
                    )

                    "social_channels" -> SocialChannelsScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        onNavigateToSettings = { viewModel.navigateTo("settings") },
                        onOpenPlatformLogin = { platform -> selectedPlatformForLogin = platform }
                    )

                    "backup_restore" -> BackupRestoreScreen(
                        currentLanguage = currentLang,
                        settings = settings,
                        projects = projects,
                        calendarTasks = calendarTasks,
                        mediaItems = mediaItems,
                        savedScripts = savedScripts,
                        onRestoreSampleData = { viewModel.seedSampleData() }
                    )

                    "settings" -> SettingsScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        onSaveSettings = { viewModel.saveSettings(it) },
                        onOpenWizard = { showWizardDialog = true }
                    )

                    else -> DashboardScreen(
                        settings = settings,
                        currentLanguage = currentLang,
                        projects = projects,
                        calendarTasks = calendarTasks,
                        onNavigate = { route -> viewModel.navigateTo(route) }
                    )
                }
            }
        }
    }

    // First Run Wizard Dialog
    if (showWizardDialog) {
        SetupWizardDialog(
            initialSettings = settings,
            onSaveAndFinish = { updated ->
                viewModel.saveSettings(updated)
                showWizardDialog = false
            },
            onDismiss = { showWizardDialog = false }
        )
    }

    // User Creator Login Dialog
    if (showUserLoginDialog) {
        UserLoginDialog(
            settings = settings,
            onLoginSuccess = { updated ->
                viewModel.saveSettings(updated)
                showUserLoginDialog = false
            },
            onDismiss = { showUserLoginDialog = false }
        )
    }

    // Social Platform Account Connection Dialog
    selectedPlatformForLogin?.let { platform ->
        AccountLoginDialog(
            platform = platform,
            settings = settings,
            currentLanguage = currentLang,
            onSaveSettings = { updated ->
                viewModel.saveSettings(updated)
                selectedPlatformForLogin = null
            },
            onDismiss = { selectedPlatformForLogin = null }
        )
    }
}
