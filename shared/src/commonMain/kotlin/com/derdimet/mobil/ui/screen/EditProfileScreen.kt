package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.model.MeResponse
import com.derdimet.mobil.model.UpdateProfilePayload
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun EditProfileScreen(
    marketService: MarketService,
    authRepository: AuthRepository,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    var me by remember { mutableStateOf<MeResponse?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val res = marketService.fetchMe()
        if (res.success && res.data != null) {
            me = res.data
            name = res.data.name
            phone = res.data.phone.orEmpty()
            companyName = res.data.companyName.orEmpty()
            city = res.data.city.orEmpty()
            addressLine = res.data.addressLine.orEmpty()
        }
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Profili Düzenle", showBack = true, onBack = onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DerdimFormCard(title = "Kişisel Bilgiler", subtitle = me?.email) {
                OutlinedTextField(name, { name = it }, label = { Text("Ad Soyad") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(phone, { phone = it }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(companyName, { companyName = it }, label = { Text("Firma adı") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(city, { city = it }, label = { Text("Şehir") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(addressLine, { addressLine = it }, label = { Text("Adres") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            success?.let { Text(it, color = Color(0xFF166534)) }
            FigmaPrimaryButton(
                text = if (saving) "Kaydediliyor..." else "Kaydet",
                enabled = !saving && name.isNotBlank(),
                onClick = { saving = true },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    LaunchedEffect(saving) {
        if (!saving) return@LaunchedEffect
        error = null
        success = null
        val updated = authRepository.updateProfile(
            UpdateProfilePayload(
                name = name.trim(),
                phone = phone.trim().ifBlank { null },
                companyName = companyName.trim().ifBlank { null },
                city = city.trim().ifBlank { null },
                addressLine = addressLine.trim().ifBlank { null },
            )
        )
        saving = false
        if (updated != null) {
            success = "Profil güncellendi."
            onSaved()
        } else {
            error = "Profil güncellenemedi."
        }
    }
}
