package com.example.ui.visualizers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EventLoopVisualizerView(
  modifier: Modifier = Modifier
) {
  // Simulation Steps:
  // Step 0: Initial code loaded
  // Step 1: console.log('A') pushes to stack and executes
  // Step 2: setTimeout(..., 0) pushes to stack, schedules Web API timer
  // Step 3: Promise.resolve().then(...) registers microtask
  // Step 4: console.log('B') pushes to stack and executes
  // Step 5: Stack is empty! Event Loop checks Microtask Queue -> runs Promise callback
  // Step 6: Microtask queue drained! Event Loop takes Macrotask (setTimeout) -> logs 'Timeout'
  var currentStep by remember { mutableIntStateOf(0) }

  val callStackItems = when (currentStep) {
    1 -> listOf("console.log('A')")
    2 -> listOf("setTimeout(fn, 0)")
    3 -> listOf("Promise.resolve().then(...)")
    4 -> listOf("console.log('B')")
    5 -> listOf("promiseCallback() [Microtask]")
    6 -> listOf("timerCallback() [Macrotask]")
    else -> emptyList()
  }

  val webApiItems = when (currentStep) {
    2, 3, 4 -> listOf("Timer (0ms)")
    else -> emptyList()
  }

  val microtasks = when (currentStep) {
    3, 4 -> listOf("console.log('Promise')")
    else -> emptyList()
  }

  val macrotasks = when (currentStep) {
    5 -> listOf("console.log('Timeout')")
    else -> emptyList()
  }

  val logs = when (currentStep) {
    0 -> emptyList()
    1 -> listOf("A")
    2 -> listOf("A")
    3 -> listOf("A")
    4 -> listOf("A", "B")
    5 -> listOf("A", "B", "Promise (Microtask)")
    6 -> listOf("A", "B", "Promise (Microtask)", "Timeout (Macrotask)")
    else -> listOf("A", "B", "Promise (Microtask)", "Timeout (Macrotask)")
  }

  val stepExplanations = listOf(
    "Step 0: Code loaded into the engine. Ready to execute line by line.",
    "Step 1: 'console.log('A')' is pushed to the Call Stack and prints 'A' immediately.",
    "Step 2: 'setTimeout' is pushed to stack. The engine delegates the timer to browser Web APIs.",
    "Step 3: 'Promise.resolve().then' is pushed. Its callback is placed in the high-priority Microtask Queue.",
    "Step 4: 'console.log('B')' is pushed to the Call Stack and prints 'B'. Synchronous code complete!",
    "Step 5: Call Stack is clear! Event Loop ticks, finds Microtask Queue has higher priority, and runs Promise callback.",
    "Step 6: Microtask Queue is empty! Event Loop takes the oldest Macrotask (setTimeout) and runs it."
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "JavaScript Event Loop Engine",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
          )
          Text(
            text = "Call Stack • Web APIs • Microtasks • Macrotasks",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
        Row {
          IconButton(onClick = { currentStep = 0 }) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color(0xFF94A3B8))
          }
          Button(
            onClick = {
              currentStep = if (currentStep < 6) currentStep + 1 else 0
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (currentStep < 6) "Step" else "Restart", color = NavyDeep, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(Modifier.height(12.dp))

      // Sample Code Box
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CodeBackground),
        shape = RoundedCornerShape(8.dp)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = """
console.log('A');
setTimeout(() => console.log('Timeout'), 0);
Promise.resolve().then(() => console.log('Promise'));
console.log('B');
            """.trimIndent(),
            color = Color(0xFFE2E8F0),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(Modifier.height(12.dp))

      // Current Step Explanation Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A4A)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(
          text = stepExplanations[currentStep],
          color = JsYellow,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(10.dp)
        )
      }

      Spacer(Modifier.height(14.dp))

      // 4 Queue Grid Containers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // 1. Call Stack (LIFO)
        QueueBox(
          title = "Call Stack",
          subtitle = "LIFO Thread",
          items = callStackItems,
          borderColor = ElectricBlue,
          modifier = Modifier.weight(1f)
        )

        // 2. Web APIs
        QueueBox(
          title = "Web APIs",
          subtitle = "Browser Background",
          items = webApiItems,
          borderColor = AmberOrange,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // 3. Microtask Queue (Promises - High Priority)
        QueueBox(
          title = "Microtasks",
          subtitle = "Promises (Priority 1)",
          items = microtasks,
          borderColor = PurpleAccent,
          modifier = Modifier.weight(1f)
        )

        // 4. Macrotask Queue (setTimeout - Priority 2)
        QueueBox(
          title = "Task Queue",
          subtitle = "Timers & I/O",
          items = macrotasks,
          borderColor = CyberCyan,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(Modifier.height(12.dp))

      // Output Screen
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Console Output: ",
          color = Color(0xFF94A3B8),
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = if (logs.isEmpty()) "(empty)" else logs.joinToString(" -> "),
          color = NeonGreen,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
private fun QueueBox(
  title: String,
  subtitle: String,
  items: List<String>,
  borderColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .height(100.dp)
      .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1322)),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
      Text(
        text = title,
        color = borderColor,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
      Text(
        text = subtitle,
        color = Color(0xFF64748B),
        fontSize = 9.sp
      )
      Spacer(Modifier.height(6.dp))

      if (items.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "Empty", color = Color(0xFF334155), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
      } else {
        items.forEach { item ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(borderColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 4.dp)
          ) {
            Text(
              text = item,
              color = Color.White,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}
