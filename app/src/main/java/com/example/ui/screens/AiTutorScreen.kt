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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
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

data class ChatMessage(
  val isUser: Boolean,
  val text: String,
  val visualLabShortcut: String? = null,
  val codeSnippet: String? = null
)

@Composable
fun AiTutorScreen(
  onBack: () -> Unit,
  onOpenVisualLab: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val messages = remember {
    mutableStateListOf(
      ChatMessage(
        isUser = false,
        text = "Hello! I am your AI JavaScript Tutor. Ask me any question, paste broken code to debug, or ask for visual explanations of complex engine concepts like the Event Loop or Closures.",
        visualLabShortcut = "EVENT_LOOP"
      )
    )
  }

  val promptChips = listOf(
    "Explain closures simply",
    "Why does this return undefined?",
    "Explain the Event Loop visually",
    "How does map vs filter work?",
    "What is the Temporal Dead Zone?"
  )

  var inputText by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
  ) {
    // Header
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
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .background(JsYellow.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = JsYellow, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
          Text(
            text = "AI JavaScript Tutor",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = "Interactive Concept Explainer & Debugger",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
        }
      }
    }

    // Chat Message List
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(messages) { msg ->
        ChatBubble(msg = msg, onOpenVisualLab = onOpenVisualLab)
      }
    }

    // Quick Prompt Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      promptChips.take(3).forEach { prompt ->
        Box(
          modifier = Modifier
            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .clickable {
              handleSend(prompt, messages)
            }
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Text(prompt, color = Color(0xFFE2E8F0), fontSize = 11.sp)
        }
      }
    }

    // Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(NavySurface)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        placeholder = { Text("Ask a JS question or paste code...") },
        modifier = Modifier.weight(1f),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = ElectricBlue,
          unfocusedBorderColor = NavyBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedPlaceholderColor = Color(0xFF64748B),
          unfocusedPlaceholderColor = Color(0xFF64748B)
        ),
        shape = RoundedCornerShape(20.dp),
        singleLine = true
      )

      Spacer(Modifier.width(8.dp))

      IconButton(
        onClick = {
          if (inputText.isNotBlank()) {
            val q = inputText
            inputText = ""
            handleSend(q, messages)
          }
        },
        modifier = Modifier
          .background(JsYellow, RoundedCornerShape(20.dp))
          .size(42.dp)
      ) {
        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NavyDeep, modifier = Modifier.size(18.dp))
      }
    }
  }
}

private fun handleSend(prompt: String, messages: MutableList<ChatMessage>) {
  messages.add(ChatMessage(isUser = true, text = prompt))

  val lower = prompt.lowercase()
  val reply = when {
    lower.contains("closure") -> ChatMessage(
      isUser = false,
      text = "A closure is when an inner function remembers variables from its outer function even after the outer function has completed execution.\n\nKey Mental Model: Think of a closure as a backpack that the inner function carries wherever it goes. All outer variables remain accessible in memory.",
      visualLabShortcut = "CLOSURE",
      codeSnippet = "function counter() {\n  let n = 0;\n  return () => ++n;\n}\nconst c = counter();\nc(); // 1\nc(); // 2"
    )
    lower.contains("event loop") -> ChatMessage(
      isUser = false,
      text = "The Event Loop continuously checks if the Call Stack is empty. When clear, it drains the Microtask Queue (Promises) first, then processes one Macrotask (setTimeout).",
      visualLabShortcut = "EVENT_LOOP",
      codeSnippet = "console.log('1: Sync');\nsetTimeout(() => console.log('3: Macro'), 0);\nPromise.resolve().then(() => console.log('2: Micro'));"
    )
    lower.contains("undefined") -> ChatMessage(
      isUser = false,
      text = "Functions return undefined when they lack an explicit 'return' statement, or when you access an uninitialized variable or non-existent object property.",
      codeSnippet = "function add(a, b) {\n  const sum = a + b;\n  // Missing return sum!\n}\nconsole.log(add(2, 3)); // undefined"
    )
    lower.contains("map") || lower.contains("filter") -> ChatMessage(
      isUser = false,
      text = "map() transforms each element 1-to-1 into a new array. filter() removes elements that fail a boolean predicate test.",
      visualLabShortcut = "ARRAY_METHODS",
      codeSnippet = "const nums = [1, 2, 3, 4];\nconst doubled = nums.map(x => x * 2); // [2, 4, 6, 8]\nconst evens = nums.filter(x => x % 2 === 0); // [2, 4]"
    )
    else -> ChatMessage(
      isUser = false,
      text = "In modern JavaScript, clean functional code with const/let, arrow functions, and defensive error boundaries with async/await prevents bugs. Explore the 29 roadmap modules for step-by-step masterclasses!"
    )
  }

  messages.add(reply)
}

@Composable
private fun ChatBubble(
  msg: ChatMessage,
  onOpenVisualLab: (String) -> Unit
) {
  val align = if (msg.isUser) Alignment.End else Alignment.Start
  val bgColor = if (msg.isUser) Color(0xFF1E3A8A) else NavySurface
  val borderColor = if (msg.isUser) ElectricBlue.copy(alpha = 0.5f) else NavyBorder

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = align
  ) {
    Card(
      modifier = Modifier
        .widthIn(max = 320.dp)
        .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
      colors = CardDefaults.cardColors(containerColor = bgColor),
      shape = RoundedCornerShape(14.dp)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = if (msg.isUser) "You" else "AI JavaScript Tutor",
          color = if (msg.isUser) ElectricBlue else JsYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
        Spacer(Modifier.height(4.dp))
        Text(
          text = msg.text,
          color = Color.White,
          fontSize = 13.sp,
          lineHeight = 19.sp
        )

        if (msg.codeSnippet != null) {
          Spacer(Modifier.height(8.dp))
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CodeBackground)
          ) {
            Text(
              text = msg.codeSnippet,
              color = NeonGreen,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              modifier = Modifier.padding(8.dp)
            )
          }
        }

        if (msg.visualLabShortcut != null) {
          Spacer(Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .background(Color(0xFF102820), RoundedCornerShape(8.dp))
              .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .clickable { onOpenVisualLab(msg.visualLabShortcut) }
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Science, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Launch Visual Lab for this concept", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }
  }
}
