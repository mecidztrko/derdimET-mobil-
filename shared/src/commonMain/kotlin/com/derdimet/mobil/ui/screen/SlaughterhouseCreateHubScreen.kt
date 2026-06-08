package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SlaughterhouseCreateHubScreen(marketService: MarketService) {
    var tab by remember { mutableStateOf("meat") }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "İlan Oluştur",
            subtitle = if (tab == "meat") "Et satış ilanı" else "Hayvan alım talebi",
        )
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            DerdimFilterTabs(
                tabs = listOf(
                    Triple("meat", "Et ilanı", 0),
                    Triple("purchase", "Alım talebi", 0),
                ),
                selectedKey = tab,
                onSelect = { tab = it },
            )
        }
        when (tab) {
            "meat" -> SlaughterhouseCreateMeatSaleRequestScreen(marketService, embedded = true)
            else -> SlaughterhouseCreateAnimalPurchaseScreen(marketService)
        }
    }
}
