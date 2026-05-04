package com.derdimet.mobil.util

object ValidationUtils {
    private val EMAIL_RE = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun validateEmail(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "E-posta gerekli"
            v.length > 255 -> "E-posta çok uzun"
            !EMAIL_RE.matches(v) -> "Geçerli bir e-posta girin"
            else -> null
        }
    }

    fun validateLoginPassword(value: String): String? {
        return if (value.isEmpty()) "Şifre gerekli" else null
    }

    fun validateRegisterPassword(value: String): String? {
        return when {
            value.isEmpty() -> "Şifre gerekli"
            value.length < 8 -> "Şifre en az 8 karakter olmalı"
            value.length > 128 -> "Şifre en fazla 128 karakter olabilir"
            else -> null
        }
    }

    fun validateName(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "Ad soyad gerekli"
            v.length > 200 -> "Ad soyad en fazla 200 karakter olabilir"
            else -> null
        }
    }

    fun validateCompanyName(value: String, isBusiness: Boolean): String? {
        if (!isBusiness) return null
        val v = value.trim()
        return when {
            v.isEmpty() -> "Şirket adı gerekli"
            v.length > 300 -> "Şirket adı en fazla 300 karakter olabilir"
            else -> null
        }
    }

    fun validateTaxNumber(value: String, isBusiness: Boolean): String? {
        if (!isBusiness) return null
        val v = value.trim()
        return when {
            v.isEmpty() -> "Vergi numarası gerekli"
            v.length > 50 -> "Vergi numarası en fazla 50 karakter olabilir"
            else -> null
        }
    }

    fun validatePasswordMatch(password: String, confirm: String): String? {
        return if (password != confirm) "Şifreler eşleşmiyor" else null
    }
}
