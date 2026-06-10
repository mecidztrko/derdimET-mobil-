package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.BuyerPurchaseItemDto
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.model.SellerSaleItemDto
import com.derdimet.mobil.model.SlaughterhousePurchaseItemDto
import com.derdimet.mobil.model.SlaughterhouseSaleItemDto
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber

@Composable
fun MyPurchasesScreen(
    userRole: UserRole,
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var buyerPurchases by remember { mutableStateOf<List<BuyerPurchaseItemDto>>(emptyList()) }
    var sellerSales by remember { mutableStateOf<List<SellerSaleItemDto>>(emptyList()) }
    var shPurchases by remember { mutableStateOf<List<SlaughterhousePurchaseItemDto>>(emptyList()) }
    var shSales by remember { mutableStateOf<List<SlaughterhouseSaleItemDto>>(emptyList()) }
    var shTab by remember { mutableStateOf("purchases") }

    LaunchedEffect(userRole) {
        loading = true
        error = null
        when (userRole) {
            UserRole.MEAT_BUYER -> {
                val res = marketService.fetchMyPurchases(limit = 50)
                if (res.success) buyerPurchases = res.data ?: emptyList() else error = res.message
            }
            UserRole.ANIMAL_SELLER -> {
                val res = marketService.fetchSellerSales(limit = 50)
                if (res.success) {
                    sellerSales = (res.data ?: emptyList()).filter { it.status == OfferStatus.ACCEPTED }
                } else error = res.message
            }
            UserRole.SLAUGHTERHOUSE -> {
                val p = marketService.fetchSlaughterhousePurchases(limit = 50)
                val s = marketService.fetchSlaughterhouseSales(limit = 50)
                if (p.success) shPurchases = (p.data ?: emptyList()).filter { it.status == OfferStatus.ACCEPTED }
                if (s.success) shSales = s.data ?: emptyList()
                if (!p.success && !s.success) error = p.message ?: s.message
            }
            else -> Unit
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = if (userRole == UserRole.ANIMAL_SELLER) "Satışlarım" else "Alışverişlerim",
            showBack = true,
            onBack = onBack,
        )
        DerdimListScreenBody(
            header = {
                if (userRole == UserRole.SLAUGHTERHOUSE) {
                    DerdimFilterTabs(
                        tabs = listOf(
                            Triple("purchases", "Alımlar", shPurchases.size),
                            Triple("sales", "Satışlar", shSales.size),
                        ),
                        selectedKey = shTab,
                        onSelect = { shTab = it },
                    )
                } else if (userRole == UserRole.MEAT_BUYER) {
                    Text(
                        "Kabul edilen teklifler ve tamamlanan alımlar",
                        fontSize = 12.sp,
                        color = DerdimColors.MutedForeground,
                    )
                }
            },
            content = {
                when {
                    loading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    userRole == UserRole.MEAT_BUYER -> BuyerPurchaseList(buyerPurchases)
                    userRole == UserRole.ANIMAL_SELLER -> SellerSaleList(sellerSales)
                    userRole == UserRole.SLAUGHTERHOUSE -> {
                        if (shTab == "purchases") ShPurchaseList(shPurchases) else ShSaleList(shSales)
                    }
                    else -> Text("Kayıt bulunamadı.", color = DerdimColors.MutedForeground)
                }
            },
        )
    }
}

@Composable
private fun BuyerPurchaseList(items: List<BuyerPurchaseItemDto>) {
    if (items.isEmpty()) {
        EmptyPurchaseHint("Henüz kabul edilmiş alışveriş yok.")
    } else {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { it.orderId ?: it.meatOfferId ?: it.createdAt.hashCode().toLong() }) { item ->
                PurchaseDetailCard(
                    title = item.saleTitle ?: "Et ilanı",
                    subtitle = listOfNotNull(
                        item.slaughterhouseCompanyName ?: item.slaughterhouseName,
                        item.meatType,
                    ).joinToString(" · "),
                    statusLabel = purchaseStatusLabel(item.status),
                    statusColor = DerdimColors.Green700,
                    statusBg = DerdimColors.Green50,
                    detailRows = listOfNotNull(
                        item.pricePerKg?.let { "Teklif fiyatı" to "${formatNumber(it)} ₺/kg" },
                        item.quantity?.let { "Miktar" to "${formatNumber(it)} kg" },
                        item.totalPrice?.let { "Toplam" to "${formatNumber(it)} ₺" },
                        item.saleRequestId?.let { "İlan no" to "#$it" },
                    ),
                    date = item.createdAt.take(10),
                )
            }
        }
    }
}

@Composable
private fun SellerSaleList(items: List<SellerSaleItemDto>) {
    if (items.isEmpty()) {
        EmptyPurchaseHint("Henüz kabul edilmiş satış yok.")
    } else {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { "${it.saleType ?: "SALE"}_${it.offerId}" }) { item ->
                PurchaseDetailCard(
                    title = item.requestTitle ?: item.listingTitle ?: "Satış",
                    subtitle = listOfNotNull(
                        item.slaughterhouseCompanyName ?: item.slaughterhouseName,
                        saleTypeLabel(item.saleType),
                    ).joinToString(" · "),
                    statusLabel = "Kabul",
                    statusColor = DerdimColors.Green700,
                    statusBg = DerdimColors.Green50,
                    detailRows = listOfNotNull(
                        item.pricePerKg?.let { "Fiyat" to "${formatNumber(it)} ₺/kg" },
                        item.animalCount?.let { "Adet" to "$it baş" },
                        item.estimatedTotal?.let { "Tahmini toplam" to "${formatNumber(it)} ₺" },
                        item.listingId?.let { "İlan no" to "#$it" },
                        item.requestId?.let { "Talep no" to "#$it" },
                    ),
                    date = item.createdAt.take(10),
                )
            }
        }
    }
}

@Composable
private fun ShPurchaseList(items: List<SlaughterhousePurchaseItemDto>) {
    if (items.isEmpty()) {
        EmptyPurchaseHint("Henüz kabul edilmiş alım yok.")
    } else {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { "${it.purchaseType ?: "PURCHASE"}_${it.offerId}" }) { item ->
                PurchaseDetailCard(
                    title = item.requestTitle ?: item.listingTitle ?: "Alım",
                    subtitle = listOfNotNull(
                        item.sellerCompanyName ?: item.sellerName,
                        saleTypeLabel(item.purchaseType),
                    ).joinToString(" · "),
                    statusLabel = "Kabul",
                    statusColor = DerdimColors.Green700,
                    statusBg = DerdimColors.Green50,
                    detailRows = listOfNotNull(
                        item.pricePerKg?.let { "Fiyat" to "${formatNumber(it)} ₺/kg" },
                        item.animalCount?.let { "Adet" to "$it baş" },
                        item.estimatedTotal?.let { "Tahmini toplam" to "${formatNumber(it)} ₺" },
                        item.listingId?.let { "İlan no" to "#$it" },
                        item.requestId?.let { "Talep no" to "#$it" },
                    ),
                    date = item.createdAt.take(10),
                )
            }
        }
    }
}

@Composable
private fun ShSaleList(items: List<SlaughterhouseSaleItemDto>) {
    if (items.isEmpty()) {
        EmptyPurchaseHint("Henüz et satışı yok.")
    } else {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { it.orderId }) { item ->
                PurchaseDetailCard(
                    title = item.saleTitle ?: "Et satışı",
                    subtitle = listOfNotNull(item.buyerName, item.meatType).joinToString(" · "),
                    statusLabel = purchaseStatusLabel(item.status ?: "COMPLETED"),
                    statusColor = DerdimColors.Green700,
                    statusBg = DerdimColors.Green50,
                    detailRows = listOfNotNull(
                        item.totalPrice?.let { "Toplam" to "${formatNumber(it)} ₺" },
                        item.saleRequestId?.let { "İlan no" to "#$it" },
                        item.meatOfferId?.let { "Teklif no" to "#$it" },
                    ),
                    date = item.createdAt.take(10),
                )
            }
        }
    }
}

@Composable
private fun PurchaseDetailCard(
    title: String,
    subtitle: String,
    statusLabel: String,
    statusColor: Color,
    statusBg: Color,
    detailRows: List<Pair<String, String>>,
    date: String,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 2.dp))
                }
            }
            Text(
                statusLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor,
                modifier = Modifier
                    .background(statusBg, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        detailRows.forEach { (label, value) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontSize = 12.sp, color = DerdimColors.MutedForeground)
                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Text(date, fontSize = 11.sp, color = DerdimColors.MutedForeground)
    }
}

@Composable
private fun EmptyPurchaseHint(message: String) {
    Text(message, color = DerdimColors.MutedForeground, fontSize = 13.sp)
}

private fun purchaseStatusLabel(status: String): String = when (status.uppercase()) {
    "COMPLETED", "ACCEPTED" -> "Kabul"
    "PENDING" -> "Bekliyor"
    "CANCELLED", "REJECTED" -> "İptal"
    else -> status
}

private fun saleTypeLabel(type: String?): String? = when (type) {
    "PURCHASE_REQUEST" -> "Alış talebi"
    "DIRECT_LISTING" -> "Doğrudan ilan"
    else -> null
}
