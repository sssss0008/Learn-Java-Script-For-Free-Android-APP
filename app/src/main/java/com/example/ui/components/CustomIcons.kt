package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.JsYellow
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NeonGreen

/**
 * Geometric, modern developer icon badge for concept items and navigation.
 */
@Composable
fun ConceptBadgeIcon(
  type: String,
  size: Dp = 40.dp,
  fontSize: Int = 13
) {
  val (bgColor, textColor, label) = when (type.uppercase()) {
    "JS" -> Triple(Brush.linearGradient(listOf(Color(0xFFF7DF1E), Color(0xFFEAB308))), NavyDeep, "JS")
    "VAR" -> Triple(Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))), Color.White, "var")
    "OP" -> Triple(Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF7E22CE))), Color.White, "+=")
    "IF" -> Triple(Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFBE123C))), Color.White, "?:")
    "LOOP" -> Triple(Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF047857))), Color.White, "for")
    "FN" -> Triple(Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF0E7490))), Color.White, "fn")
    "ARR" -> Triple(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFB45309))), Color.White, "[ ]")
    "STR" -> Triple(Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFBE185D))), Color.White, "\" \"")
    "OBJ" -> Triple(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))), Color.White, "{ }")
    "STACK" -> Triple(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF4338CA))), Color.White, "|||")
    "DOM" -> Triple(Brush.linearGradient(listOf(Color(0xFF14B8A6), Color(0xFF0F766E))), Color.White, "</>")
    "EVT" -> Triple(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C))), Color.White, "⚡")
    "API" -> Triple(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))), Color.White, "API")
    "JSON" -> Triple(Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF065F46))), Color.White, "{}")
    "ERR" -> Triple(Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFF9F1239))), Color.White, "try")
    "ASYNC" -> Triple(Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF155E75))), Color.White, "async")
    "PROM" -> Triple(Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF581C87))), Color.White, "then")
    "AWAIT" -> Triple(Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0369A1))), Color.White, "await")
    "NET" -> Triple(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF312E81))), Color.White, "req")
    "MOD" -> Triple(Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF064E3B))), Color.White, "esm")
    "OOP" -> Triple(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFF78350F))), Color.White, "class")
    "PROTO" -> Triple(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4C1D95))), Color.White, "__p")
    "CLOS" -> Triple(Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFF831843))), Color.White, "(())")
    "ADV" -> Triple(Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF164E63))), Color.White, "λ")
    "TEST" -> Triple(Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF14532D))), Color.White, "✓")
    "PERF" -> Triple(Brush.linearGradient(listOf(Color(0xFFEAB308), Color(0xFF713F12))), NavyDeep, "fps")
    "SEC" -> Triple(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF7F1D1D))), Color.White, "🛡️")
    "PROJ" -> Triple(Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF075985))), Color.White, "app")
    "INT" -> Triple(Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF581C87))), Color.White, "hire")
    else -> Triple(Brush.linearGradient(listOf(ElectricBlue, Color(0xFF0284C7))), Color.White, "JS")
  }

  Box(
    modifier = Modifier
      .size(size)
      .background(bgColor, RoundedCornerShape(10.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = textColor,
      fontSize = fontSize.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
  }
}

/**
 * Brand Logo Icon: < / JS > with execution lightning dot.
 */
@Composable
fun BrandLogoView(
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  Box(
    modifier = modifier
      .background(
        Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF1F2937))),
        RoundedCornerShape(10.dp)
      ),
    contentAlignment = Alignment.Center
  ) {
    if (compact) {
      Text(
        text = "</JS>",
        color = JsYellow,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp
      )
    } else {
      Text(
        text = "< / JS >",
        color = JsYellow,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        fontSize = 16.sp
      )
    }
  }
}
