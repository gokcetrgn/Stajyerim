package com.gen.stajyerim.ui.backgrounds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LandingPageBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height


            drawOval(
                color = Color(0xFFE0B6F5),
                topLeft = Offset(-width * 0.4f, -height * 0.3f), // Sol üst konumu
                size = Size(width * 0.9f, height * 0.6f) // Boyut
            )

            drawOval(
                color = Color(0xFFE0B6F5),
                topLeft = Offset(width * 0.6f, height * 0.7f), // Sağ alt konumu
                size = Size(width * 0.9f, height * 0.6f) // Boyut
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFC689F4), RoundedCornerShape(16.dp))
                .background(Color.White)
        )
    }
}