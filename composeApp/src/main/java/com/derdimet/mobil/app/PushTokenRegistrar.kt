package com.derdimet.mobil.app

import android.content.Context
import com.derdimet.mobil.model.DevicePlatform
import com.derdimet.mobil.model.MessageResponse
import com.derdimet.mobil.model.RegisterDeviceTokenRequest
import com.derdimet.mobil.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object PushTokenRegistrar {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun register(context: Context, token: String, apiBaseUrl: String) {
        val prefs = context.getSharedPreferences("derdimet_prefs", Context.MODE_PRIVATE)
        val authToken = prefs.getString("derdimet_auth_token", null) ?: return
        scope.launch {
            val api = ApiService(apiBaseUrl)
            api.setAuthToken(authToken)
            api.post<MessageResponse>(
                "/api/me/notifications/device-tokens",
                RegisterDeviceTokenRequest(token = token, platform = DevicePlatform.ANDROID),
            )
        }
    }
}
