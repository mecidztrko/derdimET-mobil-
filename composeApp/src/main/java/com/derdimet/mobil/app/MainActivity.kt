package com.derdimet.mobil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.derdimet.mobil.ui.DerdimAndroidApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebasePushSetup.initIfAvailable(this)
        setContent {
            DerdimAndroidApp(apiBaseUrl = BuildConfig.API_BASE_URL)
        }
    }
}
