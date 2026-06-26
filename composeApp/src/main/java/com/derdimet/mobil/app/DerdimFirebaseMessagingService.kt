package com.derdimet.mobil.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DerdimFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PushTokenRegistrar.register(
            context = applicationContext,
            token = token,
            apiBaseUrl = BuildConfig.API_BASE_URL,
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Bildirim gösterimi ileride eklenecek.
    }
}
