package com.statichumstudio.slipshell

import android.app.Application
import com.statichumstudio.slipshell.di.androidModule
import com.statichumstudio.slipshell.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SlipShellApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SlipShellApp)
            modules(androidModule, appModule)
        }
    }
}
