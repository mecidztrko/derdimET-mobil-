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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SellerCreateListingScreen() {
    // Faz s5: backend’e ilan oluşturma endpointi bağlanacak (foto URL veya upload).
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var ageMonths by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "İlan Ver", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Hayvanlarını ilan olarak ekle.", color = Color.Gray)

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (KUCUKBAS/BUYUKBAS)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tür/Irk") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ageMonths, onValueChange = { ageMonths = it }, label = { Text("Yaş (ay)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Adet") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Görsel URL (şimdilik)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Açıklama") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = { /* Faz s5: submit */ },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("İlanı Gönder")
        }
        Text(
            text = "Not: Görsel yükleme ve kayıt işlemi bir sonraki adımda bağlanacak.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

