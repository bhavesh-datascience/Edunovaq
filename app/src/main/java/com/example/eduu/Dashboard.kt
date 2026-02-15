package com.example.eduu

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import java.util.Calendar

// ==========================================
// 1. Main Dashboard Container
// ==========================================
@Composable
fun DashboardScreen(
    userEmail: String,
    userName: String,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit // <--- ADDED: To open profile
) {
    var currentTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var currentStreak by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        currentStreak = updateAndGetStreak(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1E2E))))
            .systemBarsPadding()
    ) {
        // Ambient Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color(0xFF6366F1).copy(alpha = 0.1f), radius = 700f, center = Offset(size.width, 0f))
            drawCircle(color = Color(0xFFEC4899).copy(alpha = 0.05f), radius = 500f, center = Offset(0f, size.height))
        }

        // Tab Content
        Column(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = currentTab, label = "TabSwitch") { tabIndex ->
                when (tabIndex) {
                    0 -> HomeTab(userName, currentStreak, onProfileClick) // Pass it here
                    1 -> AITab()
                    2 -> ToolsTab()
                    3 -> MeetsTab(userEmail, onLogout)
                }
            }
        }

        // Floating Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp, start = 20.dp, end = 20.dp)
        ) {
            GlassNavigationPill(currentTab) { currentTab = it }
        }
    }
}

// ==========================================
// 2. Tabs
// ==========================================

@Composable
fun HomeTab(userName: String, streak: Int, onProfileClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = Color(0xFF6366F1), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edunovaq", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Hello, $userName", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
            // Profile Logo (Clickable)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF818CF8))))
                    .clickable { onProfileClick() }, // <--- Click action
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Streak Card
        DashboardGlassCard {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFFF5722).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) { Text("🔥", fontSize = 24.sp) }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Study Streak", color = Color.Gray, fontSize = 12.sp)
                    Text("$streak Days", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(if (streak > 0) "Keep it up!" else "Start today!", color = Color(0xFF4ADE80), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Graphs Row
        Text("Your Progress", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DashboardGlassCard(modifier = Modifier.weight(1f).height(180.dp)) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CustomPieChart(listOf(0.6f, 0.25f, 0.15f), listOf(Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFF4ADE80)))
                    Spacer(Modifier.height(12.dp))
                    Text("Performance", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            DashboardGlassCard(modifier = Modifier.weight(1f).height(180.dp)) {
                Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CustomBarGraph()
                    Spacer(Modifier.height(12.dp))
                    Text("Weekly Activity", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth()) {
            StatItem(Icons.Rounded.AccessTime, "42h", "Studied", Modifier.weight(1f))
            StatItem(Icons.Rounded.TaskAlt, "85%", "Tasks", Modifier.weight(1f))
            StatItem(Icons.Rounded.EmojiEvents, "Gold", "Rank", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

// ==========================================
// 3. Components
// ==========================================

@Composable
fun AITab() { AIScreen() } // Assuming AIScreen() exists in AIFeatures.kt

@Composable
fun ToolsTab() { ToolsScreen() } // Assuming ToolsScreen() exists in StudyTools.kt

@Composable
fun MeetsTab(email: String, onLogout: () -> Unit) { StudyMeetsScreen() } // Assuming StudyMeetsScreen exists

@Composable
fun GlassNavigationPill(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.9f),
        contentColor = Color.White,
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.height(70.dp).fillMaxWidth()
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            NavIcon(Icons.Filled.Home, "Home", selectedTab == 0) { onTabSelected(0) }
            NavIcon(Icons.Filled.AutoAwesome, "AI", selectedTab == 1) { onTabSelected(1) }
            NavIcon(Icons.Filled.Construction, "Tools", selectedTab == 2) { onTabSelected(2) }
            NavIcon(Icons.Filled.VideoCall, "Meets", selectedTab == 3) { onTabSelected(3) }
        }
    }
}

@Composable
fun NavIcon(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) Color(0xFF6366F1) else Color.Gray
    val scale by animateFloatAsState(if (isSelected) 1.2f else 1.0f, label = "scale")
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.noRippleClickable { onClick() }) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp * scale))
        if (isSelected) {
            Spacer(Modifier.height(4.dp))
            Box(Modifier.size(4.dp).clip(CircleShape).background(color))
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
}

@Composable
fun DashboardGlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), content = content)
}

@Composable
fun CustomPieChart(data: List<Float>, colors: List<Color>) {
    Canvas(modifier = Modifier.size(80.dp)) {
        var startAngle = -90f
        data.forEachIndexed { index, fraction ->
            val sweepAngle = fraction * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 20f, cap = StrokeCap.Round),
                topLeft = Offset.Zero,
                size = size // Use the canvas size
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun CustomBarGraph() {
    Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        val barWidth = 15.dp.toPx()
        val spacing = 10.dp.toPx()
        val heights = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.6f)
        var startX = (size.width - (heights.size * (barWidth + spacing))) / 2 // Center graph

        heights.forEach { fraction ->
            val barHeight = size.height * fraction
            drawLine(
                color = Color(0xFF6366F1),
                start = Offset(startX + barWidth / 2, size.height),
                end = Offset(startX + barWidth / 2, size.height - barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
            startX += barWidth + spacing
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color.White) }
        Spacer(Modifier.height(8.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@SuppressLint("UseKtx")
fun updateAndGetStreak(context: Context): Int {
    val prefs = context.getSharedPreferences("edunovaq_prefs", Context.MODE_PRIVATE)
    val lastLogin = prefs.getLong("last_login_day", 0L)
    val currentStreak = prefs.getInt("user_streak", 0)
    val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)

    if (lastLogin == today) return currentStreak
    val newStreak = if (lastLogin == today - 1) currentStreak + 1 else 1
    prefs.edit { putLong("last_login_day", today); putInt("user_streak", newStreak) }
    return newStreak
}