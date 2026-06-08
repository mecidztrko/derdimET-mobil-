package com.derdimet.mobil.ui.theme

import androidx.compose.ui.graphics.Color

object DerdimColors {
    val Background = Color(0xFFF0F7FF)
    val Foreground = Color(0xFF0F1C3F)
    val Card = Color(0xFFFFFFFF)
    val Primary = Color(0xFF1E3A8A)
    val PrimaryForeground = Color(0xFFFFFFFF)
    val Secondary = Color(0xFFDBEAFE)
    val Muted = Color(0xFFEFF6FF)
    val MutedForeground = Color(0xFF64748B)
    val Border = Color(0xFFBFDBFE)
    val Destructive = Color(0xFFDC2626)
    val Success = Color(0xFF16A34A)
    val Warning = Color(0xFFD97706)
    val Ring = Color(0xFF3B82F6)

    val Amber50 = Color(0xFFFFFBEB)
    val Amber100 = Color(0xFFFEF3C7)
    val Amber400 = Color(0xFFFBBF24)
    val Amber600 = Color(0xFFD97706)
    val Amber700 = Color(0xFFB45309)

    val Green50 = Color(0xFFF0FDF4)
    val Green100 = Color(0xFFD1FAE5)
    val Green400 = Color(0xFF4ADE80)
    val Green500 = Color(0xFF22C55E)
    val Green600 = Color(0xFF16A34A)
    val Green700 = Color(0xFF047857)

    val Red50 = Color(0xFFFEF2F2)
    val Red100 = Color(0xFFFEE2E2)
    val Red400 = Color(0xFFF87171)
    val Red600 = Color(0xFFDC2626)
    val Red700 = Color(0xFFB91C1C)
}

object DerdimTypeStyle {
    private val strip = mapOf(
        "dana" to DerdimColors.Ring,
        "kuzu" to DerdimColors.Amber400,
        "koyun" to Color(0xFFC084FC),
        "keçi" to DerdimColors.Green400,
        "küçükbaş" to Color(0xFFFB923C),
        "büyükbaş" to DerdimColors.Red400,
    )

    fun stripColor(type: String): Color {
        val key = type.lowercase()
        return strip.entries.firstOrNull { key.contains(it.key) }?.value ?: DerdimColors.Primary
    }
}

val AvatarPalettes = listOf(
    Color(0xFFDBEAFE) to Color(0xFF1D4ED8),
    Color(0xFFF3E8FF) to Color(0xFF7E22CE),
    Color(0xFFD1FAE5) to Color(0xFF047857),
    Color(0xFFFEF3C7) to Color(0xFFB45309),
    Color(0xFFFFE4E6) to Color(0xFFE11D48),
)

fun avatarPalette(index: Int): Pair<Color, Color> = AvatarPalettes[index % AvatarPalettes.size]
