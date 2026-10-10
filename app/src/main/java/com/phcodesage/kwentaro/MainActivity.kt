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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.flowOf
import com.phcodesage.kwentaro.ui.auth.AuthScreen
import com.phcodesage.kwentaro.ui.components.SessionScope
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
            val ready by app.ready.collectAsStateWithLifecycle()
            val session by app.sessions.session.collectAsStateWithLifecycle()
            val settings by remember(session) { session?.settings?.settings ?: flowOf(null) }
                .collectAsStateWithLifecycle<StoreSettings?>(null)
            KwentaroTheme(dynamicColor = settings?.dynamicColor ?: false) {
                val active = session
                val loaded = settings
                when {
                    !ready || (active != null && loaded == null) -> {
                        SolidSystemBars(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {}
                    }
                    active == null -> AuthScreen(app)
                    !loaded!!.onboardingDone -> OnboardingScreen(loaded, active.settings)
                    // key() rebuilds navigation per account; SessionScope isolates its ViewModels.
                    else -> key(active.account.id) { SessionScope(active) { KwentaroRoot() } }
                }
            }
        }
    }
}
