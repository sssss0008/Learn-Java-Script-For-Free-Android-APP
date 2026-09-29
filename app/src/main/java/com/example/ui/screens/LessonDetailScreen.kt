package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExecutionResult
import com.example.data.model.Lesson
import com.example.data.model.PracticeQuestion
import com.example.engine.JavaScriptEngine
import com.example.ui.components.CodeEditorView
import com.example.ui.components.ConsoleOutputView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LessonDetailScreen(
  lesson: Lesson,
  isCompleted: Boolean,
  onBack: () -> Unit,
  onMarkCompleted: () -> Unit,
  onOpenVisualLab: (String) -> Unit,
  onNextLesson: (() -> Unit)?,
  engine: JavaScriptEngine,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var editableCode by remember(lesson.id) { mutableStateOf(lesson.initialCode) }
  var executionResult by remember(lesson.id) { mutableStateOf<ExecutionResult?>(null) }
  var isRunning by remember { mutableStateOf(false) }

  // Practice question state
  var selectedQuizOption by remember(lesson.id) { mutableStateOf<Int?>(null) }
  var hasSubmittedQuiz by remember(lesson.id) { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(NavySurface)
        .windowInsetsPadding(WindowInsets.statusBars)
        .padding(horizontal = 8.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Column {
          Text(
            text = lesson.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = "${lesson.durationMinutes} min read & practice",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
        }
      }

      if (isCompleted) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
          Spacer(Modifier.width(4.dp))
          Text("Completed", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Scrollable Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. What is it?
      ConceptSectionCard(
        title = "1. WHAT IS IT?",
        titleColor = ElectricBlue,
        body = lesson.whatIsIt
      )

      // 2. Why does it exist?
      ConceptSectionCard(
        title = "2. WHY DOES IT EXIST?",
        titleColor = JsYellow,
        body = lesson.whyExists
      )

      // 3. How does it work?
      ConceptSectionCard(
        title = "3. HOW DOES IT WORK INTERNALLY?",
        titleColor = CyberCyan,
        body = lesson.howItWorks
      )

      // Visual Lab Launcher Button if visualizer available
      if (lesson.visualConceptType != null) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenVisualLab(lesson.visualConceptType.name) },
          colors = CardDefaults.cardColors(containerColor = Color(0xFF14243B)),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔬", fontSize = 20.sp)
              Spacer(Modifier.width(10.dp))
              Column {
                Text(
                  text = "Interactive Laboratory Available",
                  color = NeonGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = lesson.visualConceptType.title,
                  color = Color(0xFFCBD5E1),
                  fontSize = 11.sp
                )
              }
            }
            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = NeonGreen)
          }
        }
      }

      // 4. Interactive Code Playground
      Column {
        Text(
          text = "4. INTERACTIVE CODE PLAYGROUND",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace
        )
        Spacer(Modifier.height(8.dp))

        CodeEditorView(
          code = editableCode,
          onCodeChange = { editableCode = it },
          onRun = {
            isRunning = true
            scope.launch {
              executionResult = engine.execute(editableCode)
              isRunning = false
            }
          },
          onReset = {
            editableCode = lesson.initialCode
            executionResult = null
          },
          isRunning = isRunning,
          title = "interactive.js"
        )

        Spacer(Modifier.height(8.dp))

        ConsoleOutputView(
          result = executionResult,
          onClear = { executionResult = null }
        )
      }

      // 5. Practice Quiz Question
      if (lesson.practiceQuestion != null) {
        PracticeQuizCard(
          quiz = lesson.practiceQuestion,
          selectedIndex = selectedQuizOption,
          hasSubmitted = hasSubmittedQuiz,
          onSelectOption = { selectedQuizOption = it },
          onSubmit = { hasSubmittedQuiz = true }
        )
      }

      // 6. Common Mistakes to Avoid
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF241517)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("⚠️ COMMON MISTAKE TO AVOID", color = CoralRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(4.dp))
          Text(lesson.commonMistakes, color = Color(0xFFFCA5A5), fontSize = 12.sp, lineHeight = 18.sp)
        }
      }

      // 7. Technical Interview Tip
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1633)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("💼 INTERVIEW REVEAL", color = PurpleAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(4.dp))
          Text(lesson.interviewTip, color = Color(0xFFDDD6FE), fontSize = 12.sp, lineHeight = 18.sp)
        }
      }

      // Complete & Next Navigation
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onMarkCompleted,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isCompleted) Color(0xFF1E293B) else NeonGreen
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isCompleted) NeonGreen else NavyDeep)
          Spacer(Modifier.width(6.dp))
          Text(
            text = if (isCompleted) "Completed" else "Mark Complete",
            color = if (isCompleted) Color.White else NavyDeep,
            fontWeight = FontWeight.Bold
          )
        }

        if (onNextLesson != null) {
          Button(
            onClick = onNextLesson,
            colors = ButtonDefaults.buttonColors(containerColor = JsYellow),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Next Lesson", color = NavyDeep, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NavyDeep)
          }
        }
      }
    }
  }
}

@Composable
private fun ConceptSectionCard(title: String, titleColor: Color, body: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = title,
        color = titleColor,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFFF1F5F9),
        lineHeight = 22.sp
      )
    }
  }
}

@Composable
private fun PracticeQuizCard(
  quiz: PracticeQuestion,
  selectedIndex: Int?,
  hasSubmitted: Boolean,
  onSelectOption: (Int) -> Unit,
  onSubmit: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF111E38)),
    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text("KNOWLEDGE CHECKPOINT", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
      Spacer(Modifier.height(6.dp))
      Text(quiz.question, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

      Spacer(Modifier.height(10.dp))

      quiz.options.forEachIndexed { idx, opt ->
        val isSelected = selectedIndex == idx
        val isCorrect = idx == quiz.correctIndex
        val optionBg = if (hasSubmitted) {
          if (isCorrect) Color(0xFF064E3B) else if (isSelected) Color(0xFF7F1D1D) else Color(0xFF1E293B)
        } else {
          if (isSelected) Color(0xFF1E3A8A) else Color(0xFF1E293B)
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(optionBg, RoundedCornerShape(8.dp))
            .clickable(enabled = !hasSubmitted) { onSelectOption(idx) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(text = opt, color = Color.White, fontSize = 13.sp)
        }
      }

      Spacer(Modifier.height(10.dp))

      if (!hasSubmitted) {
        Button(
          onClick = onSubmit,
          enabled = selectedIndex != null,
          colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.align(Alignment.End)
        ) {
          Text("Submit Answer", color = NavyDeep, fontWeight = FontWeight.Bold)
        }
      } else {
        Text(
          text = if (selectedIndex == quiz.correctIndex) "✓ Correct! " + quiz.explanation else "✕ Incorrect. " + quiz.explanation,
          color = if (selectedIndex == quiz.correctIndex) NeonGreen else CoralRed,
          fontSize = 12.sp,
          lineHeight = 17.sp
        )
      }
    }
  }
}
