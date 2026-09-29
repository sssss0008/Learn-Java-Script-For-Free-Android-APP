package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementItem
import com.example.data.model.UserProfile
import com.example.ui.components.BrandLogoView
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  achievements: List<AchievementItem>,
  onViewCertificate: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val completedLessons = userProfile.completedLessonIds.size
  val completedChallenges = userProfile.completedChallengeIds.size
  val completedProjects = userProfile.completedProjectIds.size

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Profile Header Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .background(
                Brush.linearGradient(listOf(ElectricBlue, PurpleAccent)),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = userProfile.name.take(1).uppercase(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 28.sp
            )
          }

          Spacer(Modifier.height(10.dp))
          Text(
            text = userProfile.name,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "JavaScript Developer • ${userProfile.experience}",
            style = MaterialTheme.typography.bodySmall,
            color = JsYellow
          )

          Spacer(Modifier.height(16.dp))

          // Key Stats Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            StatPill("🔥 ${userProfile.streakDays}", "Day Streak")
            StatPill("📚 $completedLessons", "Lessons")
            StatPill("⚡ $completedChallenges", "Challenges")
            StatPill("🛠️ $completedProjects", "Projects")
          }
        }
      }
    }

    // View Certificate Action Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onViewCertificate() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1D38)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(14.dp)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = JsYellow, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Column {
              Text("Official Course Certificate", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Issued by Awiskar Acharya • ID: ${userProfile.certificateId}", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
          }
          Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = ElectricBlue)
        }
      }
    }

    // Visual Skill Map Section
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("JAVASCRIPT SKILL GRAPH", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(10.dp))

          SkillNodeBar("Fundamentals & Syntax", 0.95f, NeonGreen)
          SkillNodeBar("Functions & Scope", 0.85f, ElectricBlue)
          SkillNodeBar("Array Algorithms", 0.80f, JsYellow)
          SkillNodeBar("DOM & Events", 0.70f, CyberCyan)
          SkillNodeBar("Asynchronous & Promises", 0.65f, PurpleAccent)
          SkillNodeBar("Event Loop & Internals", 0.60f, AmberOrange)
          SkillNodeBar("Closures & Prototypes", 0.55f, CoralRed)
        }
      }
    }

    // Achievements Section
    item {
      Text(
        text = "Achievements & Badges",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
    }

    items(achievements) { ach ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
          containerColor = if (ach.isUnlocked) Color(0xFF132822) else NavySurface
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (ach.isUnlocked) NeonGreen.copy(alpha = 0.5f) else NavyBorder
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(ach.iconEmoji, fontSize = 26.sp)
          Spacer(Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = ach.title,
              color = if (ach.isUnlocked) NeonGreen else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = ach.description,
              color = Color(0xFF94A3B8),
              fontSize = 12.sp
            )
          }
          if (ach.isUnlocked) {
            Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen)
          } else {
            Text("${ach.progressPercent}%", color = Color(0xFF64748B), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }

    // Creator & LinkedIn Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            BrandLogoView(compact = true, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(12.dp))
            Column {
              Text("About Awiskar Acharya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Creator & Software Engineer", color = JsYellow, fontSize = 11.sp)
            }
          }

          Spacer(Modifier.height(10.dp))
          Text(
            text = "“Learn. Code. Build. Master JavaScript.” Everything in this application is 100% free forever without subscriptions or paywalls.",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            lineHeight = 18.sp
          )

          Spacer(Modifier.height(14.dp))

          Button(
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/awiskaracharya/"))
                context.startActivity(intent)
              } catch (_: Exception) {}
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A66C2)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Message Awiskar Acharya on LinkedIn", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun StatPill(value: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
    Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp)
  }
}

@Composable
private fun SkillNodeBar(name: String, progress: Float, color: Color) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(name, color = Color(0xFFE2E8F0), fontSize = 12.sp)
      Text("${(progress * 100).toInt()}%", color = color, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
    Spacer(Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp),
      color = color,
      trackColor = NavySurfaceVariant
    )
  }
}

@Composable
fun CertificateScreen(
  userProfile: UserProfile,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
      }
      Text("Official Course Certificate", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }

    Spacer(Modifier.height(20.dp))

    // Certificate Plaque
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B36)),
      border = androidx.compose.foundation.BorderStroke(2.dp, JsYellow),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        BrandLogoView(modifier = Modifier.size(60.dp))
        Spacer(Modifier.height(14.dp))
        Text(
          text = "CERTIFICATE OF JAVASCRIPT MASTERY",
          color = JsYellow,
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )

        Spacer(Modifier.height(16.dp))
        Text("This certifies that", color = Color(0xFF94A3B8), fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Text(
          text = userProfile.name,
          color = Color.White,
          style = MaterialTheme.typography.displayMedium,
          fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))
        Text(
          text = "has successfully mastered the complete 29-level curriculum of",
          color = Color(0xFFCBD5E1),
          fontSize = 12.sp
        )
        Text(
          text = "Learn JavaScript",
          color = ElectricBlue,
          fontSize = 20.sp,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = "“Learn. Code. Build. Master JavaScript.”",
          color = JsYellow,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = Color(0xFF263963))

        Spacer(Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("CREATOR & INSTRUCTOR", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("Awiskar Acharya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("VERIFICATION ID", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text(userProfile.certificateId, color = NeonGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }

    Spacer(Modifier.height(20.dp))

    Button(
      onClick = {
        try {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/awiskaracharya/"))
          context.startActivity(intent)
        } catch (_: Exception) {}
      },
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A66C2)),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
    ) {
      Text("Share on LinkedIn / Message Awiskar", color = Color.White, fontWeight = FontWeight.Bold)
    }
  }
}
