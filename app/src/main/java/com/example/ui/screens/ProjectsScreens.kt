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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Web
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
import com.example.data.model.ProjectItem
import com.example.engine.JavaScriptEngine
import com.example.ui.components.CodeEditorView
import com.example.ui.components.ConsoleOutputView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProjectsScreen(
  projects: List<ProjectItem>,
  completedProjectIds: Set<String>,
  onSelectProject: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Column {
          Text(
            text = "Real-World Projects Center",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Build real production web applications from scratch",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8)
          )
        }
      }
    }

    items(projects) { project ->
      val isDone = completedProjectIds.contains(project.id)
      val difficultyColor = when (project.difficulty) {
        "Easy" -> NeonGreen
        "Medium" -> AmberOrange
        else -> CoralRed
      }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSelectProject(project.id) },
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
                  text = project.difficulty.uppercase(),
                  color = difficultyColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
              Spacer(Modifier.width(8.dp))
              Text("• ${project.category}", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }

            if (isDone) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("BUILT", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }

          Spacer(Modifier.height(8.dp))
          Text(
            text = project.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = project.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFCBD5E1)
          )

          Spacer(Modifier.height(10.dp))
          // Concepts Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            project.concepts.take(3).forEach { concept ->
              Box(
                modifier = Modifier
                  .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(concept, color = ElectricBlue, fontSize = 10.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProjectDetailScreen(
  project: ProjectItem,
  isCompleted: Boolean,
  onBack: () -> Unit,
  onProjectCompleted: () -> Unit,
  engine: JavaScriptEngine,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var projectCode by remember(project.id) { mutableStateOf(project.initialCode) }
  var execResult by remember(project.id) { mutableStateOf<ExecutionResult?>(null) }
  var isRunning by remember { mutableStateOf(false) }

  // Interactive Checklist
  val checkedItems = remember(project.id) { mutableStateMapOf<Int, Boolean>() }

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
          text = project.title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        Text(
          text = "${project.category} Project • ${project.difficulty}",
          color = Color(0xFF94A3B8),
          fontSize = 11.sp
        )
      }
    }

    // Scrollable Workspace
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Requirements & Specs
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("PROJECT REQUIREMENTS", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(6.dp))
          Text(project.description, color = Color.White, fontSize = 13.sp)
          Spacer(Modifier.height(8.dp))
          project.requirements.forEach { req ->
            Text("• $req", color = Color(0xFFCBD5E1), fontSize = 12.sp, modifier = Modifier.padding(vertical = 1.dp))
          }
        }
      }

      // 2. Simulated Live Webpage Preview
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF1E293B))
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Web, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Live Webpage Simulator (localhost:3000)", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
              .background(Color(0xFF1E2433), RoundedCornerShape(8.dp))
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
              .padding(16.dp)
          ) {
            Column {
              Text(
                text = "✨ Interactive App Preview",
                color = JsYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Spacer(Modifier.height(6.dp))
              Text(
                text = "Output generated from JavaScript engine state:",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
              Spacer(Modifier.height(8.dp))
              Text(
                text = if (execResult?.returnValue != null) "State: ${execResult?.returnValue}" else "Status: Running active script logic",
                color = NeonGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      // 3. Code Editor
      Column {
        Text("PROJECT JAVASCRIPT CODE:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        CodeEditorView(
          code = projectCode,
          onCodeChange = { projectCode = it },
          onRun = {
            isRunning = true
            scope.launch {
              execResult = engine.execute(projectCode)
              isRunning = false
            }
          },
          onReset = {
            projectCode = project.initialCode
            execResult = null
          },
          isRunning = isRunning,
          title = "app.js"
        )

        Spacer(Modifier.height(8.dp))
        ConsoleOutputView(result = execResult, onClear = { execResult = null })
      }

      // 4. Checklist & Submission
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("PROJECT COMPLETION CHECKLIST", color = JsYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(8.dp))

          project.checklist.forEachIndexed { idx, item ->
            val isChecked = checkedItems[idx] == true
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { checkedItems[idx] = !isChecked }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = isChecked,
                onCheckedChange = { checkedItems[idx] = it },
                colors = CheckboxDefaults.colors(checkedColor = NeonGreen, checkmarkColor = NavyDeep)
              )
              Spacer(Modifier.width(8.dp))
              Text(item, color = if (isChecked) Color(0xFF94A3B8) else Color.White, fontSize = 13.sp)
            }
          }

          Spacer(Modifier.height(10.dp))

          Button(
            onClick = onProjectCompleted,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isCompleted) Color(0xFF1E293B) else NeonGreen
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isCompleted) NeonGreen else NavyDeep)
            Spacer(Modifier.width(6.dp))
            Text(
              text = if (isCompleted) "Project Completed" else "Mark Project as Completed",
              color = if (isCompleted) Color.White else NavyDeep,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
