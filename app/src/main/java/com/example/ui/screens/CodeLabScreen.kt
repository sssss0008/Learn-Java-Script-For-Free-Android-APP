package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
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
import com.example.engine.JavaScriptEngine
import com.example.ui.components.CodeEditorView
import com.example.ui.components.ConsoleOutputView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CodeLabScreen(
  engine: JavaScriptEngine,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val templates = listOf(
    "Array Pipeline" to """
// Modern Array Transformation Pipeline
const scores = [85, 92, 45, 78, 64, 99, 88];

const topPerformers = scores
  .filter(s => s >= 80)
  .map(s => `Grade A: ${'$'}s`)
  .sort();

console.log('Top performers count:', topPerformers.length);
topPerformers.forEach(p => console.log(p));
    """.trimIndent(),

    "Async/Await" to """
// Simulated Asynchronous API Call
async function fetchUser(id) {
  console.log(`[API] Fetching user ${'$'}id...`);
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({ id, name: 'Awiskar Acharya', role: 'Architect' });
    }, 100);
  });
}

(async () => {
  const user = await fetchUser(101);
  console.log('Fetched:', user.name, '(' + user.role + ')');
})();
    """.trimIndent(),

    "Closure Factory" to """
// Closure Private State Machine
function createBankAccount(owner, initialBalance = 0) {
  let balance = initialBalance;
  return {
    deposit(amount) {
      balance += amount;
      console.log(`[+] Deposited ${'$'}amount. Balance: ${'$'}balance`);
      return balance;
    },
    getBalance() { return balance; }
  };
}

const myAccount = createBankAccount('Awiskar', 500);
myAccount.deposit(250);
console.log('Final Balance:', myAccount.getBalance());
    """.trimIndent(),

    "Debounce Utility" to """
// Debounce Function Implementation
function debounce(fn, waitMs = 200) {
  let timer;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), waitMs);
  };
}

const search = debounce((query) => {
  console.log('Executing search for:', query);
}, 50);

search('J');
search('Java');
search('JavaScript');
    """.trimIndent()
  )

  var activeCode by remember { mutableStateOf(templates[0].second) }
  var executionResult by remember { mutableStateOf<ExecutionResult?>(null) }
  var isRunning by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
  ) {
    // Header
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Terminal, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
          Text(
            text = "JavaScript Code Lab",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Professional IDE Playground & V8 Execution Engine",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
      }

      Spacer(Modifier.height(12.dp))

      // Template Selector Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        templates.forEach { (name, snippet) ->
          Box(
            modifier = Modifier
              .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
              .clickable {
                activeCode = snippet
                executionResult = null
              }
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Text(
              text = name,
              color = Color(0xFFE2E8F0),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // Scrollable editor & console
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      CodeEditorView(
        code = activeCode,
        onCodeChange = { activeCode = it },
        onRun = {
          isRunning = true
          scope.launch {
            executionResult = engine.execute(activeCode)
            isRunning = false
          }
        },
        onReset = {
          activeCode = templates[0].second
          executionResult = null
        },
        isRunning = isRunning,
        title = "scratchpad.js"
      )

      ConsoleOutputView(
        result = executionResult,
        onClear = { executionResult = null }
      )

      // Variable Inspector Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "VARIABLE INSPECTOR",
            color = CyberCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Spacer(Modifier.height(6.dp))
          Text(
            text = "Execution state and scope bindings evaluate live via real JavaScript engines.",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp
          )
        }
      }
      Spacer(Modifier.height(16.dp))
    }
  }
}
