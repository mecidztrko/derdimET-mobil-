package com.derdimet.mobil.ui

import android.content.SharedPreferences
import com.derdimet.mobil.repository.AuthStorage
import com.derdimet.mobil.repository.SettingsStorage

internal class AndroidSettingsStorage(
    private val prefs: SharedPreferences
) : SettingsStorage {
    override fun getString(key: String): String? = prefs.getString(key, null)
    override fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}

internal class AndroidAuthStorage(
    private val prefs: SharedPreferences
) : AuthStorage {
    private val tokenKey = "derdimet_auth_token"

    override fun getToken(): String? = prefs.getString(tokenKey, null)

    override fun setToken(token: String) {
        prefs.edit().putString(tokenKey, token).apply()
    }

    override fun clearToken() {
        prefs.edit().remove(tokenKey).apply()
    }
}
