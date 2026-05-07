package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.CreateMeatSaleRequestPayload
import com.derdimet.mobil.service.MarketService

@Composable
fun SlaughterhouseCreateMeatSaleRequestScreen(
    marketService: MarketService,
) {
    var title by remember { mutableStateOf("") }
    var meatType by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") } // kg
    var description by remember { mutableStateOf("") }

    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "İlan ver", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Et ilanı oluşturun.", color = Color.Gray)

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = meatType, onValueChange = { meatType = it }, label = { Text("Et türü") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = quantityText, onValueChange = { quantityText = it }, label = { Text("Miktar (kg)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama (opsiyonel)") },
            modifier = Modifier.fillMaxWidth(),
        )

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        success?.let { Text(it, color = Color(0xFF166534)) }

        Button(
            enabled = !submitting,
            onClick = { submitting = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) { Text(if (submitting) "Gönderiliyor..." else "İlanı Gönder") }
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect

        error = null
        success = null

        val q = quantityText.trim().replace(',', '.').toDoubleOrNull()
        if (title.isBlank() || meatType.isBlank() || q == null || q <= 0) {
            error = "Başlık, et türü ve geçerli miktar zorunlu."
            submitting = false
            return@LaunchedEffect
        }

        val res = marketService.createSlaughterhouseMeatSaleRequest(
            payload = CreateMeatSaleRequestPayload(
                title = title.trim(),
                meatType = meatType.trim(),
                quantity = q,
                description = description.trim().ifBlank { null },
            )
        )

        if (!res.success) {
            error = res.message ?: "İlan oluşturulamadı"
            submitting = false
            return@LaunchedEffect
        }

        success = "İlan oluşturuldu."
        title = ""
        meatType = ""
        quantityText = ""
        description = ""
        submitting = false
    }
}

