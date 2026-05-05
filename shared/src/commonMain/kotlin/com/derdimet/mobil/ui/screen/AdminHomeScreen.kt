package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.ui.components.DashboardInlineMessage
import com.derdimet.mobil.ui.components.StatusTone
import com.derdimet.mobil.viewmodel.AdminViewModel

@Composable
fun AdminHomeScreen(viewModel: AdminViewModel) {
    val title by viewModel.title.collectAsState()
    val animalCategory by viewModel.animalCategory.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val expectedWeight by viewModel.expectedWeight.collectAsState()
    val description by viewModel.description.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Yönetici Paneli", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Hayvan alış ilanı oluştur (satıcılar görecek).",
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            fontSize = 15.sp,
            color = Color.Gray
        )
        Surface(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            color = Color(0xFFEEF2FF),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "İpucu: Net başlık ve adet bilgisi, satıcılardan daha hızlı teklif almanızı sağlar.",
                modifier = Modifier.padding(12.dp),
                color = Color(0xFF1D4ED8),
                fontSize = 13.sp
            )
        }

        error?.let { DashboardInlineMessage(text = it, tone = StatusTone.Danger, modifier = Modifier.padding(bottom = 8.dp)) }

        message?.let { DashboardInlineMessage(text = it, tone = StatusTone.Success, modifier = Modifier.padding(bottom = 8.dp)) }

        Label("Başlık *")
        OutlinedTextField(
            value = title,
            onValueChange = viewModel::onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("örn. 5 adet besi danası") }
        )

        Label("Hayvan türü *")
        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AnimalCategory.values().forEach { category ->
                FilterChip(
                    selected = animalCategory == category,
                    onClick = { viewModel.onCategoryChange(category) },
                    label = { Text(if (category == AnimalCategory.KUCUKBAS) "Küçükbaş" else "Büyükbaş") }
                )
            }
        }

        Label("Adet")
        OutlinedTextField(
            value = quantity,
            onValueChange = viewModel::onQuantityChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("İsteğe bağlı") }
        )

        Label("Beklenen ağırlık (kg)")
        OutlinedTextField(
            value = expectedWeight,
            onValueChange = viewModel::onWeightChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("İsteğe bağlı") }
        )

        Label("Açıklama")
        OutlinedTextField(
            value = description,
            onValueChange = viewModel::onDescriptionChange,
            modifier = Modifier.fillMaxWidth().height(100.dp),
            placeholder = { Text("İsteğe bağlı detay") },
            minLines = 3
        )

        Button(
            onClick = viewModel::submitRequest,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("İlanı yayınla", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
}
