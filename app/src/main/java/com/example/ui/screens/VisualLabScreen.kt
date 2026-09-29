package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.data.model.VisualizerType
import com.example.ui.theme.*
import com.example.ui.visualizers.*

@Composable
fun VisualLabScreen(
  initialVisualizerType: String = "EVENT_LOOP",
  modifier: Modifier = Modifier
) {
  var selectedType by remember {
    mutableStateOf(
      try {
        VisualizerType.valueOf(initialVisualizerType)
      } catch (_: Exception) {
        VisualizerType.EVENT_LOOP
      }
    )
  }

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
            .background(NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Science, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
          Text(
            text = "JavaScript Visual Laboratory",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Make abstract runtime execution concepts visible",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
      }

      Spacer(Modifier.height(14.dp))

      // Lab selector tabs (Horizontal scrollable row)
      ScrollableTabRow(
        selectedTabIndex = VisualizerType.values().indexOf(selectedType),
        containerColor = Color.Transparent,
        contentColor = ElectricBlue,
        edgePadding = 0.dp,
        divider = {}
      ) {
        VisualizerType.values().forEach { type ->
          val isSelected = type == selectedType
          Tab(
            selected = isSelected,
            onClick = { selectedType = type },
            text = {
              Text(
                text = type.title.replace(" Visualizer", "").replace(" Laboratory", "").replace(" Machine", ""),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                color = if (isSelected) NeonGreen else Color(0xFF94A3B8)
              )
            }
          )
        }
      }
    }

    // Selected Visualizer Container
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      when (selectedType) {
        VisualizerType.EVENT_LOOP -> EventLoopVisualizerView()
        VisualizerType.CALL_STACK -> CallStackVisualizerView()
        VisualizerType.ARRAY_METHODS -> ArrayTransformLabView()
        VisualizerType.VARIABLES -> VariableMemoryVisualizerView()
        VisualizerType.CLOSURE -> ClosureScopeVisualizerView()
        VisualizerType.PROMISES -> PromiseStateMachineView()
        VisualizerType.DOM_EVENTS -> DomEventFlowLabView()
        VisualizerType.DEBOUNCE_THROTTLE -> DebounceThrottleLabView()
        VisualizerType.PROTOTYPE -> PrototypeChainVisualizerView()
      }

      Spacer(Modifier.height(24.dp))

      // Educational Insight Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "WHY VISUAL PROGRAMMING MATTERS",
            color = JsYellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Spacer(Modifier.height(6.dp))
          Text(
            text = "Most JavaScript bugs stem from misunderstanding asynchronous execution order, closure memory lifecycles, and prototype delegation. Seeing the engine operate builds deep mental models.",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      }
      Spacer(Modifier.height(16.dp))
    }
  }
}
