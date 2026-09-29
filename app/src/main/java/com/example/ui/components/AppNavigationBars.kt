package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.MainTab
import com.example.ui.theme.*

@Composable
fun AppTopBar(
  title: String,
  onOpenSettings: () -> Unit,
  onOpenSearch: () -> Unit,
  streakDays: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(NavySurface)
      .windowInsetsPadding(WindowInsets.statusBars)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      BrandLogoView(compact = true, modifier = Modifier.size(34.dp))
      Spacer(Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
        Text(
          text = "Awiskar Acharya",
          color = JsYellow,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Streak Badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("🔥", fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        Text(
          text = "$streakDays",
          color = AmberOrange,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(Modifier.width(8.dp))

      IconButton(
        onClick = onOpenSearch,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = Color(0xFFCBD5E1),
          modifier = Modifier.size(20.dp)
        )
      }

      IconButton(
        onClick = onOpenSettings,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = "Settings",
          tint = Color(0xFFCBD5E1),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun AppBottomNav(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
    containerColor = NavySurface,
    contentColor = Color(0xFF94A3B8),
    tonalElevation = 8.dp
  ) {
    NavigationBarItem(
      selected = currentTab == MainTab.HOME,
      onClick = { onTabSelected(MainTab.HOME) },
      icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
      label = { Text("Home", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavyDeep,
        selectedTextColor = JsYellow,
        indicatorColor = JsYellow,
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B)
      )
    )

    NavigationBarItem(
      selected = currentTab == MainTab.LEARN,
      onClick = { onTabSelected(MainTab.LEARN) },
      icon = { Icon(Icons.Default.MenuBook, contentDescription = "Learn") },
      label = { Text("Learn", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavyDeep,
        selectedTextColor = ElectricBlue,
        indicatorColor = ElectricBlue,
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B)
      )
    )

    NavigationBarItem(
      selected = currentTab == MainTab.VISUAL_LABS,
      onClick = { onTabSelected(MainTab.VISUAL_LABS) },
      icon = { Icon(Icons.Default.Science, contentDescription = "Labs") },
      label = { Text("Visual Lab", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavyDeep,
        selectedTextColor = NeonGreen,
        indicatorColor = NeonGreen,
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B)
      )
    )

    NavigationBarItem(
      selected = currentTab == MainTab.PRACTICE,
      onClick = { onTabSelected(MainTab.PRACTICE) },
      icon = { Icon(Icons.Default.Code, contentDescription = "Practice") },
      label = { Text("Practice", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavyDeep,
        selectedTextColor = CyberCyan,
        indicatorColor = CyberCyan,
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B)
      )
    )

    NavigationBarItem(
      selected = currentTab == MainTab.PROFILE,
      onClick = { onTabSelected(MainTab.PROFILE) },
      icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
      label = { Text("Profile", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavyDeep,
        selectedTextColor = PurpleAccent,
        indicatorColor = PurpleAccent,
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B)
      )
    )
  }
}
