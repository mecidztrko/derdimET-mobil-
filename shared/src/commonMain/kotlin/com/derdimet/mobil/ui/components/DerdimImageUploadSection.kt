package com.derdimet.mobil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.platform.rememberImagePickerLauncher
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.launch

@Composable
fun DerdimImageUploadSection(
    marketService: MarketService,
    imageUrls: List<String>,
    onImageUrlsChange: (List<String>) -> Unit,
    imageUrlInput: String,
    onImageUrlInputChange: (String) -> Unit,
) {
    var uploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val pickImage = rememberImagePickerLauncher { bytes, filename, contentType ->
        uploading = true
        uploadError = null
        scope.launch {
            val res = marketService.uploadImage(bytes, filename, contentType)
            uploading = false
            if (res.success && !res.data?.url.isNullOrBlank()) {
                onImageUrlsChange(imageUrls + res.data!!.url)
            } else {
                uploadError = res.message ?: "Görsel yüklenemedi"
            }
        }
    }

    DerdimFormCard(title = "Görseller", subtitle = "Galeriden seçin veya URL ekleyin") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FigmaSecondaryButton(
                text = if (uploading) "Yükleniyor..." else "Galeriden seç",
                onClick = { if (!uploading) pickImage() },
                enabled = !uploading,
                modifier = Modifier.weight(1f),
            )
            if (uploading) {
                CircularProgressIndicator(modifier = Modifier.weight(1f))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = imageUrlInput,
                onValueChange = onImageUrlInputChange,
                label = { Text("Görsel URL") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            FigmaSecondaryButton(
                text = "Ekle",
                onClick = {
                    val u = imageUrlInput.trim()
                    if (u.isNotBlank()) {
                        onImageUrlsChange(imageUrls + u)
                        onImageUrlInputChange("")
                    }
                },
            )
        }
        uploadError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (imageUrls.isNotEmpty()) {
            ImageCarousel(imageUrls = imageUrls)
            FigmaSecondaryButton(
                text = "Tüm görselleri kaldır",
                onClick = { onImageUrlsChange(emptyList()) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
