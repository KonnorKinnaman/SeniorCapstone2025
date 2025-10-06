package com.mycompany.CapstoneProject

import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.PyException
import android.util.Log
import android.widget.Toast

class PanelActivity : ComponentActivity() {
    val test_string: String
  companion object {
    val webViewUrls: MutableList<Pair<String, String>> =
        mutableListOf(
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
                "Micropilot",
                "https://www.youtube.com/embed/0Q9cH-JxEGg?autoplay=1;fs=1;autohide=0;hd=0;",
            ),
        )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { CardList() }
      if (!Python.isStarted()) {
          Python.start(AndroidPlatform(this))
      }

      val py = Python.getInstance()
      val pymod = py.getModule("test")
      try {
          val result1 = pymod.callAttr("testFun")
          val test_string = result1.toString()
          if (test_string != "This is the test string") {

              simpleText()
          }

      } catch (e: PyException) {
          Log.ERROR
      }
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
fun simpleText(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
