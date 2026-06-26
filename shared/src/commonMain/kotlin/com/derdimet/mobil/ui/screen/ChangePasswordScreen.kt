package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.ui.components.AuthGradientButton
import com.derdimet.mobil.ui.components.AuthTextField
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    authRepository: AuthRepository,
    onBack: () -> Unit,
    onSuccess: () -> Unit = onBack,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Gizlilik ve Güvenlik", showBack = true, onBack = onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Hesap şifrenizi güncelleyin. Yeni şifre en az 8 karakter olmalıdır.",
                color = DerdimColors.MutedForeground,
            )
            DerdimFormCard(title = "Şifre Değiştir") {
                AuthTextField(currentPassword, { currentPassword = it }, "Mevcut şifre", Icons.Default.Lock)
                AuthTextField(newPassword, { newPassword = it }, "Yeni şifre", Icons.Default.Lock)
                AuthTextField(confirmPassword, { confirmPassword = it }, "Yeni şifre (tekrar)", Icons.Default.Lock)
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            success?.let { Text(it, color = Color(0xFF166534)) }
            AuthGradientButton(
                text = "Şifreyi Güncelle",
                isLoading = loading,
                onClick = {
                    error = null
                    success = null
                    when {
                        currentPassword.isBlank() || newPassword.isBlank() -> error = "Tüm alanları doldurun."
                        newPassword.length < 8 -> error = "Yeni şifre en az 8 karakter olmalıdır."
                        newPassword != confirmPassword -> error = "Yeni şifreler eşleşmiyor."
                        else -> {
                            loading = true
                            scope.launch {
                                val err = authRepository.changePassword(currentPassword, newPassword)
                                loading = false
                                if (err == null) {
                                    success = "Şifreniz güncellendi."
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                    onSuccess()
                                } else {
                                    error = err
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}
