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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
  userProfile: UserProfile,
  currentTheme: AppThemeMode,
  onThemeSelected: (AppThemeMode) -> Unit,
  onUpdateDailyGoal: (Int) -> Unit,
  onResetProgress: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showResetDialog by remember { mutableStateOf(false) }

  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text("Reset All Progress?", color = Color.White) },
      text = { Text("This will clear all completed lessons, challenge scores, and custom notes. This action cannot be undone.", color = Color(0xFFCBD5E1)) },
      containerColor = NavySurface,
      confirmButton = {
        Button(
          onClick = {
            showResetDialog = false
            onResetProgress()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
        ) {
          Text("Reset Everything", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel", color = Color(0xFF94A3B8))
        }
      }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .verticalScroll(rememberScrollState())
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
      Text("Settings & Preferences", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }

    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Theme Options (Item 9)
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("APPEARANCE THEME", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(10.dp))

          val themes = listOf(
            AppThemeMode.DARK to "Dark (Developer Navy)",
            AppThemeMode.LIGHT to "Light (Clean Workspace)",
            AppThemeMode.AMOLED to "AMOLED (Pure Black)",
            AppThemeMode.HIGH_CONTRAST to "High Contrast"
          )

          themes.forEach { (mode, title) ->
            val isSelected = currentTheme == mode
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onThemeSelected(mode) }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { onThemeSelected(mode) },
                colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
              )
              Spacer(Modifier.width(8.dp))
              Text(title, color = Color.White, fontSize = 13.sp)
            }
          }
        }
      }

      // Daily Learning Goal
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("DAILY LEARNING GOAL", color = JsYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(10, 20, 30, 60).forEach { mins ->
              val isSelected = userProfile.dailyGoalMinutes == mins
              Box(
                modifier = Modifier
                  .weight(1f)
                  .background(
                    if (isSelected) JsYellow else Color(0xFF1E293B),
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { onUpdateDailyGoal(mins) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${mins}m",
                  color = if (isSelected) NavyDeep else Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }
      }

      // Reset Data Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF201316)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("DATA MANAGEMENT", color = CoralRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(6.dp))
          Text(
            text = "Reset all lesson progress, challenge scores, and stored notes to start fresh.",
            color = Color(0xFFFCA5A5),
            fontSize = 12.sp
          )
          Spacer(Modifier.height(12.dp))
          Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(6.dp))
            Text("Reset Progress", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
