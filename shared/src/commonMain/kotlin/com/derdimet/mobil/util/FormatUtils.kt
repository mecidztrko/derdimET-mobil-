package com.derdimet.mobil.util

fun formatNumber(d: Double): String {
    if (d == d.toLong().toDouble()) return d.toLong().toString()
    val rounded = ((d * 100).toLong()) / 100.0
    return rounded.toString()
}
