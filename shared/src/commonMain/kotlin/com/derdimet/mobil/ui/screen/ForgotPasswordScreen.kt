package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.ui.components.AuthGradientButton
import com.derdimet.mobil.ui.components.AuthTextField
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    authRepository: AuthRepository,
    initialEmail: String = "",
    onBack: () -> Unit,
    onResetSuccess: () -> Unit,
) {
    var step by remember { mutableStateOf(0) }
    var email by remember { mutableStateOf(initialEmail) }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Şifremi Unuttum", showBack = true, onBack = onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                if (step == 0) "E-posta adresinize sıfırlama kodu gönderilecek." else "Kod ve yeni şifrenizi girin.",
                fontSize = 14.sp,
                color = DerdimColors.MutedForeground,
            )
            if (step == 0) {
                AuthTextField(email, { email = it }, "E-posta", Icons.Default.Email)
            } else {
                Text(email, fontWeight = FontWeight.Medium)
            }
            if (step == 1) {
                AuthTextField(code, { code = it }, "Doğrulama kodu", Icons.Default.Pin)
                AuthTextField(newPassword, { newPassword = it }, "Yeni şifre", Icons.Default.Lock)
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            info?.let { Text(it, color = Color(0xFF166534)) }
            AuthGradientButton(
                text = if (step == 0) "Kod Gönder" else "Şifreyi Güncelle",
                isLoading = loading,
                onClick = {
                    loading = true
                    error = null
                    info = null
                    scope.launch {
                        if (step == 0) {
                            val err = authRepository.forgotPassword(email)
                            loading = false
                            if (err == null) {
                                info = "Kod gönderildi (demo ortamında e-posta kontrol edin)."
                                step = 1
                            } else {
                                error = err
                            }
                        } else {
                            val err = authRepository.resetPassword(email, code, newPassword)
                            loading = false
                            if (err == null) {
                                onResetSuccess()
                            } else {
                                error = err
                            }
                        }
                    }
                },
            )
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Girişe dön", color = DerdimColors.Primary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
