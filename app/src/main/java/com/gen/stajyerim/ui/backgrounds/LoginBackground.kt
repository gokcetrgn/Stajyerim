package com.gen.stajyerim.ui.backgrounds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFF))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawOval(
                color = Color(0xFFE0B6F5),
                topLeft = Offset(-width * 0.6f, -height * 0.6f),
                size = Size(width * 1.4f, height *  0.95f)
            )


            drawOval(
                color = Color(0xFFD1A6EB),
                topLeft = Offset(-width * 0.5f, -height * 0.5f),
                size = Size(width * 1.2f, height * 0.75f)
            )

            drawOval(
                color = Color(0xFFBF89D4),
                topLeft = Offset(-width * 0.4f, -height * 0.4f),
                size = Size(width * 1.0f, height * 0.55f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF9C27B0), Color(0xFFE0B6F5))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .shadow(4.dp, RoundedCornerShape(5.dp), clip = false)
                .fillMaxWidth()
                .height(450.dp)
        )
    }
}
