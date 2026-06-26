package com.derdimet.mobil.util

import com.derdimet.mobil.model.ListingReview
import com.derdimet.mobil.model.ReviewDto
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

fun ReviewDto.toListingReview(): ListingReview = ListingReview(
    id = id.toString(),
    authorName = reviewerName?.takeIf { it.isNotBlank() } ?: "Kullanıcı",
    rating = rating,
    comment = comment.orEmpty(),
    timeAgo = formatReviewTimeAgo(createdAt),
)

fun formatReviewTimeAgo(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return ""
    return runCatching {
        val instant = runCatching { Instant.parse(createdAt) }
            .getOrElse { Instant.parse("${createdAt}Z") }
        val now = Clock.System.now()
        val days = instant.daysUntil(now, TimeZone.currentSystemDefault())
        when {
            days <= 0 -> "Bugün"
            days == 1 -> "Dün"
            days < 7 -> "$days gün önce"
            days < 30 -> "${days / 7} hafta önce"
            days < 365 -> "${days / 30} ay önce"
            else -> "${days / 365} yıl önce"
        }
    }.getOrDefault(createdAt.take(10))
}

fun formatReviewSummary(averageRating: Double, reviewCount: Long): String {
    if (reviewCount <= 0L) return "Henüz değerlendirme yok"
    return String.format("%.1f · %d değerlendirme", averageRating, reviewCount)
}
