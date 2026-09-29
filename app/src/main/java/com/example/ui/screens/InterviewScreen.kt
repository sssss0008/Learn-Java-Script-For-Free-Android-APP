package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterviewItem
import com.example.ui.theme.*

@Composable
fun InterviewScreen(
  questions: List<InterviewItem>,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }
  val categories = listOf("All", "Closures", "Event Loop", "Fundamentals", "Prototypes")
  val filtered = if (selectedCategory == "All") questions else questions.filter { it.category == selectedCategory }

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
            text = "JavaScript Interview Center",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Master technical screens at Google, Meta, Amazon & top startups",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8)
          )
        }
      }

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
                if (isSelected) PurpleAccent else Color(0xFF1E293B),
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

    items(filtered) { item ->
      InterviewQuestionExpandableCard(item = item)
    }
  }
}

@Composable
private fun InterviewQuestionExpandableCard(item: InterviewItem) {
  var isExpanded by remember { mutableStateOf(false) }
  var showHint by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded },
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
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
              .background(PurpleAccent.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = item.difficulty.uppercase(),
              color = PurpleAccent,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(Modifier.width(8.dp))
          Text("• ${item.category}", color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
        Text(
          text = if (isExpanded) "Collapse ▲" else "Expand ▼",
          color = ElectricBlue,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(Modifier.height(8.dp))
      Text(
        text = item.question,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )

      if (isExpanded) {
        Spacer(Modifier.height(14.dp))

        // How to Think About This
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF14243B)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
              Spacer(Modifier.width(6.dp))
              Text("HOW TO THINK ABOUT THIS", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(item.thinkPrompt, color = Color(0xFFCBD5E1), fontSize = 12.sp)
          }
        }

        Spacer(Modifier.height(8.dp))

        // Hint toggle
        if (!showHint) {
          TextButton(onClick = { showHint = true }) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = JsYellow, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Show Interview Hint", color = JsYellow, fontSize = 11.sp)
          }
        } else {
          Text("💡 Hint: ${item.hint}", color = JsYellow, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(Modifier.height(8.dp))

        // Detailed Answer
        Text("DETAILED TECHNICAL ANSWER:", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Text(item.answer, color = Color.White, fontSize = 13.sp, lineHeight = 20.sp)

        Spacer(Modifier.height(10.dp))

        // Code Example
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CodeBackground)
        ) {
          Text(
            text = item.codeExample,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(10.dp)
          )
        }

        Spacer(Modifier.height(10.dp))

        // Follow up question
        Text("FOLLOW-UP QUESTION:", color = AmberOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(item.followUp, color = Color(0xFFFDE68A), fontSize = 12.sp)
      }
    }
  }
}
