package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.theme.AuthTheme

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AuthTheme.Text,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(AuthTheme.RadiusSm))
                .background(Color.White.copy(alpha = 0.4f))
                .border(
                    width = 1.dp,
                    color = if (isFocused) AuthTheme.Accent else Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(AuthTheme.RadiusSm)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused) AuthTheme.Accent else AuthTheme.TextMuted,
                modifier = Modifier.size(20.dp)
            )

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AuthTheme.Accent
                ),
                placeholder = {
                    Text(text = label, color = AuthTheme.TextMuted, fontSize = 16.sp)
                },
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                textStyle = TextStyle(fontSize = 16.sp, color = AuthTheme.Text)
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = AuthTheme.Error,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun AuthGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(AuthTheme.RadiusMd))
            .background(
                Brush.linearGradient(
                    colors = listOf(AuthTheme.AccentMid, AuthTheme.Accent)
                )
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
