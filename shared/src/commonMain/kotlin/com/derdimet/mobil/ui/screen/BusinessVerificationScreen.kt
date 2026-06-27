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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.MeResponse
import com.derdimet.mobil.platform.rememberImagePickerLauncher
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.AuthGradientButton
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlinx.coroutines.launch

private val statusLabels = mapOf(
    "NONE" to "Başvuru yapılmadı",
    "PENDING" to "İnceleme bekliyor",
    "APPROVED" to "Onaylandı",
    "REJECTED" to "Reddedildi",
)

@Composable
fun BusinessVerificationScreen(
    marketService: MarketService,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var me by remember { mutableStateOf<MeResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var uploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(refreshKey) {
        loading = true
        loadError = null
        val res = marketService.fetchMe()
        if (res.success && res.data != null) {
            me = res.data
        } else {
            loadError = res.message ?: "Profil alınamadı"
        }
        loading = false
    }

    val pickDocument = rememberImagePickerLauncher { bytes, filename, contentType ->
        uploading = true
        error = null
        success = null
        scope.launch {
            val upload = marketService.uploadImage(bytes, filename, contentType)
            if (upload.success && !upload.data?.url.isNullOrBlank()) {
                val res = marketService.submitBusinessVerification(upload.data!!.url)
                uploading = false
                if (res.success) {
                    success = "Başvurunuz alındı. İnceleme sonucu bildirilecektir."
                    refreshKey++
                } else {
                    error = res.message ?: "Başvuru gönderilemedi"
                }
            } else {
                uploading = false
                error = upload.message ?: "Belge yüklenemedi"
            }
        }
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Kurumsal doğrulama", showBack = true, onBack = onBack)
        DerdimScreenState(
            loading = loading,
            error = loadError,
            empty = false,
            onRetry = { refreshKey++ },
            modifier = Modifier.fillMaxSize(),
        ) {
            val profile = me ?: return@DerdimScreenState
            if (profile.accountType != "BUSINESS") {
                Text(
                    "Kurumsal doğrulama yalnızca işletme hesapları için geçerlidir.",
                    modifier = Modifier.padding(16.dp),
                    color = DerdimColors.MutedForeground,
                )
                return@DerdimScreenState
            }
            val status = profile.businessVerificationStatus ?: if (profile.businessVerified) "APPROVED" else "NONE"
            val canSubmit = status == "NONE" || status == "REJECTED"
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Vergi levhası veya ticaret sicil belgesi yükleyerek işletmenizi doğrulatabilirsiniz.",
                    color = DerdimColors.MutedForeground,
                    fontSize = 14.sp,
                )
                DerdimFormCard(title = "Başvuru durumu", subtitle = statusLabels[status] ?: status) {
                    profile.businessVerificationNote?.let {
                        Text(it, fontSize = 13.sp, color = DerdimColors.MutedForeground)
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                success?.let { Text(it, color = Color(0xFF166534)) }
                if (canSubmit) {
                    AuthGradientButton(
                        text = if (uploading) "Gönderiliyor…" else "Belge yükle ve başvur",
                        isLoading = uploading,
                        onClick = pickDocument,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
