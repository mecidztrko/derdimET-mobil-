package com.derdimet.mobil.ui

import com.derdimet.mobil.repository.AuthStorage
import com.derdimet.mobil.repository.SettingsStorage
import platform.Foundation.NSUserDefaults

internal class IosSettingsStorage : SettingsStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getString(key: String): String? = defaults.stringForKey(key)

    override fun setString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    override fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }
}

internal class IosAuthStorage : AuthStorage {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val tokenKey = "derdimet_auth_token"
    private val refreshKey = "derdimet_refresh_token"
    private val expiresKey = "derdimet_token_expires_at"

    override fun getToken(): String? = defaults.stringForKey(tokenKey)

    override fun setToken(token: String) {
        defaults.setObject(token, tokenKey)
    }

    override fun clearToken() {
        defaults.removeObjectForKey(tokenKey)
    }

    override fun getRefreshToken(): String? = defaults.stringForKey(refreshKey)

    override fun setRefreshToken(token: String) {
        defaults.setObject(token, refreshKey)
    }

    override fun clearRefreshToken() {
        defaults.removeObjectForKey(refreshKey)
    }

    override fun getTokenExpiresAtMs(): Long? {
        val value = defaults.doubleForKey(expiresKey)
        return if (value > 0.0) value.toLong() else null
    }

    override fun setTokenExpiresAtMs(expiresAtMs: Long) {
        defaults.setDouble(expiresAtMs.toDouble(), expiresKey)
    }

    override fun clearTokenExpiresAtMs() {
        defaults.removeObjectForKey(expiresKey)
    }
}
