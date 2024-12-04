package com.gen.stajyerim.ui.backgrounds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SignUpBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0B6F5))
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF9C27B0), Color(0xFFE0B6F5)) // Çerçeve rengi (gradient)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        )
    }
}