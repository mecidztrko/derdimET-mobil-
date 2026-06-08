package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun DerdimTopBar(
    title: String? = null,
    subtitle: String? = null,
    showBack: Boolean = false,
    showLogo: Boolean = false,
    onBack: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DerdimColors.Card,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = DerdimColors.Foreground)
                }
            }
            when {
                showLogo -> {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(DerdimColors.Primary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text("derdimET", fontWeight = FontWeight.SemiBold, color = DerdimColors.Primary, fontSize = 15.sp)
                            subtitle?.let {
                                Text(it, fontSize = 10.sp, color = DerdimColors.MutedForeground, lineHeight = 10.sp)
                            }
                        }
                    }
                }
                else -> {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title.orEmpty(),
                            fontWeight = FontWeight.SemiBold,
                            color = DerdimColors.Foreground,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        subtitle?.let {
                            Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground, lineHeight = 12.sp)
                        }
                    }
                }
            }
            action?.invoke()
        }
    }
}

@Composable
fun DerdimActionBadge(text: String, background: Color = DerdimColors.Amber400) {
    Text(
        text = text,
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun DerdimPrimaryBadge(text: String) {
    DerdimActionBadge(text, DerdimColors.Primary)
}
