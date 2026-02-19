package com.statichumstudio.slipshell.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.statichumstudio.slipshell.db.SlipShellDb

class DatabaseDriverFactory(private val context: Context) {
    fun create(): SqlDriver {
        return AndroidSqliteDriver(SlipShellDb.Schema, context, "slipshell.db")
    }
}
