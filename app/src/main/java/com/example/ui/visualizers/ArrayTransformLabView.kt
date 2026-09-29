package com.example.ui.visualizers

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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

enum class ArrayOperation(val label: String, val codeSnippet: String) {
  MAP("map()", "arr.map(x => x * 2)"),
  FILTER("filter()", "arr.filter(x => x % 2 === 0)"),
  REDUCE("reduce()", "arr.reduce((acc, x) => acc + x, 0)"),
  PUSH("push(5)", "arr.push(5)"),
  SLICE("slice(1, 3)", "arr.slice(1, 3)")
}

@Composable
fun ArrayTransformLabView(
  modifier: Modifier = Modifier
) {
  val initialArray = listOf(1, 2, 3, 4)
  var selectedOp by remember { mutableStateOf(ArrayOperation.MAP) }

  val outputArray = when (selectedOp) {
    ArrayOperation.MAP -> initialArray.map { it * 2 }
    ArrayOperation.FILTER -> initialArray.filter { it % 2 == 0 }
    ArrayOperation.REDUCE -> listOf(initialArray.sum())
    ArrayOperation.PUSH -> initialArray + 5
    ArrayOperation.SLICE -> initialArray.subList(1, 3)
  }

  val opExplanation = when (selectedOp) {
    ArrayOperation.MAP -> "map() applies the callback to every element and returns a new transformed array of equal length."
    ArrayOperation.FILTER -> "filter() evaluates each element against the predicate boolean test, returning only elements where test is true."
    ArrayOperation.REDUCE -> "reduce() folds all elements sequentially into a single accumulated total value starting from initialValue 0."
    ArrayOperation.PUSH -> "push() mutates the array by appending a new element to the end of the collection."
    ArrayOperation.SLICE -> "slice(start, end) shallow-copies elements from index 1 up to (but not including) index 3."
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Array Transformation Laboratory",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
      )
      Text(
        text = "Input -> Higher-Order Function -> Output Pipeline",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8)
      )

      Spacer(Modifier.height(14.dp))

      // Operation Selector Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        ArrayOperation.values().forEach { op ->
          val isSelected = op == selectedOp
          Box(
            modifier = Modifier
              .background(
                if (isSelected) ElectricBlue else Color(0xFF1E2A4A),
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedOp = op }
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Text(
              text = op.label,
              color = if (isSelected) NavyDeep else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      Spacer(Modifier.height(12.dp))

      // Code Display
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CodeBackground),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(
          text = "const arr = [1, 2, 3, 4];\nconst result = " + selectedOp.codeSnippet + ";",
          color = Color(0xFFE2E8F0),
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          modifier = Modifier.padding(10.dp)
        )
      }

      Spacer(Modifier.height(12.dp))

      // Visual Pipeline: Input Array -> Transformation -> Output Array
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Input Box
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("INPUT ARRAY", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Spacer(Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            initialArray.forEach { item ->
              ArrayElementBox(value = "$item", color = ElectricBlue)
            }
          }
        }

        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          tint = JsYellow,
          modifier = Modifier.size(24.dp)
        )

        // Output Box
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("OUTPUT RESULT", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Spacer(Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            outputArray.forEach { item ->
              ArrayElementBox(value = "$item", color = NeonGreen)
            }
          }
        }
      }

      Spacer(Modifier.height(12.dp))

      Text(
        text = opExplanation,
        color = Color(0xFFCBD5E1),
        fontSize = 12.sp,
        lineHeight = 17.sp
      )
    }
  }
}

@Composable
fun ArrayElementBox(value: String, color: Color) {
  Box(
    modifier = Modifier
      .size(36.dp)
      .background(color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
      .border(1.dp, color, RoundedCornerShape(6.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = value,
      color = Color.White,
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      fontFamily = FontFamily.Monospace
    )
  }
}
