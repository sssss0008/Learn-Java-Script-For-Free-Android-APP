package com.example.data.model

data class UserProfile(
  val name: String = "JavaScript Learner",
  val experience: String = "Beginner",
  val learningGoals: List<String> = listOf("Learn JavaScript from zero", "Build projects"),
  val completedLessonIds: Set<String> = emptySet(),
  val completedChallengeIds: Set<String> = emptySet(),
  val completedProjectIds: Set<String> = emptySet(),
  val streakDays: Int = 3,
  val currentLessonId: String = "m1_l1",
  val dailyGoalMinutes: Int = 20,
  val bookmarkedIds: Set<String> = emptySet(),
  val isOnboardingCompleted: Boolean = false,
  val certificateId: String = "JS-MASTER-AWISKAR-2026-9812"
)

data class Module(
  val id: String,
  val level: Int,
  val title: String,
  val description: String,
  val iconType: String,
  val lessons: List<Lesson> = emptyList()
)

data class Lesson(
  val id: String,
  val moduleId: String,
  val title: String,
  val description: String,
  val durationMinutes: Int,
  val whatIsIt: String,
  val whyExists: String,
  val howItWorks: String,
  val initialCode: String,
  val expectedOutput: String,
  val visualConceptType: VisualizerType? = null,
  val commonMistakes: String,
  val interviewTip: String,
  val practiceQuestion: PracticeQuestion? = null
)

data class PracticeQuestion(
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)

enum class VisualizerType(val title: String, val subtitle: String) {
  CALL_STACK("Call Stack Visualizer", "Watch function frames push, execute, and pop"),
  EVENT_LOOP("Event Loop Visualizer", "Call Stack, Web APIs, Microtasks & Callback Queue"),
  VARIABLES("Variables & Memory", "Primitives vs References & Value Mutation"),
  ARRAY_METHODS("Array Transformation Lab", "map, filter, reduce & mutating methods"),
  CLOSURE("Closure & Scope Lab", "Lexical environments & retained memory state"),
  PROMISES("Promise State Machine", "Pending -> Fulfilled / Rejected lifecycle"),
  DOM_EVENTS("DOM Tree & Event Flow", "Hierarchy, Bubbling, and Capturing"),
  DEBOUNCE_THROTTLE("Debounce & Throttle Lab", "Event frequency & performance timing"),
  PROTOTYPE("Prototype Chain", "Object inheritance -> null resolution")
}

data class Challenge(
  val id: String,
  val title: String,
  val difficulty: String, // Beginner, Intermediate, Advanced
  val category: String, // Fundamentals, Functions, Arrays, Objects, Async
  val description: String,
  val examples: List<String>,
  val constraints: List<String>,
  val hints: List<String>,
  val initialCode: String,
  val testCases: List<ChallengeTestCase>,
  val solutionCode: String,
  val points: Int = 100
)

data class ChallengeTestCase(
  val description: String,
  val testCode: String,
  val expectedOutput: String
)

data class ProjectItem(
  val id: String,
  val title: String,
  val category: String, // Beginner, Intermediate, Advanced
  val difficulty: String,
  val description: String,
  val concepts: List<String>,
  val requirements: List<String>,
  val initialCode: String,
  val solutionCode: String,
  val checklist: List<String>,
  val simulatedHtml: String
)

data class InterviewItem(
  val id: String,
  val category: String,
  val difficulty: String,
  val question: String,
  val thinkPrompt: String,
  val hint: String,
  val answer: String,
  val codeExample: String,
  val followUp: String
)

data class ReferenceItem(
  val id: String,
  val name: String,
  val category: String, // Arrays, Strings, Objects, DOM, Async, ES6+
  val syntax: String,
  val summary: String,
  val exampleCode: String,
  val returnType: String,
  val gotcha: String
)

data class ExecutionResult(
  val logs: List<ConsoleLogItem> = emptyList(),
  val returnValue: String? = null,
  val error: String? = null,
  val executionTimeMs: Long = 0,
  val testResults: List<TestRunResult> = emptyList()
)

data class TestRunResult(
  val description: String,
  val isPassed: Boolean,
  val expected: String,
  val actual: String
)

data class ConsoleLogItem(
  val type: LogType,
  val message: String
)

enum class LogType {
  LOG, ERROR, WARN, INFO, RESULT
}

data class AchievementItem(
  val id: String,
  val title: String,
  val description: String,
  val iconEmoji: String,
  val isUnlocked: Boolean = false,
  val progressPercent: Int = 0
)

data class NoteItem(
  val id: String,
  val lessonId: String,
  val lessonTitle: String,
  val content: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class SnippetItem(
  val id: String,
  val title: String,
  val category: String,
  val code: String,
  val explanation: String
)
