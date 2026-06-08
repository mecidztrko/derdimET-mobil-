package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.BuyerPurchaseItemDto
import com.derdimet.mobil.model.SellerSaleItemDto
import com.derdimet.mobil.model.SlaughterhousePurchaseItemDto
import com.derdimet.mobil.model.SlaughterhouseSaleItemDto
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFilterTabs
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
                if (res.success) sellerSales = res.data ?: emptyList() else error = res.message
            }
            UserRole.SLAUGHTERHOUSE -> {
                val p = marketService.fetchSlaughterhousePurchases(limit = 50)
                val s = marketService.fetchSlaughterhouseSales(limit = 50)
                if (p.success) shPurchases = p.data ?: emptyList()
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
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            when {
                loading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                userRole == UserRole.MEAT_BUYER -> PurchaseList(buyerPurchases.map { it.orderId.toString() to "${it.status} · ${it.totalPrice?.let { p -> "${formatNumber(p)} ₺" } ?: "—"} · ${it.createdAt.take(10)}" })
                userRole == UserRole.ANIMAL_SELLER -> PurchaseList(sellerSales.map { it.offerId.toString() to "${it.requestTitle ?: "Satış"} · ${it.status.name} · ${it.createdAt.take(10)}" })
                userRole == UserRole.SLAUGHTERHOUSE -> {
                    DerdimFilterTabs(
                        tabs = listOf(
                            Triple("purchases", "Alımlar", shPurchases.size),
                            Triple("sales", "Satışlar", shSales.size),
                        ),
                        selectedKey = shTab,
                        onSelect = { shTab = it },
                    )
                    if (shTab == "purchases") {
                        PurchaseList(shPurchases.map { it.offerId.toString() to "${it.requestTitle ?: "Alım"} · ${it.sellerName ?: "—"} · ${it.createdAt.take(10)}" })
                    } else {
                        PurchaseList(shSales.map { it.orderId.toString() to "${it.saleTitle ?: "Satış"} · ${it.buyerName ?: "—"} · ${it.createdAt.take(10)}" })
                    }
                }
                else -> Text("Kayıt bulunamadı.", color = DerdimColors.MutedForeground)
            }
        }
    }
}

@Composable
private fun PurchaseList(items: List<Pair<String, String>>) {
    if (items.isEmpty()) {
        Text("Henüz kayıt yok.", color = DerdimColors.MutedForeground)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items, key = { it.first }) { (_, line) ->
                Column(
                    Modifier.fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                ) {
                    Text(line, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
