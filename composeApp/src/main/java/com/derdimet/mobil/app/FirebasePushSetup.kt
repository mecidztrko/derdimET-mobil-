package com.derdimet.mobil.app

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

object FirebasePushSetup {
    private const val TAG = "DerdimFirebase"

    fun initIfAvailable(activity: MainActivity) {
        try {
            if (FirebaseApp.getApps(activity).isEmpty()) {
                FirebaseApp.initializeApp(activity)
            }
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "FCM token alınamadı", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result ?: return@addOnCompleteListener
                PushTokenRegistrar.register(
                    context = activity.applicationContext,
                    token = token,
                    apiBaseUrl = BuildConfig.API_BASE_URL,
                )
            }
        } catch (e: Exception) {
            Log.i(TAG, "Firebase kullanılamıyor (google-services.json eksik olabilir): ${e.message}")
        }
    }
}
