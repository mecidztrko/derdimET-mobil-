package com.derdimet.mobil.util

fun toggleFavoriteIdSet(ids: Set<Long>, userId: Long, isFavorited: Boolean): Set<Long> =
    if (isFavorited) ids + userId else ids - userId
