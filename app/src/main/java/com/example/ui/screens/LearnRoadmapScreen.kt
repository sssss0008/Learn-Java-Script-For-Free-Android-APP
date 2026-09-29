package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Module
import com.example.ui.components.ConceptBadgeIcon
import com.example.ui.theme.*

@Composable
fun LearnRoadmapScreen(
  modules: List<Module>,
  completedLessonIds: Set<String>,
  onSelectLesson: (String) -> Unit,
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
      Column {
        Text(
          text = "JavaScript Roadmap",
          style = MaterialTheme.typography.headlineLarge,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "29 Levels from Absolute Zero to Senior Mastery",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF94A3B8)
        )
        Spacer(Modifier.height(8.dp))
        Text(
          text = "${completedLessonIds.size} lessons completed",
          color = NeonGreen,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
      }
    }

    items(modules) { module ->
      val firstLessonId = module.lessons.firstOrNull()?.id ?: ""
      val isModuleCompleted = module.lessons.isNotEmpty() && module.lessons.all { completedLessonIds.contains(it.id) }
      val isModuleInProgress = module.lessons.any { completedLessonIds.contains(it.id) } && !isModuleCompleted

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            if (firstLessonId.isNotEmpty()) onSelectLesson(firstLessonId)
          },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isModuleCompleted) NeonGreen.copy(alpha = 0.6f)
          else if (isModuleInProgress) ElectricBlue.copy(alpha = 0.6f)
          else NavyBorder
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
              ConceptBadgeIcon(type = module.iconType, size = 36.dp)
              Spacer(Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "LEVEL ${module.level}",
                    color = if (isModuleCompleted) NeonGreen else ElectricBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                  if (isModuleCompleted) {
                    Spacer(Modifier.width(6.dp))
                    Text("• COMPLETED", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
                Text(
                  text = module.title,
                  style = MaterialTheme.typography.titleMedium,
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            if (isModuleCompleted) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .background(NeonGreen.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
              }
            } else {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .background(ElectricBlue.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
              }
            }
          }

          Spacer(Modifier.height(8.dp))
          Text(
            text = module.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )

          // Lessons list preview inside level card
          if (module.lessons.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              module.lessons.forEach { lesson ->
                val isDone = completedLessonIds.contains(lesson.id)
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                    .clickable { onSelectLesson(lesson.id) }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isDone) "✓" else "○", color = if (isDone) NeonGreen else Color(0xFF64748B), fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(lesson.title, color = Color(0xFFE2E8F0), fontSize = 12.sp)
                  }
                  Text("${lesson.durationMinutes}m", color = Color(0xFF64748B), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
              }
            }
          }
        }
      }
    }
  }
}
