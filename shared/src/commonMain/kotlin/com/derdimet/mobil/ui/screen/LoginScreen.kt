package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.ui.components.AuthGradientButton
import com.derdimet.mobil.ui.components.AuthTextField
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.viewmodel.LoginViewModel

private data class DemoRoleOption(
    val role: UserRole,
    val label: String,
    val icon: ImageVector,
    val bg: Color,
    val fg: Color,
    val border: Color,
)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: (UserRole) -> Unit,
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    var demoLoadingRole by remember { mutableStateOf<UserRole?>(null) }

    LaunchedEffect(isLoading) {
        if (!isLoading) demoLoadingRole = null
    }

    val demoRoles = listOf(
        DemoRoleOption(UserRole.MEAT_BUYER, "Et Alıcı", Icons.Default.ShoppingCart, Color(0xFFEFF6FF), Color(0xFF1D4ED8), Color(0xFFBFDBFE)),
        DemoRoleOption(UserRole.ANIMAL_SELLER, "Hayvan Satıcı", Icons.Default.Inventory2, Color(0xFFECFDF5), Color(0xFF047857), Color(0xFFA7F3D0)),
        DemoRoleOption(UserRole.SLAUGHTERHOUSE, "Kesimhane", Icons.Default.Business, Color(0xFFF3E8FF), Color(0xFF7E22CE), Color(0xFFE9D5FF)),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DerdimColors.Primary, Color(0xFF1E40AF), DerdimColors.Primary))),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 56.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(0.15f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("🥩", fontSize = 36.sp)
            }
            Text("derdimET", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp, modifier = Modifier.padding(top = 16.dp))
            Text("Hayvancılık ve Et Ticareti Pazaryeri", color = Color(0xFFBFDBFE), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DerdimColors.Background, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Text(
                "DEMO MODUNDA HIZLI GİRİŞ",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DerdimColors.MutedForeground,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                demoRoles.forEach { option ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = demoLoadingRole == null && !isLoading) {
                                demoLoadingRole = option.role
                                viewModel.demoLogin(option.role) { role ->
                                    demoLoadingRole = null
                                    onLoginSuccess(role)
                                }
                            }
                            .background(option.bg, RoundedCornerShape(14.dp))
                            .border(2.dp, option.border, RoundedCornerShape(14.dp))
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (demoLoadingRole == option.role) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = option.fg)
                        } else {
                            Icon(option.icon, null, tint = option.fg, modifier = Modifier.size(18.dp))
                        }
                        Text(option.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = option.fg, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(DerdimColors.Border))
                Text("veya e-posta ile", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(horizontal = 12.dp))
                Box(Modifier.weight(1f).height(1.dp).background(DerdimColors.Border))
            }
            Spacer(Modifier.height(20.dp))

            AuthTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = "E-posta",
                icon = Icons.Default.Email,
            )
            AuthTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = "Şifre",
                icon = Icons.Default.Lock,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onNavigateToForgotPassword, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Text("Şifremi unuttum", color = DerdimColors.Primary, fontSize = 12.sp)
                }
                TextButton(onClick = { showPassword = !showPassword }, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(16.dp))
                    Text(if (showPassword) "Gizle" else "Göster", color = DerdimColors.MutedForeground, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }

            error?.let {
                Text(it, color = DerdimColors.Destructive, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            }

            AuthGradientButton(
                text = "Giriş Yap",
                onClick = { viewModel.login(onLoginSuccess) },
                isLoading = isLoading,
            )

            Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
                Text("Hesabınız yok mu? ", color = DerdimColors.MutedForeground, fontSize = 14.sp)
                TextButton(onClick = onNavigateToRegister, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Text("Kayıt Ol", color = DerdimColors.Primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
