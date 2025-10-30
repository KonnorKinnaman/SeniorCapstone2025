package com.mycompany.CapstoneProject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint

class PanelActivity : ComponentActivity() {
  companion object {
    val webViewUrls: List<Pair<String, String>> =
        listOf(
            Pair(
                "Fluid Analysis and Applications",
                "https://www.youtube.com/embed/6aKbrPp09jo?autoplay=1;fs=1;autohide=0;hd=0;",
            ),
            Pair(
                "About the Industrial Process Control Cart (IPCC)",
                "https://www.youtube.com/embed/dfXtUbROf20?autoplay=1;fs=1;autohide=0;hd=0;",
            ),
            Pair(
                "Coriolis Sensor",
                "https://www.youtube.com/embed/qCBqGUufbVE?autoplay=1;fs=1;autohide=0;hd=0;",
            ),
            Pair(
                "Fluid Pressure Sensing",
                "https://www.youtube.com/embed/UnWCxIlKyNM?autoplay=1;fs=1;autohide=0;hd=0;",
            ),
            Pair(
                "MicroPilot",
                "https://www.youtube.com/embed/0Q9cH-JxEGg?autoplay=1;fs=1;autohide=0;hd=0;",
            ),
        )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { CardList() }
  }
}

@Preview
@Composable
fun CardList() {
  val context = LocalContext.current

  Column(
      modifier =
          Modifier.clip(RoundedCornerShape(32.dp))
              .fillMaxSize()
              .background(
                          Color(0xff252533)
              )
              .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      repeat(5) { index ->
        CardItem(
            title = PanelActivity.webViewUrls[index].first,
            onClick = {
              val intent = Intent("com.mycompany.PLAY_VIDEO")
              intent.putExtra("webviewURI", PanelActivity.webViewUrls[index].second)
              context.sendBroadcast(intent)
            },
        )
      }
    }
  }
}
@Composable
fun CardItem(title: String, onClick: () -> Unit) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(
                  brush = Brush.linearGradient(
                      colors = listOf(
                          Color(0xffa4a2e0),
                          Color(0xeeebebf5)
                      )
                  )
              )

              .clickable(onClick = onClick),
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      BasicText(text = title, style = TextStyle(fontSize = 25.sp))
    }
  }
}
