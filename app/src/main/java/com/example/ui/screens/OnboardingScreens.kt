package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandLogoView
import com.example.ui.theme.*

@Composable
fun SplashScreen(
  onFinishSplash: () -> Unit
) {
  LaunchedEffect(Unit) {
    kotlinx.coroutines.delay(1200)
    onFinishSplash()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(NavyDeep, NavyBackground, Color(0xFF0F172A))
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      BrandLogoView(modifier = Modifier.size(90.dp))
      Spacer(Modifier.height(24.dp))
      Text(
        text = "Learn JavaScript",
        style = MaterialTheme.typography.displayMedium,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Learn. Code. Build. Master JavaScript.",
        style = MaterialTheme.typography.titleMedium,
        color = JsYellow,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(Modifier.height(16.dp))
      Text(
        text = "Created by Awiskar Acharya • 100% Free Forever",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )
    }
  }
}

@Composable
fun OnboardingFlow(
  onComplete: (name: String, experience: String, goals: List<String>) -> Unit
) {
  var step by remember { mutableIntStateOf(1) } // 1: Welcome, 2: Name, 3: Experience, 4: Goals, 5: Journey
  var name by remember { mutableStateOf("") }
  var experience by remember { mutableStateOf("Beginner") }
  val selectedGoals = remember { mutableStateListOf("Learn JavaScript from zero", "Build projects") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyBackground)
      .windowInsetsPadding(WindowInsets.safeDrawing)
  ) {
    when (step) {
      1 -> OnboardingWelcomeStep(onNext = { step = 2 })
      2 -> OnboardingNameStep(
        name = name,
        onNameChange = { name = it },
        onNext = { step = 3 }
      )
      3 -> OnboardingExperienceStep(
        selectedExperience = experience,
        onSelectExperience = { experience = it },
        onNext = { step = 4 }
      )
      4 -> OnboardingGoalsStep(
        selectedGoals = selectedGoals,
        onToggleGoal = { goal ->
          if (selectedGoals.contains(goal)) selectedGoals.remove(goal)
          else selectedGoals.add(goal)
        },
        onNext = { step = 5 }
      )
      5 -> OnboardingJourneyStep(
        name = if (name.isBlank()) "Developer" else name,
        onStart = {
          onComplete(name.ifBlank { "JavaScript Learner" }, experience, selectedGoals.toList())
        }
      )
    }
  }
}

@Composable
private fun OnboardingWelcomeStep(onNext: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Spacer(Modifier.height(20.dp))
      BrandLogoView(modifier = Modifier.size(80.dp))
      Spacer(Modifier.height(24.dp))
      Text(
        text = "Master JavaScript Visually",
        style = MaterialTheme.typography.displayMedium,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Spacer(Modifier.height(12.dp))
      Text(
        text = "Learn JavaScript from the fundamentals to advanced concepts through interactive coding, visual laboratories, debugging challenges, and real production projects.",
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFFCBD5E1),
        lineHeight = 24.sp
      )

      Spacer(Modifier.height(24.dp))

      // Feature highlights
      FeaturePill("⚡ Universal Learning Cycle: Explain -> Visualize -> Code -> Debug")
      Spacer(Modifier.height(8.dp))
      FeaturePill("🔬 Interactive Visual Labs for Call Stack, Event Loop & Closures")
      Spacer(Modifier.height(8.dp))
      FeaturePill("🛠️ Real-world Projects: Weather, To-Do, Quiz & Kanban")
      Spacer(Modifier.height(8.dp))
      FeaturePill("🎓 100% Free Forever • Created by Awiskar Acharya")
    }

    Button(
      onClick = onNext,
      colors = ButtonDefaults.buttonColors(containerColor = JsYellow),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    ) {
      Text("Start Learning", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      Spacer(Modifier.width(8.dp))
      Icon(Icons.Default.ArrowForward, contentDescription = null, tint = NavyDeep)
    }
  }
}

@Composable
private fun FeaturePill(text: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF131D38), RoundedCornerShape(10.dp))
      .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text, color = Color(0xFFE2E8F0), fontSize = 13.sp, lineHeight = 18.sp)
  }
}

@Composable
private fun OnboardingNameStep(
  name: String,
  onNameChange: (String) -> Unit,
  onNext: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Spacer(Modifier.height(30.dp))
      Text(
        text = "What should we call you?",
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Your name will be used on your profile and official course certificate.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(32.dp))

      OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        placeholder = { Text("Enter your name (e.g. Awiskar)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = ElectricBlue,
          unfocusedBorderColor = NavyBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedPlaceholderColor = Color(0xFF64748B),
          unfocusedPlaceholderColor = Color(0xFF64748B)
        ),
        shape = RoundedCornerShape(12.dp)
      )

      if (name.isNotBlank()) {
        Spacer(Modifier.height(16.dp))
        Text(
          text = "Nice to meet you, $name! Let's tailor your journey.",
          color = JsYellow,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
      }
    }

    Button(
      onClick = onNext,
      colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    ) {
      Text("Continue", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
  }
}

@Composable
private fun OnboardingExperienceStep(
  selectedExperience: String,
  onSelectExperience: (String) -> Unit,
  onNext: () -> Unit
) {
  val options = listOf(
    "Complete Beginner" to "I am brand new to programming and JavaScript.",
    "Beginner" to "I know basic HTML/CSS or another language.",
    "Intermediate" to "I can write basic JavaScript scripts and apps.",
    "Advanced" to "I want to master engine internals, closures & async."
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Spacer(Modifier.height(20.dp))
      Text(
        text = "How much JavaScript do you know?",
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "We will adapt code examples and explanations to your level.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(24.dp))

      options.forEach { (title, subtitle) ->
        val isSelected = selectedExperience == title
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(
              1.5.dp,
              if (isSelected) ElectricBlue else NavyBorder,
              RoundedCornerShape(12.dp)
            )
            .clickable { onSelectExperience(title) },
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF132A4A) else NavySurface
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = title,
                color = if (isSelected) ElectricBlue else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Spacer(Modifier.height(4.dp))
              Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
              )
            }
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = ElectricBlue)
            }
          }
        }
      }
    }

    Spacer(Modifier.height(16.dp))

    Button(
      onClick = onNext,
      colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    ) {
      Text("Continue", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
  }
}

@Composable
private fun OnboardingGoalsStep(
  selectedGoals: List<String>,
  onToggleGoal: (String) -> Unit,
  onNext: () -> Unit
) {
  val goals = listOf(
    "Learn JavaScript from zero",
    "Frontend & Web development",
    "React & Next.js preparation",
    "Node.js backend preparation",
    "Technical coding interviews",
    "Build real-world projects",
    "Master advanced async & closures"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Spacer(Modifier.height(20.dp))
      Text(
        text = "What are your learning goals?",
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Select all that apply to personalize your daily targets.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(20.dp))

      goals.forEach { goal ->
        val isSelected = selectedGoals.contains(goal)
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
              1.dp,
              if (isSelected) NeonGreen else NavyBorder,
              RoundedCornerShape(10.dp)
            )
            .clickable { onToggleGoal(goal) },
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF102A20) else NavySurface
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = goal,
              color = if (isSelected) NeonGreen else Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              modifier = Modifier.weight(1f)
            )
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen)
            }
          }
        }
      }
    }

    Spacer(Modifier.height(16.dp))

    Button(
      onClick = onNext,
      colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    ) {
      Text("Review Your Journey", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
  }
}

@Composable
private fun OnboardingJourneyStep(
  name: String,
  onStart: () -> Unit
) {
  val journeySteps = listOf(
    "1. JavaScript Basics & Engines",
    "2. Variables, Types & Memory",
    "3. Functions & Lexical Scope",
    "4. Arrays & Higher-Order Methods",
    "5. DOM Manipulation & Events",
    "6. Asynchronous JS & Promises",
    "7. Event Loop & Microtasks",
    "8. Closures & Prototypes",
    "9. Real-World Applications",
    "10. Interview Mastery & Certificate"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Spacer(Modifier.height(20.dp))
      Text(
        text = "Your JavaScript Journey",
        style = MaterialTheme.typography.displayMedium,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Prepared exclusively for $name by Awiskar Acharya.",
        style = MaterialTheme.typography.bodyMedium,
        color = JsYellow
      )

      Spacer(Modifier.height(20.dp))

      journeySteps.forEachIndexed { idx, stepText ->
        Row(
          modifier = Modifier.padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .background(ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
              .border(1.dp, ElectricBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text("${idx + 1}", color = ElectricBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(Modifier.width(10.dp))
          Text(
            text = stepText,
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    Spacer(Modifier.height(20.dp))

    Button(
      onClick = onStart,
      colors = ButtonDefaults.buttonColors(containerColor = JsYellow),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    ) {
      Text("Start My Journey", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      Spacer(Modifier.width(8.dp))
      Icon(Icons.Default.ArrowForward, contentDescription = null, tint = NavyDeep)
    }
  }
}
