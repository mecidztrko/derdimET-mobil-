package com.derdimet.mobil.util

import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.ui.components.SearchFilters

fun parseFilterDoubleOrNull(s: String): Double? =
    s.trim().takeIf { it.isNotEmpty() }?.replace(',', '.')?.toDoubleOrNull()

fun matchesMeatListing(item: MeatSaleRequestDto, query: String, filters: SearchFilters): Boolean {
    val q = query.trim().lowercase()
    val meatType = filters.type.trim().lowercase()
    val city = filters.city.trim().lowercase()
    val wMin = parseFilterDoubleOrNull(filters.weightMin)
    val wMax = parseFilterDoubleOrNull(filters.weightMax)
    val pMin = parseFilterDoubleOrNull(filters.priceMin)
    val pMax = parseFilterDoubleOrNull(filters.priceMax)

    if (q.isNotBlank()) {
        val hay = listOf(
            item.title,
            item.meatType,
            item.slaughterhouseName,
            item.slaughterhouseCompanyName,
            item.location,
            item.slaughterhouseCity,
        ).joinToString(" ").lowercase()
        if (!hay.contains(q)) return false
    }
    if (meatType.isNotBlank() && !item.meatType.lowercase().contains(meatType)) return false
    if (city.isNotBlank()) {
        val loc = "${item.location ?: ""} ${item.slaughterhouseCity ?: ""}".lowercase()
        if (!loc.contains(city)) return false
    }
    val qty = item.quantity
    if (wMin != null && (qty == null || qty < wMin)) return false
    if (wMax != null && (qty == null || qty > wMax)) return false
    val price = item.pricePerKg
    if (pMin != null && (price == null || price < pMin)) return false
    if (pMax != null && (price == null || price > pMax)) return false
    return true
}

fun filterMeatListings(
    listings: List<MeatSaleRequestDto>,
    query: String,
    filters: SearchFilters,
): List<MeatSaleRequestDto> {
    val base = listings.filter { matchesMeatListing(it, query, filters) }
    return when (filters.sort) {
        "lowest" -> base.sortedBy { it.pricePerKg ?: Double.MAX_VALUE }
        "highest" -> base.sortedByDescending { it.pricePerKg ?: Double.MIN_VALUE }
        "qtyasc" -> base.sortedBy { it.quantity ?: Double.MAX_VALUE }
        "qtydesc" -> base.sortedByDescending { it.quantity ?: Double.MIN_VALUE }
        else -> base
    }
}

fun countMeatListings(
    listings: List<MeatSaleRequestDto>,
    query: String,
    filters: SearchFilters,
): Int = filterMeatListings(listings, query, filters).size
