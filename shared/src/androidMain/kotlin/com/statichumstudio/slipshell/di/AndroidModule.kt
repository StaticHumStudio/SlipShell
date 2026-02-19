package com.statichumstudio.slipshell.di

import com.statichumstudio.slipshell.data.credential.AndroidCredentialStore
import com.statichumstudio.slipshell.data.credential.CredentialStore
import com.statichumstudio.slipshell.data.db.DatabaseDriverFactory
import com.statichumstudio.slipshell.data.repository.ServerProfileRepository
import com.statichumstudio.slipshell.data.repository.SqlDelightServerProfileRepository
import com.statichumstudio.slipshell.db.SlipShellDb
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(get()) }
    single { SlipShellDb(get<DatabaseDriverFactory>().create()) }
    single<CredentialStore> { AndroidCredentialStore(get()) }
    single<ServerProfileRepository> { SqlDelightServerProfileRepository(get()) }
}
