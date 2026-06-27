package com.derdimet.mobil.util

fun formatNumber(d: Double): String {
    if (d == d.toLong().toDouble()) return d.toLong().toString()
    val rounded = ((d * 100).toLong()) / 100.0
    return rounded.toString()
}

fun formatOneDecimal(d: Double): String {
    val rounded = kotlin.math.round(d * 10.0) / 10.0
    return if (rounded == rounded.toLong().toDouble()) "${rounded.toLong()}.0" else rounded.toString()
}
