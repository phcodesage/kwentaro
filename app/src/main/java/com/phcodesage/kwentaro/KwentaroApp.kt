package com.phcodesage.kwentaro

import android.app.Application
import com.phcodesage.kwentaro.data.AppDatabase
import com.phcodesage.kwentaro.data.PosRepository
import com.phcodesage.kwentaro.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class KwentaroApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val repository by lazy { PosRepository(AppDatabase.build(this)) }
    val settings by lazy { SettingsRepository(this) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { repository.seedIfEmpty() }
    }
}
