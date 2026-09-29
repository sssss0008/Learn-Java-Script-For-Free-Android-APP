package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConsoleLogItem
import com.example.data.model.ExecutionResult
import com.example.data.model.LogType
import com.example.data.model.TestRunResult
import com.example.ui.theme.*

@Composable
fun ConsoleOutputView(
  result: ExecutionResult?,
  modifier: Modifier = Modifier,
  onClear: (() -> Unit)? = null
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = CodeBackground),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(NavySurface)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Terminal Console",
            style = CodeSnippetHeaderStyle,
            color = Color(0xFF94A3B8)
          )
          if (result != null && result.executionTimeMs > 0) {
            Spacer(Modifier.width(8.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = JsYellow,
                modifier = Modifier.size(12.dp)
              )
              Spacer(Modifier.width(4.dp))
              Text(
                text = "${result.executionTimeMs}ms",
                color = JsYellow,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        if (onClear != null) {
          TextButton(
            onClick = onClear,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text("Clear", color = Color(0xFF94A3B8), fontSize = 11.sp)
          }
        }
      }

      // Content
      if (result == null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "> Ready to execute. Click 'Run' to see output.",
            color = Color(0xFF64748B),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
          )
        }
      } else {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          // Logs
          result.logs.forEach { item ->
            val textColor = when (item.type) {
              LogType.ERROR -> CoralRed
              LogType.WARN -> AmberOrange
              LogType.INFO -> CyberCyan
              LogType.RESULT -> NeonGreen
              LogType.LOG -> Color(0xFFE2E8F0)
            }
            Row(
              modifier = Modifier.padding(vertical = 2.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text(
                text = "> ",
                color = ElectricBlue,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = item.message,
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )
            }
          }

          // Return value
          if (result.returnValue != null && result.returnValue != "undefined") {
            Row(
              modifier = Modifier
                .padding(top = 6.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Return: ",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = result.returnValue,
                color = NeonGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Error box if present
          if (result.error != null) {
            EducationalErrorCard(errorText = result.error)
          }
        }
      }
    }
  }
}

@Composable
fun EducationalErrorCard(errorText: String) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1215)),
    border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(8.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.BugReport,
          contentDescription = null,
          tint = CoralRed,
          modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
          text = "Runtime Error Detected",
          color = CoralRed,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
      Spacer(Modifier.height(6.dp))
      Text(
        text = errorText,
        color = Color(0xFFFCA5A5),
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = "💡 Tip: Check variable declarations, matching parentheses/brackets, or unhandled null/undefined values.",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun TestResultsView(
  testResults: List<TestRunResult>,
  modifier: Modifier = Modifier
) {
  val passedCount = testResults.count { it.isPassed }
  val totalCount = testResults.size

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Test Cases: $passedCount / $totalCount Passed",
        style = MaterialTheme.typography.titleMedium,
        color = if (passedCount == totalCount) NeonGreen else AmberOrange,
        fontWeight = FontWeight.Bold
      )
    }

    testResults.forEachIndexed { index, test ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (test.isPassed) Color(0xFF0F241D) else Color(0xFF291417)
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (test.isPassed) NeonGreen.copy(alpha = 0.5f) else CoralRed.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "${if (test.isPassed) "✓" else "✕"} Test ${index + 1}: ${test.description}",
              color = if (test.isPassed) NeonGreen else CoralRed,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            )
          }

          if (!test.isPassed) {
            Spacer(Modifier.height(6.dp))
            Row {
              Text(
                text = "Expected: ",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = test.expected,
                color = NeonGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
            Row {
              Text(
                text = "Actual:   ",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = test.actual,
                color = CoralRed,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }
  }
}
