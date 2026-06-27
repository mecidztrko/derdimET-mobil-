package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun SplashScreen(message: String = "Çiftlikten sofraya…") {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF14532D), Color(0xFF166534), Color(0xFF052E16)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("dE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }
            Text("derdimET", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(message, color = Color.White.copy(0.85f), fontSize = 14.sp)
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 8.dp).size(28.dp),
                color = Color.White,
                strokeWidth = 2.5.dp,
            )
        }
    }
}
