package com.example.ui.visualizers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.TouchApp
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

/**
 * 1. Variable & Memory Visualizer (Primitives vs Heap Objects & Value Mutation)
 */
@Composable
fun VariableMemoryVisualizerView(modifier: Modifier = Modifier) {
  var age by remember { mutableIntStateOf(22) }
  var userName by remember { mutableStateOf("Awiskar") }
  var isDeveloper by remember { mutableStateOf(true) }

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Variable & Memory Visualizer",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
      )
      Text(
        text = "Stack Allocation • Primitive Values • Mutable State",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { age += 1 },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("age++ ($age)", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Button(
          onClick = { isDeveloper = !isDeveloper },
          colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("Toggle Flag", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      Spacer(Modifier.height(14.dp))

      // Memory slot representations
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MemorySlotRow("let age", "$age", "Number (Primitive)")
        MemorySlotRow("const name", "\"$userName\"", "String (Primitive)")
        MemorySlotRow("let isDev", "$isDeveloper", "Boolean (Primitive)")
        MemorySlotRow("const userObj", "0x3F2A -> { id: 1 }", "Object (Heap Reference)")
      }

      Spacer(Modifier.height(10.dp))
      Text(
        text = "💡 When primitives are updated, their memory values are replaced directly. Objects store memory addresses pointing to heap allocations.",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp
      )
    }
  }
}

@Composable
private fun MemorySlotRow(label: String, value: String, typeDesc: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
      .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
      .padding(10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(label, color = ElectricBlue, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
      Text(typeDesc, color = Color(0xFF64748B), fontSize = 10.sp)
    }
    Text(
      text = value,
      color = JsYellow,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      fontSize = 13.sp
    )
  }
}

/**
 * 2. Closure & Lexical Scope Visualizer
 */
@Composable
fun ClosureScopeVisualizerView(modifier: Modifier = Modifier) {
  var count by remember { mutableIntStateOf(0) }

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Closure & Lexical Scope Lab",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
      )
      Text(
        text = "Inner Function Encloses and Retains Outer State in Memory",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(12.dp))

      // Nested scope containers: GLOBAL -> createCounter() -> inner counter()
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0C1322), RoundedCornerShape(12.dp))
          .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
          .padding(10.dp)
      ) {
        Column {
          Text("🌐 GLOBAL SCOPE: const myCounter = createCounter()", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(Modifier.height(8.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF131D38), RoundedCornerShape(10.dp))
              .border(1.dp, JsYellow, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Text("📦 OUTER LEXICAL SCOPE: createCounter()", color = JsYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("Enclosed variable: let count = $count (Retained in Heap!)", color = Color(0xFFE2E8F0), fontSize = 12.sp, fontFamily = FontFamily.Monospace)

              Spacer(Modifier.height(8.dp))

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFF1B2E24), RoundedCornerShape(8.dp))
                  .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("⚡ INNER FUNCTION: increment()", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Returns ++count -> Current value: $count", color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                  }
                  Button(
                    onClick = { count += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("Call increment()", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }

      Spacer(Modifier.height(10.dp))
      Text(
        text = "Even though createCounter() finished running long ago, the inner function preserves access to 'count' because of its closure.",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp
      )
    }
  }
}

/**
 * 3. Promise State Machine Visualizer
 */
@Composable
fun PromiseStateMachineView(modifier: Modifier = Modifier) {
  var promiseState by remember { mutableStateOf("PENDING") } // PENDING, FULFILLED, REJECTED

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Promise State Machine",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
      )
      Text(
        text = "Pending -> Fulfilled (.then) or Rejected (.catch)",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { promiseState = "PENDING" },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("Reset", color = Color.White, fontSize = 11.sp)
        }
        Button(
          onClick = { promiseState = "FULFILLED" },
          colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("resolve('Success')", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Button(
          onClick = { promiseState = "REJECTED" },
          colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("reject(Error)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      Spacer(Modifier.height(14.dp))

      // State Node
      val (stateColor, stateIcon, stateText) = when (promiseState) {
        "FULFILLED" -> Triple(NeonGreen, Icons.Default.Check, "FULFILLED: Value = 'Data Loaded' -> .then() executes")
        "REJECTED" -> Triple(CoralRed, Icons.Default.Close, "REJECTED: Error = 'Network Fail' -> .catch() executes")
        else -> Triple(AmberOrange, Icons.Default.HourglassTop, "PENDING: Waiting for asynchronous resolution...")
      }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, stateColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(imageVector = stateIcon, contentDescription = null, tint = stateColor, modifier = Modifier.size(24.dp))
          Spacer(Modifier.width(12.dp))
          Column {
            Text(text = "STATE: $promiseState", color = stateColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = stateText, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }
  }
}

/**
 * 4. DOM Tree & Event Flow Lab
 */
@Composable
fun DomEventFlowLabView(modifier: Modifier = Modifier) {
  var isBubblingPhase by remember { mutableStateOf(true) }
  var lastEventLog by remember { mutableStateOf("Click 'Trigger Event' to watch propagation") }

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
            text = "DOM Tree & Event Propagation",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
          )
          Text(
            text = if (isBubblingPhase) "Mode: Bubbling (Target -> Root)" else "Mode: Capturing (Root -> Target)",
            style = MaterialTheme.typography.bodySmall,
            color = if (isBubblingPhase) NeonGreen else CyberCyan
          )
        }
        TextButton(onClick = { isBubblingPhase = !isBubblingPhase }) {
          Text(if (isBubblingPhase) "Switch: Capturing" else "Switch: Bubbling", color = JsYellow, fontSize = 11.sp)
        }
      }

      Spacer(Modifier.height(12.dp))

      // Simulated DOM hierarchy
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0C1322), RoundedCornerShape(10.dp))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        DomNodeTag("window", 0)
        DomNodeTag("document", 12)
        DomNodeTag("<div class=\"container\">", 24)
        DomNodeTag("<button id=\"btn\">Click Me</button>", 36, isTarget = true) {
          lastEventLog = if (isBubblingPhase) {
            "⚡ Bubbling: <button> -> <div.container> -> document -> window"
          } else {
            "⚡ Capturing: window -> document -> <div.container> -> <button>"
          }
        }
      }

      Spacer(Modifier.height(10.dp))
      Text(
        text = lastEventLog,
        color = JsYellow,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
private fun DomNodeTag(name: String, indent: Int, isTarget: Boolean = false, onClick: (() -> Unit)? = null) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = indent.dp)
      .background(
        if (isTarget) Color(0xFF1E3A8A) else Color(0xFF1E293B),
        RoundedCornerShape(6.dp)
      )
      .clickable(enabled = onClick != null) { onClick?.invoke() }
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = name,
      color = if (isTarget) ElectricBlue else Color(0xFFCBD5E1),
      fontFamily = FontFamily.Monospace,
      fontSize = 12.sp,
      fontWeight = if (isTarget) FontWeight.Bold else FontWeight.Normal
    )
    if (isTarget) {
      Text("TAP TO FIRE EVENT", color = JsYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 5. Debounce & Throttle Lab
 */
@Composable
fun DebounceThrottleLabView(modifier: Modifier = Modifier) {
  var totalClicks by remember { mutableIntStateOf(0) }
  var debouncedCalls by remember { mutableIntStateOf(0) }
  var throttledCalls by remember { mutableIntStateOf(0) }

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Debounce vs Throttle Lab",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
      )
      Text(
        text = "Rapid Burst Event Rate-Limiting Demonstration",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(12.dp))

      Button(
        onClick = {
          totalClicks += 1
          if (totalClicks % 4 == 0) throttledCalls += 1
          debouncedCalls = 1 // Simulating final pause execution
        },
        colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.TouchApp, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Click Rapidly in Bursts! ($totalClicks clicks)", color = Color.White, fontWeight = FontWeight.Bold)
      }

      Spacer(Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricBox("Raw Events", "$totalClicks", CoralRed, Modifier.weight(1f))
        MetricBox("Throttled", "$throttledCalls", AmberOrange, Modifier.weight(1f))
        MetricBox("Debounced", "$debouncedCalls", NeonGreen, Modifier.weight(1f))
      }

      Spacer(Modifier.height(10.dp))
      Text(
        text = "Debounce waits for silence before firing (ideal for search autocomplete). Throttle guarantees execution at fixed intervals (ideal for scroll/resize).",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp
      )
    }
  }
}

@Composable
private fun MetricBox(label: String, value: String, color: Color, modifier: Modifier) {
  Card(
    modifier = modifier.border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Spacer(Modifier.height(2.dp))
      Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
    }
  }
}

/**
 * 6. Prototype Chain Visualizer
 */
@Composable
fun PrototypeChainVisualizerView(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Prototypal Inheritance Chain",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
      )
      Text(
        text = "Property lookup delegation until reaching null",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(12.dp))

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ChainNode("myArray = [1, 2, 3]", "Own properties: length: 3, indices [0, 1, 2]", ElectricBlue)
        ChainLinkArrow()
        ChainNode("Array.prototype", "Delegated methods: map(), filter(), reduce(), push()", NeonGreen)
        ChainLinkArrow()
        ChainNode("Object.prototype", "Universal methods: toString(), hasOwnProperty()", PurpleAccent)
        ChainLinkArrow()
        ChainNode("null", "End of prototype chain (Lookup termination)", Color(0xFF64748B))
      }
    }
  }
}

@Composable
private fun ChainNode(title: String, desc: String, color: Color) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0C1322), RoundedCornerShape(8.dp))
      .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .background(color, RoundedCornerShape(5.dp))
    )
    Spacer(Modifier.width(10.dp))
    Column {
      Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
      Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
  }
}

@Composable
private fun ChainLinkArrow() {
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    Text("↓ __proto__", color = JsYellow, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
  }
}
