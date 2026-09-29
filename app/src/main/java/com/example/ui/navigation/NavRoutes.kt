package com.example.ui.navigation

sealed class ScreenRoute {
  object Splash : ScreenRoute()
  object Onboarding : ScreenRoute()
  object Home : ScreenRoute()
  object LearnRoadmap : ScreenRoute()
  data class LessonDetail(val lessonId: String) : ScreenRoute()
  object VisualLabs : ScreenRoute()
  data class VisualLabDetail(val type: String) : ScreenRoute()
  object CodeLab : ScreenRoute()
  object Practice : ScreenRoute()
  data class ChallengeDetail(val challengeId: String) : ScreenRoute()
  object Projects : ScreenRoute()
  data class ProjectDetail(val projectId: String) : ScreenRoute()
  object Interview : ScreenRoute()
  object AiTutor : ScreenRoute()
  object Reference : ScreenRoute()
  object Profile : ScreenRoute()
  object Certificate : ScreenRoute()
  object Settings : ScreenRoute()
}

enum class MainTab {
  HOME, LEARN, VISUAL_LABS, PRACTICE, PROFILE
}
