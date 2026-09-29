package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.BrandLogoView
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  onContinueLesson: (String) -> Unit,
  onOpenDailyChallenge: (String) -> Unit,
  onOpenCodeLab: () -> Unit,
  onOpenVisualLab: (String) -> Unit,
  onOpenProjects: () -> Unit,
  onOpenAiTutor: () -> Unit,
  modifier: Modifier = Modifier
) {
  val completedCount = userProfile.completedLessonIds.size
  val totalLessons = 29
  val progressPercent = ((completedCount.toFloat() / totalLessons.toFloat()) * 100).toInt().coerceAtMost(100)

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Header Greeting
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Good morning, ${userProfile.name}",
          style = MaterialTheme.typography.headlineLarge,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Continue your JavaScript journey.",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF94A3B8)
        )
      }
    }

    Spacer(Modifier.height(16.dp))

    // Hero Continue Learning Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onContinueLesson(userProfile.currentLessonId) },
      colors = CardDefaults.cardColors(containerColor = Color(0xFF132247)),
      border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.6f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .background(ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "CONTINUE LEARNING",
                color = ElectricBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
          Text(
            text = "$progressPercent% Complete",
            color = JsYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(Modifier.height(12.dp))

        Text(
          text = "Variables, Types & Execution Engines",
          style = MaterialTheme.typography.titleLarge,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Understand let, const, Temporal Dead Zone, and primitive vs object heap pointers.",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFFCBD5E1),
          maxLines = 2
        )

        Spacer(Modifier.height(14.dp))

        LinearProgressIndicator(
          progress = { progressPercent / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
          color = ElectricBlue,
          trackColor = NavySurfaceVariant
        )

        Spacer(Modifier.height(14.dp))

        Button(
          onClick = { onContinueLesson(userProfile.currentLessonId) },
          colors = ButtonDefaults.buttonColors(containerColor = JsYellow),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Continue Lesson", color = NavyDeep, fontWeight = FontWeight.Bold)
          Spacer(Modifier.width(6.dp))
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NavyDeep, modifier = Modifier.size(18.dp))
        }
      }
    }

    Spacer(Modifier.height(18.dp))

    // Daily Challenge Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenDailyChallenge("ch_avg") },
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1738)),
      border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .background(PurpleAccent.copy(alpha = 0.2f), CircleShape)
            .border(1.dp, PurpleAccent, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text("⚡", fontSize = 20.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("TODAY'S CHALLENGE", color = PurpleAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text("• 100 PTS", color = JsYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(Modifier.height(2.dp))
          Text(
            text = "Calculate Array Average",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Write a function using reduce() to find the average.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PurpleAccent)
      }
    }

    Spacer(Modifier.height(18.dp))

    // Quick Labs & IDE Grid
    Text(
      text = "Interactive Laboratories",
      style = MaterialTheme.typography.titleLarge,
      color = Color.White,
      fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Code Lab Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onOpenCodeLab() },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Terminal, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
          }
          Spacer(Modifier.height(10.dp))
          Text("Code Lab", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text("Playground & REPL", color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
      }

      // Visual Lab Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onOpenVisualLab("EVENT_LOOP") },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(CyberCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Science, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
          }
          Spacer(Modifier.height(10.dp))
          Text("Visual Lab", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text("Event Loop & Stack", color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
      }

      // AI Tutor Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onOpenAiTutor() },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(JsYellow.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = JsYellow, modifier = Modifier.size(20.dp))
          }
          Spacer(Modifier.height(10.dp))
          Text("AI Tutor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text("Code & Debug Help", color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
      }
    }

    Spacer(Modifier.height(18.dp))

    // Real-World Project Shortcut
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenProjects() },
      colors = CardDefaults.cardColors(containerColor = Color(0xFF131D38)),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      shape = RoundedCornerShape(16.dp)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Web, contentDescription = null, tint = ElectricBlue)
          }
          Spacer(Modifier.width(12.dp))
          Column {
            Text("Real-World Projects Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Build Counter, Weather, To-Do & Kanban", color = Color(0xFF94A3B8), fontSize = 12.sp)
          }
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = ElectricBlue)
      }
    }

    Spacer(Modifier.height(20.dp))

    // Creator & Free Education Guarantee Banner
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1322)),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("🎓 100% Free Forever", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(Modifier.width(8.dp))
          Text("• No Paywalls", color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
          text = "“Learn. Code. Build. Master JavaScript.”",
          color = Color(0xFFE2E8F0),
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
        Text(
          text = "Curriculum designed & built by Awiskar Acharya",
          color = JsYellow,
          fontSize = 11.sp
        )
      }
    }
  }
}
