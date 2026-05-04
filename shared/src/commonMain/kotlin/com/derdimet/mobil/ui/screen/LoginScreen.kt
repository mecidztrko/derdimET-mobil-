package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.components.AuthGradientButton
import com.derdimet.mobil.ui.components.AuthTextField
import com.derdimet.mobil.ui.theme.AuthTheme
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (UserRole) -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan resmi buraya gelecek (Image bileşeni ile)
        // Image(painter = painterResource("background.jpg"), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            
            // Logo veya Uygulama Adı
            Text(
                text = "derdimET",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = AuthTheme.Text,
                letterSpacing = (-1).sp
            )
            
            Text(
                text = "En taze et, en hızlı çözüm.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AuthTheme.TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 60.dp)
            )

            // Cam (Glass) Görünümlü Giriş Kartı
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AuthTheme.GlassBorder, RoundedCornerShape(24.dp)),
                color = AuthTheme.GlassBg,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Hoş Geldiniz",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuthTheme.Text,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    AuthTextField(
                        value = email,
                        onValueChange = viewModel::onEmailChange,
                        label = "E-posta",
                        icon = Icons.Default.Email
                    )

                    AuthTextField(
                        value = password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Şifre",
                        icon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { viewModel.onRememberMeToggle() },
                                colors = CheckboxDefaults.colors(checkedColor = AuthTheme.Accent)
                            )
                            Text(text = "Beni hatırla", fontSize = 14.sp, color = AuthTheme.Text)
                        }
                        
                        TextButton(onClick = { /* Forgot Password */ }) {
                            Text(text = "Şifremi unuttum?", color = AuthTheme.Accent, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    error?.let {
                        Text(
                            text = it,
                            color = AuthTheme.Error,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    AuthGradientButton(
                        text = "Giriş Yap",
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = isLoading
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Hesabınız yok mu? ", color = AuthTheme.TextMuted, fontSize = 14.sp)
                        TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                            Text(text = "Kayıt Ol", color = AuthTheme.Accent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
