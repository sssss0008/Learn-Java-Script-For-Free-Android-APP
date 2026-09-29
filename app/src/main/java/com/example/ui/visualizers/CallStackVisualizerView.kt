package com.example.ui.visualizers

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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

data class StackFrame(
  val functionName: String,
  val parameters: String,
  val localVariables: String,
  val lineNum: Int
)

@Composable
fun CallStackVisualizerView(
  modifier: Modifier = Modifier
) {
  var step by remember { mutableIntStateOf(0) }

  val frames = when (step) {
    0 -> listOf(StackFrame("main()", "global", "total = undefined", 1))
    1 -> listOf(
      StackFrame("calculateTotal(100)", "price = 100", "tax = undefined", 2),
      StackFrame("main()", "global", "total = undefined", 1)
    )
    2 -> listOf(
      StackFrame("calculateTax(100)", "amount = 100", "rate = 0.15", 3),
      StackFrame("calculateTotal(100)", "price = 100", "tax = undefined", 2),
      StackFrame("main()", "global", "total = undefined", 1)
    )
    3 -> listOf(
      // calculateTax returns 15
      StackFrame("calculateTotal(100)", "price = 100", "tax = 15", 4),
      StackFrame("main()", "global", "total = undefined", 1)
    )
    4 -> listOf(
      // calculateTotal returns 115
      StackFrame("main()", "global", "total = 115", 5)
    )
    else -> listOf(StackFrame("main()", "global", "total = 115", 6))
  }

  val stepExpl = listOf(
    "Step 0: Global execution context created. 'main()' begins execution.",
    "Step 1: 'calculateTotal(100)' is called. A new stack frame is pushed on top of main().",
    "Step 2: 'calculateTotal()' calls 'calculateTax(100)'. Another frame is pushed on top.",
    "Step 3: 'calculateTax()' computes 15 and returns. Its stack frame pops off!",
    "Step 4: 'calculateTotal()' receives 15, calculates total = 115 and returns. Frame pops off!",
    "Step 5: Execution returns to 'main()' with the result 115. Stack is complete!"
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
            text = "Call Stack Simulator",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
          )
          Text(
            text = "LIFO (Last In, First Out) Stack Frames",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
        Row {
          IconButton(onClick = { step = 0 }) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color(0xFF94A3B8))
          }
          Button(
            onClick = { step = if (step < 5) step + 1 else 0 },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (step < 5) "Step" else "Restart", color = NavyDeep, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(Modifier.height(10.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A4A)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(
          text = stepExpl[step],
          color = JsYellow,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(10.dp)
        )
      }

      Spacer(Modifier.height(14.dp))

      // The Call Stack Column (showing top frame at top)
      Text(
        text = "ACTIVE CALL STACK (TOP TO BOTTOM):",
        color = Color(0xFF64748B),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(Modifier.height(6.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF090E1A), RoundedCornerShape(10.dp))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        frames.forEachIndexed { idx, frame ->
          val isTop = idx == 0
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                1.5.dp,
                if (isTop) NeonGreen else ElectricBlue.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
              ),
            colors = CardDefaults.cardColors(
              containerColor = if (isTop) Color(0xFF132A22) else Color(0xFF14213D)
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (isTop) {
                    Text(
                      text = "▶ RUNNING: ",
                      color = NeonGreen,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace
                    )
                  }
                  Text(
                    text = frame.functionName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                  text = "Params: ${frame.parameters} | Locals: ${frame.localVariables}",
                  color = Color(0xFF94A3B8),
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
              Text(
                text = "Line ${frame.lineNum}",
                color = if (isTop) JsYellow else Color(0xFF64748B),
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
