package com.phcodesage.kwentaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.KwentaroRoot
import com.phcodesage.kwentaro.ui.onboarding.OnboardingScreen
import com.phcodesage.kwentaro.ui.theme.KwentaroTheme
import com.phcodesage.kwentaro.ui.theme.SolidSystemBars

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val app = application as KwentaroApp
        setContent {
            val settings by app.settings.settings.collectAsStateWithLifecycle<StoreSettings?>(null)
            KwentaroTheme(dynamicColor = settings?.dynamicColor ?: false) {
                val loaded = settings
                when {
                    loaded == null -> {
                        SolidSystemBars(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {}
                    }
                    !loaded.onboardingDone -> OnboardingScreen(loaded, app.settings)
                    else -> KwentaroRoot()
                }
            }
        }
    }
}
