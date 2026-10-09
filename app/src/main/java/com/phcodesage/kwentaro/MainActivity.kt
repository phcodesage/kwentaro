package com.phcodesage.kwentaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.KwentaroRoot
import com.phcodesage.kwentaro.ui.theme.KwentaroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as KwentaroApp
        setContent {
            val settings by app.settings.settings.collectAsStateWithLifecycle(StoreSettings())
            KwentaroTheme(dynamicColor = settings.dynamicColor) {
                KwentaroRoot()
            }
        }
    }
}
