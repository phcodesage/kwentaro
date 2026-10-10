package com.phcodesage.kwentaro

import android.app.Application
import com.phcodesage.kwentaro.data.auth.AccountRepository
import com.phcodesage.kwentaro.data.auth.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KwentaroApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val accounts by lazy { AccountRepository(this) }
    val sessions by lazy { SessionManager(this, appScope) }

    /** True once we know whether a remembered account should be reopened. */
    private val _ready = MutableStateFlow(false)
    val ready = _ready.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            accounts.rememberedAccountId?.let { id -> accounts.get(id)?.let(sessions::open) }
            _ready.value = true
        }
    }
}
