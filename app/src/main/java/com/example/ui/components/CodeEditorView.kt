package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Syntax highlighter for JavaScript keywords, strings, numbers, and comments.
 */
fun highlightJavaScript(code: String): AnnotatedString {
  return buildAnnotatedString {
    append(code)
    val text = code

    // Highlight comments
    val commentRegex = Regex("//.*")
    commentRegex.findAll(text).forEach { match ->
      addStyle(SpanStyle(color = CodeComment), match.range.first, match.range.last + 1)
    }

    // Highlight keywords
    val keywords = listOf(
      "const", "let", "var", "function", "return", "if", "else", "for", "while",
      "do", "switch", "case", "break", "continue", "async", "await", "new", "try",
      "catch", "finally", "throw", "class", "extends", "super", "this", "import",
      "export", "default", "from", "typeof", "instanceof", "in", "of"
    )
    val keywordRegex = Regex("\\b(${keywords.joinToString("|")})\\b")
    keywordRegex.findAll(text).forEach { match ->
      addStyle(
        SpanStyle(color = CodeKeyword, fontWeight = FontWeight.SemiBold),
        match.range.first,
        match.range.last + 1
      )
    }

    // Highlight booleans & null/undefined
    val literals = listOf("true", "false", "null", "undefined", "NaN")
    val literalRegex = Regex("\\b(${literals.joinToString("|")})\\b")
    literalRegex.findAll(text).forEach { match ->
      addStyle(SpanStyle(color = CodeBoolean), match.range.first, match.range.last + 1)
    }

    // Highlight numbers
    val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
    numberRegex.findAll(text).forEach { match ->
      addStyle(SpanStyle(color = CodeNumber), match.range.first, match.range.last + 1)
    }

    // Highlight strings
    val stringRegex = Regex("(\"[^\"]*\"|'[^']*'|`[^`]*`)")
    stringRegex.findAll(text).forEach { match ->
      addStyle(SpanStyle(color = CodeString), match.range.first, match.range.last + 1)
    }
  }
}

@Composable
fun CodeEditorView(
  code: String,
  onCodeChange: (String) -> Unit,
  onRun: () -> Unit,
  onReset: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  isRunning: Boolean = false,
  title: String = "JavaScript Editor"
) {
  val clipboardManager = LocalClipboardManager.current
  val lines = code.lines()
  val lineCount = lines.size.coerceAtLeast(1)
  val horizontalScroll = rememberScrollState()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = CodeBackground),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column {
      // Editor Toolbar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(NavySurface)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Window Control Dots
          Box(Modifier.size(10.dp).background(Color(0xFFEF4444), RoundedCornerShape(5.dp)))
          Spacer(Modifier.width(6.dp))
          Box(Modifier.size(10.dp).background(Color(0xFFFBBF24), RoundedCornerShape(5.dp)))
          Spacer(Modifier.width(6.dp))
          Box(Modifier.size(10.dp).background(Color(0xFF10B981), RoundedCornerShape(5.dp)))
          Spacer(Modifier.width(12.dp))
          Text(
            text = title,
            style = CodeSnippetHeaderStyle,
            color = Color(0xFF94A3B8)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (onReset != null) {
            IconButton(
              onClick = onReset,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset Code",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          IconButton(
            onClick = { clipboardManager.setText(AnnotatedString(code)) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Code",
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(Modifier.width(4.dp))

          Button(
            onClick = onRun,
            enabled = !isRunning,
            colors = ButtonDefaults.buttonColors(
              containerColor = NeonGreen,
              contentColor = NavyDeep
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Run Code",
              modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
              text = if (isRunning) "Running..." else "Run",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }

      // Code Area with Line Numbers
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        // Line numbers gutter
        Column(
          modifier = Modifier.padding(end = 12.dp, top = 2.dp),
          horizontalAlignment = Alignment.End
        ) {
          for (i in 1..lineCount) {
            Text(
              text = "$i",
              color = Color(0xFF475569),
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              lineHeight = 20.sp
            )
          }
        }

        // Editable text field with syntax highlighting
        Box(
          modifier = Modifier
            .weight(1f)
            .horizontalScroll(horizontalScroll)
        ) {
          BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            textStyle = CodeTextStyle.copy(color = Color(0xFFF8FAFC)),
            cursorBrush = SolidColor(ElectricBlue),
            visualTransformation = { text ->
              androidx.compose.ui.text.input.TransformedText(
                highlightJavaScript(text.text),
                androidx.compose.ui.text.input.OffsetMapping.Identity
              )
            },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}
