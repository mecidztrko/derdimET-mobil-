package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.ui.components.AuthGradientButton
import com.derdimet.mobil.ui.components.AuthTextField
import com.derdimet.mobil.ui.theme.AuthTheme
import com.derdimet.mobil.viewmodel.AccountType
import com.derdimet.mobil.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (UserRole) -> Unit
) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val role by viewModel.role.collectAsState()
    val accountType by viewModel.accountType.collectAsState()
    val companyName by viewModel.companyName.collectAsState()
    val taxNumber by viewModel.taxNumber.collectAsState()
    val address by viewModel.address.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "‹ Geri",
                color = AuthTheme.Accent,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateBack() }.padding(vertical = 4.dp)
            )

            Text(
                text = "Hesap oluştur",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthTheme.Text,
                modifier = Modifier.padding(top = 12.dp)
            )
            
            Text(
                text = "Bilgilerinizi girerek başlayın",
                fontSize = 14.sp,
                color = AuthTheme.TextMuted,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AuthTheme.CardBg),
                shape = RoundedCornerShape(AuthTheme.RadiusMd)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AuthTextField(value = name, onValueChange = viewModel::onNameChange, label = "Ad soyad", icon = Icons.Default.Person)
                    AuthTextField(value = email, onValueChange = viewModel::onEmailChange, label = "E-posta", icon = Icons.Default.Email)
                    AuthTextField(value = password, onValueChange = viewModel::onPasswordChange, label = "Şifre", icon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation())
                    AuthTextField(value = confirmPassword, onValueChange = viewModel::onConfirmPasswordChange, label = "Şifre tekrar", icon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation())

                    Text(text = "Rol", style = sectionLabelStyle())
                    Row(modifier = Modifier.padding(bottom = 14.dp)) {
                        Chip(label = "Et alıcı", selected = role == UserRole.MEAT_BUYER, onClick = { viewModel.onRoleChange(UserRole.MEAT_BUYER) })
                        Chip(label = "Hayvan satıcı", selected = role == UserRole.ANIMAL_SELLER, onClick = { viewModel.onRoleChange(UserRole.ANIMAL_SELLER) })
                    }

                    Text(text = "Hesap türü", style = sectionLabelStyle())
                    Row(modifier = Modifier.padding(bottom = 14.dp)) {
                        Chip(label = "Bireysel", selected = accountType == AccountType.INDIVIDUAL, onClick = { viewModel.onAccountTypeChange(AccountType.INDIVIDUAL) })
                        Chip(label = "İşletme", selected = accountType == AccountType.BUSINESS, onClick = { viewModel.onAccountTypeChange(AccountType.BUSINESS) })
                    }

                    if (accountType == AccountType.BUSINESS) {
                        AuthTextField(value = companyName, onValueChange = viewModel::onCompanyNameChange, label = "Şirket adı", icon = Icons.Default.Business)
                        AuthTextField(value = taxNumber, onValueChange = viewModel::onTaxNumberChange, label = "Vergi numarası", icon = Icons.Default.Description)
                        AuthTextField(value = address, onValueChange = viewModel::onAddressChange, label = "Adres", icon = Icons.Default.LocationOn, singleLine = false)
                    }

                    error?.let {
                        Text(text = it, color = AuthTheme.Error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(120.dp))
        }

        // Footer
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = Color.White.copy(alpha = 0.72f),
            border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                AuthGradientButton(text = "Kayıt ol", onClick = { viewModel.register(onRegisterSuccess) }, isLoading = isLoading)
                Text(
                    text = "Kayıt olarak kullanım koşullarını kabul etmiş olursunuz.",
                    fontSize = 11.sp,
                    color = AuthTheme.TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun Chip(label: String, selected: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    val bgColor = when {
        !enabled -> Color.Gray.copy(alpha = 0.08f)
        selected -> AuthTheme.Accent.copy(alpha = 0.12f)
        else -> Color.White.copy(alpha = 0.85f)
    }
    val borderColor = when {
        !enabled -> Color.Gray.copy(alpha = 0.25f)
        selected -> AuthTheme.Accent
        else -> Color.Gray.copy(alpha = 0.45f)
    }
    val textColor = when {
        !enabled -> AuthTheme.TextMuted
        selected -> AuthTheme.Accent
        else -> AuthTheme.Text
    }
    Box(
        modifier = Modifier
            .padding(end = 10.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(AuthTheme.RadiusSm))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(AuthTheme.RadiusSm))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Text(text = label, color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun sectionLabelStyle() = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AuthTheme.Text)
