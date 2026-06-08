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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
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
import com.derdimet.mobil.viewmodel.RegisterViewModel

private val registerCities = listOf(
    "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya",
    "Gaziantep", "Şanlıurfa", "Mersin", "Diyarbakır", "Kayseri", "Samsun",
    "Eskişehir", "Denizli", "Trabzon", "Erzurum", "Malatya", "Balıkesir",
)

private data class RegisterRoleOption(
    val role: UserRole,
    val label: String,
    val desc: String,
    val icon: ImageVector,
    val gradient: List<Color>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (UserRole) -> Unit,
) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val role by viewModel.role.collectAsState()
    val companyName by viewModel.companyName.collectAsState()
    val address by viewModel.address.collectAsState()
    val city by viewModel.city.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var step by remember { mutableStateOf(1) }
    var showPassword by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }

    val roles = listOf(
        RegisterRoleOption(UserRole.MEAT_BUYER, "Et Alıcı", "Kesimhanelerden et satın alıyorum", Icons.Default.ShoppingCart, listOf(Color(0xFF3B82F6), Color(0xFF2563EB))),
        RegisterRoleOption(UserRole.ANIMAL_SELLER, "Hayvan Satıcı", "Kesimhaneye hayvan satıyorum", Icons.Default.Inventory2, listOf(Color(0xFF10B981), Color(0xFF059669))),
        RegisterRoleOption(UserRole.SLAUGHTERHOUSE, "Kesimhane", "Hayvan alıyor, et satıyorum", Icons.Default.Business, listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))),
    )
    val selectedRole = roles.find { it.role == role }

    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DerdimColors.Primary, Color(0xFF1E40AF), DerdimColors.Primary)))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (step == 2) step = 1 else onNavigateBack() }, modifier = Modifier.size(40.dp).background(Color.White.copy(0.15f), RoundedCornerShape(12.dp))) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Text("derdimET", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(40.dp))
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            StepDot(1, step >= 1)
            Box(Modifier.weight(1f).height(2.dp).background(if (step >= 2) Color.White else Color.White.copy(0.3f)))
            StepDot(2, step >= 2)
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Rolünü Seç", fontSize = 11.sp, color = Color(0xFFBFDBFE))
            Text("Bilgilerini Gir", fontSize = 11.sp, color = if (step == 2) Color.White else Color(0xFFBFDBFE))
        }

        Column(
            Modifier.fillMaxSize().background(DerdimColors.Background, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            if (step == 1) {
                Text("Rolünüzü seçin", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Platformda nasıl yer almak istiyorsunuz?", fontSize = 14.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
                roles.forEach { option ->
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(16.dp)).background(Color.White)
                            .border(2.dp, DerdimColors.Border, RoundedCornerShape(16.dp))
                            .clickable { viewModel.onRoleChange(option.role); step = 2 }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(48.dp).background(Brush.linearGradient(option.gradient), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                            Icon(option.icon, null, tint = Color.White)
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(option.label, fontWeight = FontWeight.SemiBold)
                            Text(option.desc, fontSize = 12.sp, color = DerdimColors.MutedForeground)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = DerdimColors.MutedForeground)
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    selectedRole?.let {
                        Box(Modifier.size(40.dp).background(Brush.linearGradient(it.gradient), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(it.icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            Text("${it.label} Hesabı", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Bilgilerinizi doldurun", fontSize = 13.sp, color = DerdimColors.MutedForeground)
                        }
                    }
                }
                AuthTextField(value = name, onValueChange = viewModel::onNameChange, label = "İsim Soyisim", icon = Icons.Default.Person)
                AuthTextField(value = email, onValueChange = viewModel::onEmailChange, label = "E-posta", icon = Icons.Default.Email)
                AuthTextField(value = password, onValueChange = viewModel::onPasswordChange, label = "Şifre", icon = Icons.Default.Lock, visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation())
                TextButton(onClick = { showPassword = !showPassword }, modifier = Modifier.align(Alignment.End)) {
                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, modifier = Modifier.size(16.dp))
                    Text(if (showPassword) "Gizle" else "Göster", modifier = Modifier.padding(start = 4.dp))
                }
                AuthTextField(value = companyName, onValueChange = viewModel::onCompanyNameChange, label = "Firma / İşletme Adı", icon = Icons.Default.Business)
                ExposedDropdownMenuBox(expanded = cityExpanded, onExpandedChange = { cityExpanded = it }) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Şehir") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                        registerCities.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { viewModel.onCityChange(c); cityExpanded = false })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                AuthTextField(value = address, onValueChange = viewModel::onAddressChange, label = "Adres", icon = Icons.Default.LocationOn, singleLine = false)
                error?.let { Text(it, color = DerdimColors.Destructive, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) }
                AuthGradientButton(text = "Kayıt Ol", onClick = { viewModel.register(onRegisterSuccess) }, isLoading = isLoading)
            }
            Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.Center) {
                Text("Zaten hesabınız var mı? ", color = DerdimColors.MutedForeground)
                TextButton(onClick = onNavigateBack) { Text("Giriş Yapın", color = DerdimColors.Primary, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun StepDot(number: Int, active: Boolean) {
    Box(Modifier.size(24.dp).background(if (active) Color.White else Color.White.copy(0.3f), CircleShape), contentAlignment = Alignment.Center) {
        Text("$number", color = if (active) DerdimColors.Primary else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
