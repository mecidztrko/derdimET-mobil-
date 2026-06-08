package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.derdimet.mobil.ui.theme.DerdimColors

data class SearchFilters(
    val type: String = "",
    val city: String = "",
    val priceMin: String = "",
    val priceMax: String = "",
    val weightMin: String = "",
    val weightMax: String = "",
    val verifiedOnly: Boolean = false,
    val sort: String = "newest",
)

val DefaultSearchFilters = SearchFilters()

val MeatTypeChips = listOf("Tümü", "Dana", "Kuzu", "Koyun", "Keçi")
val TurkishCities = listOf("", "İstanbul", "İzmir", "Ankara", "Bursa", "Konya", "Manisa", "Aydın", "Antalya")
val PricePresets = listOf(
    "0-100" to ("0" to "100"),
    "100-200" to ("100" to "200"),
    "200-300" to ("200" to "300"),
    "300+" to ("300" to ""),
)

@Composable
fun SheetDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = 12.dp)
            .size(width = 40.dp, height = 4.dp)
            .background(DerdimColors.MutedForeground.copy(alpha = 0.3f), RoundedCornerShape(999.dp)),
    )
}

@Composable
fun FilterSection(
    title: String,
    defaultOpen: Boolean = true,
    content: @Composable () -> Unit,
) {
    var open by remember { mutableStateOf(defaultOpen) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DerdimColors.Muted.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { open = !open },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DerdimColors.Foreground)
            Icon(
                if (open) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = DerdimColors.MutedForeground,
            )
        }
        if (open) content()
    }
}

@Composable
fun FilterChipButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(if (selected) DerdimColors.Primary else Color.White, RoundedCornerShape(12.dp))
            .border(2.dp, if (selected) DerdimColors.Primary else DerdimColors.Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        color = if (selected) Color.White else DerdimColors.MutedForeground,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun SearchFilterSheet(
    filters: SearchFilters,
    isAnimal: Boolean = false,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var local by remember(filters) { mutableStateOf(filters) }
    val activeCount = remember(local) {
        listOf(
            local.type.isNotBlank(),
            local.city.isNotBlank(),
            local.priceMin.isNotBlank() || local.priceMax.isNotBlank(),
            local.weightMin.isNotBlank() || local.weightMax.isNotBlank(),
            local.verifiedOnly,
        ).count { it }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DerdimColors.Background, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
    ) {
        SheetDragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Filtreler", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.clickable { local = DefaultSearchFilters },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(16.dp))
                    Text("Sıfırla", fontSize = 13.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 4.dp))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilterSection(if (isAnimal) "Hayvan Kategorisi" else "Et Türü") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    MeatTypeChips.forEach { chip ->
                        FilterChipButton(
                            label = chip,
                            selected = local.type.equals(chip, true) || (chip == "Tümü" && local.type.isBlank()),
                            onClick = { local = local.copy(type = if (chip == "Tümü") "" else chip) },
                        )
                    }
                }
            }

            FilterSection("Konum") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(2.dp, DerdimColors.Border, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = local.city.ifBlank { "Tüm Şehirler" },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { },
                        color = if (local.city.isBlank()) DerdimColors.MutedForeground else DerdimColors.Foreground,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    TurkishCities.filter { it.isNotBlank() }.forEach { city ->
                        FilterChipButton(city, local.city == city, onClick = { local = local.copy(city = city) })
                    }
                }
            }

            FilterSection("Fiyat Aralığı") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    PricePresets.forEach { (label, range) ->
                        val active = local.priceMin == range.first && local.priceMax == range.second
                        FilterChipButton(
                            label = "$label ₺",
                            selected = active,
                            onClick = {
                                local = if (active) local.copy(priceMin = "", priceMax = "")
                                else local.copy(priceMin = range.first, priceMax = range.second)
                            },
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = local.priceMin,
                        onValueChange = { local = local.copy(priceMin = it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Min ₺") },
                        singleLine = true,
                    )
                    Text("–", color = DerdimColors.MutedForeground)
                    OutlinedTextField(
                        value = local.priceMax,
                        onValueChange = { local = local.copy(priceMax = it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Max ₺") },
                        singleLine = true,
                    )
                }
            }

            FilterSection("Ağırlık Aralığı (kg)", defaultOpen = false) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = local.weightMin,
                        onValueChange = { local = local.copy(weightMin = it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Min kg") },
                        singleLine = true,
                    )
                    Text("–", color = DerdimColors.MutedForeground)
                    OutlinedTextField(
                        value = local.weightMax,
                        onValueChange = { local = local.copy(weightMax = it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Max kg") },
                        singleLine = true,
                    )
                }
            }

            FilterSection("Satıcı Özellikleri", defaultOpen = false) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { local = local.copy(verifiedOnly = !local.verifiedOnly) }
                        .background(if (local.verifiedOnly) DerdimColors.Green50 else Color.White, RoundedCornerShape(12.dp))
                        .border(2.dp, if (local.verifiedOnly) DerdimColors.Green400 else DerdimColors.Border, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Yalnızca Doğrulanmış Satıcılar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Kimlik ve belge doğrulaması yapılmış", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                    }
                    if (local.verifiedOnly) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = DerdimColors.Success)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
        ) {
            FigmaPrimaryButton(
                text = if (activeCount > 0) "Filtreleri Uygula ($activeCount)" else "Filtreleri Uygula",
                onClick = { onApply(local); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SortOptionSheet(
    current: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        "newest" to "En Yeni",
        "lowest" to "En Düşük Fiyat",
        "highest" to "En Yüksek Fiyat",
        "qtyasc" to "En Düşük Ağırlık",
        "qtydesc" to "En Yüksek Ağırlık",
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DerdimColors.Background, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(bottom = 24.dp),
    ) {
        SheetDragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
        Text("Sırala", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        options.forEach { (key, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(key); onDismiss() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, fontWeight = if (current == key) FontWeight.SemiBold else FontWeight.Normal)
                if (current == key) Icon(Icons.Default.Check, contentDescription = null, tint = DerdimColors.Primary)
            }
        }
    }
}
