package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Challenge
import com.example.data.model.ExecutionResult
import com.example.engine.JavaScriptEngine
import com.example.ui.components.CodeEditorView
import com.example.ui.components.ConsoleOutputView
import com.example.ui.components.TestResultsView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PracticeScreen(
  challenges: List<Challenge>,
  completedChallengeIds: Set<String>,
  onSelectChallenge: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }
  val categories = listOf("All", "Arrays", "Strings", "Closures")

  val filteredChallenges = if (selectedCategory == "All") challenges
  else challenges.filter { it.category == selectedCategory }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "Practice & Challenges",
          style = MaterialTheme.typography.headlineLarge,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Solve algorithm challenges, pass unit tests, and earn points",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF94A3B8)
        )

        Spacer(Modifier.height(14.dp))

        // Category Filter Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = cat == selectedCategory
            Box(
              modifier = Modifier
                .background(
                  if (isSelected) ElectricBlue else Color(0xFF1E293B),
                  RoundedCornerShape(8.dp)
                )
                .clickable { selectedCategory = cat }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                color = if (isSelected) NavyDeep else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    items(filteredChallenges) { challenge ->
      val isDone = completedChallengeIds.contains(challenge.id)
      val difficultyColor = when (challenge.difficulty) {
        "Beginner" -> NeonGreen
        "Intermediate" -> AmberOrange
        else -> CoralRed
      }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSelectChallenge(challenge.id) },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isDone) NeonGreen.copy(alpha = 0.6f) else NavyBorder
        ),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .background(difficultyColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = challenge.difficulty.uppercase(),
                  color = difficultyColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
              Spacer(Modifier.width(8.dp))
              Text(
                text = "• ${challenge.category}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }

            if (isDone) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("PASSED", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            } else {
              Text(
                text = "${challenge.points} PTS",
                color = JsYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          Spacer(Modifier.height(8.dp))
          Text(
            text = challenge.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = challenge.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFCBD5E1),
            maxLines = 2
          )
        }
      }
    }
  }
}

@Composable
fun ChallengeDetailScreen(
  challenge: Challenge,
  isCompleted: Boolean,
  onBack: () -> Unit,
  onChallengePassed: () -> Unit,
  engine: JavaScriptEngine,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var userCode by remember(challenge.id) { mutableStateOf(challenge.initialCode) }
  var testResult by remember(challenge.id) { mutableStateOf<ExecutionResult?>(null) }
  var isTesting by remember { mutableStateOf(false) }

  // Hint level
  var currentHintIndex by remember { mutableIntStateOf(0) }
  var showSolution by remember { mutableStateOf(false) }

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
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
      }
      Column {
        Text(
          text = challenge.title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        Text(
          text = "${challenge.category} • ${challenge.difficulty} (${challenge.points} pts)",
          color = Color(0xFF94A3B8),
          fontSize = 11.sp
        )
      }
    }

    // Scrollable problem description & IDE
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Problem statement
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "PROBLEM DESCRIPTION",
            color = ElectricBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
          Spacer(Modifier.height(6.dp))
          Text(
            text = challenge.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            lineHeight = 22.sp
          )

          Spacer(Modifier.height(10.dp))
          Text("Examples:", color = JsYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          challenge.examples.forEach { ex ->
            Text(
              text = "• $ex",
              color = Color(0xFFCBD5E1),
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // Progressive Hint System
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141F38)),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsYellow.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = JsYellow, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(6.dp))
              Text("Progressive Hints", color = JsYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            if (currentHintIndex < challenge.hints.size) {
              TextButton(onClick = { currentHintIndex++ }) {
                Text("Reveal Hint ${currentHintIndex + 1}", color = ElectricBlue, fontSize = 11.sp)
              }
            }
          }

          for (i in 0 until currentHintIndex) {
            Text(
              text = "Hint ${i + 1}: ${challenge.hints[i]}",
              color = Color(0xFFE2E8F0),
              fontSize = 12.sp,
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }

          if (currentHintIndex >= challenge.hints.size && !showSolution) {
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = { showSolution = true }) {
              Text("Show Reference Solution", color = AmberOrange, fontSize = 11.sp)
            }
          }

          if (showSolution) {
            Spacer(Modifier.height(6.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = CodeBackground)
            ) {
              Text(
                text = challenge.solutionCode,
                color = NeonGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }

      // Code Editor
      Column {
        Text("WRITE YOUR SOLUTION:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        CodeEditorView(
          code = userCode,
          onCodeChange = { userCode = it },
          onRun = {
            isTesting = true
            scope.launch {
              testResult = engine.runTests(userCode, challenge.testCases)
              isTesting = false
              if (testResult?.testResults?.all { it.isPassed } == true) {
                onChallengePassed()
              }
            }
          },
          onReset = {
            userCode = challenge.initialCode
            testResult = null
          },
          isRunning = isTesting,
          title = "solution.js"
        )
      }

      // Automated Test Suite Results
      if (testResult != null) {
        if (testResult!!.testResults.isNotEmpty()) {
          TestResultsView(testResults = testResult!!.testResults)
        }
        ConsoleOutputView(result = testResult)
      }
    }
  }
}
