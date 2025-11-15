package com.mycompany.CapstoneProject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.mycompany.CapstoneProject.ui.theme.VRUITheme

class UIActivity : ComponentActivity() {

    companion object {
        val webViewUrls: List<Pair<String, String>> =
            listOf(
                Pair(
                    "Levelflex",
                    "https://www.youtube.com/embed/WY6Bj3f6piE?autoplay=1;fs=1;autohide=0;hd=0;",
                ),
                Pair(
                    "Liquiphant",
                    "https://www.youtube.com/embed/dfXtUbROf20?autoplay=1;fs=1;autohide=0;hd=0;",
                ),
                Pair(
                    "Promag P 300",
                    "https://www.youtube.com/embed/H3lJlXBuXkY?autoplay=1;fs=1;autohide=0;hd=0;",
                ),
                Pair(
                    "Prosonic M",
                    "https://www.youtube.com/embed/iwVBUo11UAU?autoplay=1;fs=1;autohide=0;hd=0;",
                ),
                Pair(
                    "Micropilot",
                    "https://www.youtube.com/embed/3IdBuVEssvo?autoplay=1;fs=1;autohide=0;hd=0;",
                ),
            )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VRUITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D1117)
                ) {
                    AppScreen()
                }
            }
        }
    }
}

private val DarkNavy = Color(0xFF1a1f32)
private val DarkBlue = Color(0xFF2e3a59)
private val AccentWhite = Color(0xeeebebf5)
private val AccentPurple = Color(0xffa4a2e0)
private val DarkBackground = Color(0xFF0d1117)
private val GlassBackground = Color(0x0DFFFFFF) // 5% opacity
private val GlassBorder = Color(0x1AFFFFFF) // 10% opacity

enum class AppState {
    WARNING, HOME, TUTORIAL, INFORMATION
}

@Composable
fun AppScreen() {
    var appState by remember { mutableStateOf(AppState.WARNING) }
    var selectedFeature by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E2A4A), DarkBackground),
                    center = Offset.Zero,
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(16.dp)
                .background(
                    color = DarkBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(32.dp)
                )
                .clip(RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Crossfade(targetState = appState, label = "") { state ->
                when (state) {
                    AppState.WARNING -> WarningScreen {
                        appState = AppState.HOME
                    }
                    AppState.HOME -> HomeScreen(onFeatureSelected = { feature ->
                        selectedFeature = feature
                        appState = AppState.TUTORIAL
                    })
                    AppState.TUTORIAL -> TutorialScreen(
                        feature = selectedFeature ?: "Unknown",
                        onBack = { appState = AppState.HOME },
                        onContinue = {
                            if (selectedFeature == "Sensors") {
                                appState = AppState.INFORMATION
                            }
                        }
                    )
                    AppState.INFORMATION -> CardList(
                        onBack = {appState = AppState.HOME}
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .background(
                color = GlassBackground,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        content = content
    )
}

@Composable
fun WarningScreen(onAcknowledge: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Safety Warning",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Use VR headsets in a safe, clean, and unobstructed environment to avoid collisions with other objects or people.",
                color = AccentWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Prolonged use of VR headsets may cause eye strain and dizziness. Users should take regular breaks and limit each session to no more than 30 minutes.",
                color = AccentWhite
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAcknowledge,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xffa4a2e0), Color(0xeeebebf5))),
                    shape = RoundedCornerShape(50)
                )
                .border(
                    width = 1.dp,
                    brush = SolidColor(AccentPurple),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Text(
                text = "I Acknowledge",
                fontWeight = FontWeight.SemiBold,
                color = DarkNavy
            )
        }
    }
}

@Composable
fun HomeScreen(onFeatureSelected: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Welcome to the VR App!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Here you can view 3D models of different items. Select a feature below to get started and see a tutorial.",
                color = AccentWhite
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF5C3300).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF6B4200),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Warning",
                    tint = Color(0xFFFACC15),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remember to take breaks and be aware of your surroundings.",
                    fontSize = 12.sp,
                    color = Color(0xFFFACC15)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FeatureBox(
                feature = "Scan",
                onClick = { onFeatureSelected("Scan") }
            )
            FeatureBox(
                feature = "Sensors",
                onClick = { onFeatureSelected("Sensors") }
            )
            FeatureBox(
                feature = "ChatGPT",
                onClick = { onFeatureSelected("ChatGPT") }
            )
        }
    }
}

@Composable
fun FeatureBox(feature: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (feature) {
                "Sensors" -> {
                    // Custom SVG-like icon
                    Box(modifier = Modifier.size(48.dp)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "TTS Icon", tint = AccentPurple, modifier = Modifier.size(48.dp))
                    }
                }
                "Scan" -> {
                    // Custom SVG-like icon
                    Box(modifier = Modifier.size(48.dp)) {
                        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceAround) {
                            Box(modifier = Modifier.fillMaxHeight().width(3.dp).background(AccentPurple))
                            Box(modifier = Modifier.fillMaxHeight().width(3.dp).background(AccentPurple))
                            Box(modifier = Modifier.fillMaxHeight().width(3.dp).background(AccentPurple))
                        }
                    }
                }
                "ChatGPT" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.openai_logo),
                        contentDescription = "ChatGPT Icon",
                        tint = AccentPurple,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = feature, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun TutorialScreen(feature: String, onBack: () -> Unit, onContinue: () -> Unit) {
    val (title, description, buttonText) = when (feature) {
        "Sensors" -> Triple(
            "Sensors Tutorial",
            "This feature allows you to view information about various sensors. Tap the button below to see the list of sensors.",
            "View Sensors"
        )
        "Scan" -> Triple(
            "Scan Tutorial",
            "The Scan feature allows you to capture and save real-world objects as new 3D models. Hold down the \"Scan\" button on your controller and slowly move around the object to capture its geometry.",
            "Activate Scan Feature"
        )
        "ChatGPT" -> Triple(
            "ChatGPT Tutorial",
            "Ask questions about any of the 3D models using natural language. The ChatGPT feature will provide detailed information and insights about the selected object. To use it, simply say \"Hey, AI\" and then ask your question.",
            "Activate ChatGPT Feature"
        )
        else -> Triple("", "", "")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center,
                color = AccentWhite
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = buttonText, fontWeight = FontWeight.SemiBold, color = DarkNavy)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = AccentWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Back to Home", fontWeight = FontWeight.SemiBold, color = DarkNavy)
        }
    }
}

@Composable
fun CardList(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff252533))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            UIActivity.webViewUrls.forEach { (title, url) ->
                CardItem(
                    title = title,
                    onClick = {
                        val intent = Intent("com.mycompany.PLAY_VIDEO")
                        intent.putExtra("webviewURI", url)
                        context.sendBroadcast(intent)
                    },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AccentWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Back to Home", fontWeight = FontWeight.SemiBold, color = DarkNavy)
            }
        }
    }
}

@Composable
fun CardItem(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xffa4a2e0),
                        Color(0xeeebebf5)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text = title, style = TextStyle(fontSize = 25.sp, color = DarkNavy))
    }
}
