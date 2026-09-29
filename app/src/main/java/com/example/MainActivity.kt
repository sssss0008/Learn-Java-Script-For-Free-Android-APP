package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.repository.JavaScriptRepository
import com.example.engine.JavaScriptEngine
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.MainTab
import com.example.ui.navigation.ScreenRoute
import com.example.ui.screens.*
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LearnJsTheme
import com.example.ui.theme.NavyBackground

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = JavaScriptRepository(applicationContext)
    val engine = JavaScriptEngine(applicationContext)

    setContent {
      val userProfile by repository.userProfile.collectAsState()
      var currentTheme by remember { mutableStateOf(AppThemeMode.DARK) }
      var currentRoute by remember {
        mutableStateOf<ScreenRoute>(
          if (userProfile.isOnboardingCompleted) ScreenRoute.Home else ScreenRoute.Splash
        )
      }
      var currentTab by remember { mutableStateOf(MainTab.HOME) }
      val achievements = remember(userProfile) { repository.getAchievements() }

      LearnJsTheme(themeMode = currentTheme) {
        // Handle Android System Back button
        BackHandler(enabled = currentRoute !is ScreenRoute.Home && currentRoute !is ScreenRoute.Splash && currentRoute !is ScreenRoute.Onboarding) {
          currentRoute = when (currentTab) {
            MainTab.HOME -> ScreenRoute.Home
            MainTab.LEARN -> ScreenRoute.LearnRoadmap
            MainTab.VISUAL_LABS -> ScreenRoute.VisualLabs
            MainTab.PRACTICE -> ScreenRoute.Practice
            MainTab.PROFILE -> ScreenRoute.Profile
          }
        }

        val showChrome = currentRoute !is ScreenRoute.Splash &&
            currentRoute !is ScreenRoute.Onboarding &&
            currentRoute !is ScreenRoute.LessonDetail &&
            currentRoute !is ScreenRoute.ChallengeDetail &&
            currentRoute !is ScreenRoute.ProjectDetail &&
            currentRoute !is ScreenRoute.AiTutor &&
            currentRoute !is ScreenRoute.Reference &&
            currentRoute !is ScreenRoute.Certificate &&
            currentRoute !is ScreenRoute.Settings

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = NavyBackground,
          topBar = {
            if (showChrome) {
              AppTopBar(
                title = "Learn JavaScript",
                onOpenSettings = { currentRoute = ScreenRoute.Settings },
                onOpenSearch = { currentRoute = ScreenRoute.Reference },
                streakDays = userProfile.streakDays
              )
            }
          },
          bottomBar = {
            if (showChrome) {
              AppBottomNav(
                currentTab = currentTab,
                onTabSelected = { tab ->
                  currentTab = tab
                  currentRoute = when (tab) {
                    MainTab.HOME -> ScreenRoute.Home
                    MainTab.LEARN -> ScreenRoute.LearnRoadmap
                    MainTab.VISUAL_LABS -> ScreenRoute.VisualLabs
                    MainTab.PRACTICE -> ScreenRoute.Practice
                    MainTab.PROFILE -> ScreenRoute.Profile
                  }
                }
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(NavyBackground)
              .padding(innerPadding)
          ) {
            when (val route = currentRoute) {
              is ScreenRoute.Splash -> {
                SplashScreen(
                  onFinishSplash = {
                    currentRoute = if (userProfile.isOnboardingCompleted) ScreenRoute.Home else ScreenRoute.Onboarding
                  }
                )
              }

              is ScreenRoute.Onboarding -> {
                OnboardingFlow(
                  onComplete = { name, exp, goals ->
                    repository.completeOnboarding(name, exp, goals)
                    currentRoute = ScreenRoute.Home
                  }
                )
              }

              is ScreenRoute.Home -> {
                HomeScreen(
                  userProfile = userProfile,
                  onContinueLesson = { lessonId ->
                    currentRoute = ScreenRoute.LessonDetail(lessonId)
                  },
                  onOpenDailyChallenge = { chId ->
                    currentRoute = ScreenRoute.ChallengeDetail(chId)
                  },
                  onOpenCodeLab = { currentRoute = ScreenRoute.CodeLab },
                  onOpenVisualLab = { type ->
                    currentRoute = ScreenRoute.VisualLabDetail(type)
                  },
                  onOpenProjects = { currentRoute = ScreenRoute.Projects },
                  onOpenAiTutor = { currentRoute = ScreenRoute.AiTutor }
                )
              }

              is ScreenRoute.LearnRoadmap -> {
                LearnRoadmapScreen(
                  modules = repository.modules,
                  completedLessonIds = userProfile.completedLessonIds,
                  onSelectLesson = { lessonId ->
                    currentRoute = ScreenRoute.LessonDetail(lessonId)
                  }
                )
              }

              is ScreenRoute.LessonDetail -> {
                val lesson = repository.getLesson(route.lessonId)
                if (lesson != null) {
                  val allLessons = repository.modules.flatMap { it.lessons }
                  val currentIndex = allLessons.indexOfFirst { it.id == lesson.id }
                  val nextLesson = if (currentIndex != -1 && currentIndex + 1 < allLessons.size) allLessons[currentIndex + 1] else null

                  LessonDetailScreen(
                    lesson = lesson,
                    isCompleted = userProfile.completedLessonIds.contains(lesson.id),
                    onBack = { currentRoute = ScreenRoute.LearnRoadmap },
                    onMarkCompleted = { repository.markLessonCompleted(lesson.id) },
                    onOpenVisualLab = { type -> currentRoute = ScreenRoute.VisualLabDetail(type) },
                    onNextLesson = if (nextLesson != null) {
                      { currentRoute = ScreenRoute.LessonDetail(nextLesson.id) }
                    } else null,
                    engine = engine
                  )
                } else {
                  currentRoute = ScreenRoute.LearnRoadmap
                }
              }

              is ScreenRoute.VisualLabs -> {
                VisualLabScreen(initialVisualizerType = "EVENT_LOOP")
              }

              is ScreenRoute.VisualLabDetail -> {
                VisualLabScreen(initialVisualizerType = route.type)
              }

              is ScreenRoute.CodeLab -> {
                CodeLabScreen(engine = engine)
              }

              is ScreenRoute.Practice -> {
                PracticeScreen(
                  challenges = repository.challenges,
                  completedChallengeIds = userProfile.completedChallengeIds,
                  onSelectChallenge = { chId ->
                    currentRoute = ScreenRoute.ChallengeDetail(chId)
                  }
                )
              }

              is ScreenRoute.ChallengeDetail -> {
                val challenge = repository.getChallenge(route.challengeId)
                if (challenge != null) {
                  ChallengeDetailScreen(
                    challenge = challenge,
                    isCompleted = userProfile.completedChallengeIds.contains(challenge.id),
                    onBack = { currentRoute = ScreenRoute.Practice },
                    onChallengePassed = { repository.markChallengeCompleted(challenge.id) },
                    engine = engine
                  )
                } else {
                  currentRoute = ScreenRoute.Practice
                }
              }

              is ScreenRoute.Projects -> {
                ProjectsScreen(
                  projects = repository.projects,
                  completedProjectIds = userProfile.completedProjectIds,
                  onSelectProject = { projId ->
                    currentRoute = ScreenRoute.ProjectDetail(projId)
                  },
                  onBack = { currentRoute = ScreenRoute.Home }
                )
              }

              is ScreenRoute.ProjectDetail -> {
                val project = repository.getProject(route.projectId)
                if (project != null) {
                  ProjectDetailScreen(
                    project = project,
                    isCompleted = userProfile.completedProjectIds.contains(project.id),
                    onBack = { currentRoute = ScreenRoute.Projects },
                    onProjectCompleted = { repository.markProjectCompleted(project.id) },
                    engine = engine
                  )
                } else {
                  currentRoute = ScreenRoute.Projects
                }
              }

              is ScreenRoute.Interview -> {
                InterviewScreen(
                  questions = repository.interviewQuestions,
                  onBack = { currentRoute = ScreenRoute.Home }
                )
              }

              is ScreenRoute.AiTutor -> {
                AiTutorScreen(
                  onBack = { currentRoute = ScreenRoute.Home },
                  onOpenVisualLab = { type -> currentRoute = ScreenRoute.VisualLabDetail(type) }
                )
              }

              is ScreenRoute.Reference -> {
                ReferenceScreen(
                  referenceItems = repository.referenceItems,
                  glossary = repository.glossary,
                  onBack = { currentRoute = ScreenRoute.Home }
                )
              }

              is ScreenRoute.Profile -> {
                ProfileScreen(
                  userProfile = userProfile,
                  achievements = achievements,
                  onViewCertificate = { currentRoute = ScreenRoute.Certificate },
                  onOpenSettings = { currentRoute = ScreenRoute.Settings }
                )
              }

              is ScreenRoute.Certificate -> {
                CertificateScreen(
                  userProfile = userProfile,
                  onBack = { currentRoute = ScreenRoute.Profile }
                )
              }

              is ScreenRoute.Settings -> {
                SettingsScreen(
                  userProfile = userProfile,
                  currentTheme = currentTheme,
                  onThemeSelected = { currentTheme = it },
                  onUpdateDailyGoal = { repository.updateDailyGoal(it) },
                  onResetProgress = { repository.resetAllProgress() },
                  onBack = { currentRoute = ScreenRoute.Home }
                )
              }
            }
          }
        }
      }
    }
  }
}
