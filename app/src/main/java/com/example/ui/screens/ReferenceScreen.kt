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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReferenceItem
import com.example.ui.theme.*

@Composable
fun ReferenceScreen(
  referenceItems: List<ReferenceItem>,
  glossary: Map<String, String>,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedTab by remember { mutableStateOf("Methods") } // "Methods", "Glossary", "Cheatsheet"

  val filteredMethods = referenceItems.filter {
    it.name.contains(searchQuery, ignoreCase = true) ||
        it.summary.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
  }

  val filteredGlossary = glossary.filter {
    it.key.contains(searchQuery, ignoreCase = true) ||
        it.value.contains(searchQuery, ignoreCase = true)
  }

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
      Column {
        Text(
          text = "JavaScript Reference",
          style = MaterialTheme.typography.titleLarge,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Methods, Cheatsheets & Technical Glossary",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF94A3B8)
        )
      }
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search methods, operators, terms (e.g. map, closure)...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ElectricBlue,
        unfocusedBorderColor = NavyBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedPlaceholderColor = Color(0xFF64748B),
        unfocusedPlaceholderColor = Color(0xFF64748B)
      ),
      shape = RoundedCornerShape(12.dp)
    )

    // Segmented Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("Methods", "Glossary", "Cheatsheets").forEach { tab ->
        val isSelected = tab == selectedTab
        Box(
          modifier = Modifier
            .weight(1f)
            .background(
              if (isSelected) ElectricBlue else Color(0xFF1E293B),
              RoundedCornerShape(8.dp)
            )
            .clickable { selectedTab = tab }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = tab,
            color = if (isSelected) NavyDeep else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }

    Spacer(Modifier.height(10.dp))

    // List Content
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (selectedTab == "Methods") {
        items(filteredMethods) { item ->
          ReferenceItemCard(item = item)
        }
      } else if (selectedTab == "Glossary") {
        items(filteredGlossary.entries.toList()) { entry ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(entry.key, color = JsYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
              Spacer(Modifier.height(4.dp))
              Text(entry.value, color = Color(0xFFE2E8F0), fontSize = 13.sp, lineHeight = 19.sp)
            }
          }
        }
      } else {
        // Cheatsheets
        item {
          CheatsheetCard(
            title = "Array Methods Cheatsheet",
            items = listOf(
              "Mutating: push(), pop(), shift(), unshift(), splice(), sort(), reverse()",
              "Non-mutating: map(), filter(), reduce(), slice(), concat(), flat(), toSorted()",
              "Searching: find(), findIndex(), indexOf(), includes(), some(), every()"
            )
          )
        }
        item {
          CheatsheetCard(
            title = "ES6+ Modern Syntax Cheatsheet",
            items = listOf(
              "Optional Chaining: user?.profile?.email ?? 'default@mail.com'",
              "Nullish Coalescing: const count = input ?? 10 (treats 0 as valid!)",
              "Destructuring: const { name, age, ...rest } = user",
              "Rest & Spread: function sum(...nums) { return nums.reduce(...) }",
              "Dynamic Imports: const module = await import('./heavyLib.js')"
            )
          )
        }
      }
    }
  }
}

@Composable
private fun ReferenceItemCard(item: ReferenceItem) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded },
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.name,
          color = ElectricBlue,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = item.category,
          color = Color(0xFF94A3B8),
          fontSize = 11.sp
        )
      }

      Spacer(Modifier.height(4.dp))
      Text(
        text = item.summary,
        color = Color(0xFFCBD5E1),
        fontSize = 12.sp
      )

      if (isExpanded) {
        Spacer(Modifier.height(10.dp))
        Text("SYNTAX:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(item.syntax, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp)

        Spacer(Modifier.height(8.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CodeBackground)
        ) {
          Text(item.exampleCode, color = NeonGreen, fontFamily = FontFamily.Monospace, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
        }

        Spacer(Modifier.height(8.dp))
        Text("RETURN VALUE: ${item.returnType}", color = JsYellow, fontSize = 11.sp)
        Text("⚠️ GOTCHA: ${item.gotcha}", color = CoralRed, fontSize = 11.sp)
      }
    }
  }
}

@Composable
private fun CheatsheetCard(title: String, items: List<String>) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(title, color = JsYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(Modifier.height(8.dp))
      items.forEach { line ->
        Text("• $line", color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
      }
    }
  }
}
