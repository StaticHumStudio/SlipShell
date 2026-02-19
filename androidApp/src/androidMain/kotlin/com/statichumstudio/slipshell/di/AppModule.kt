package com.statichumstudio.slipshell.di

import com.statichumstudio.slipshell.ui.viewmodel.ServerListViewModel
import com.statichumstudio.slipshell.ui.viewmodel.AddEditServerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { ServerListViewModel(get(), get()) }
    viewModel { params -> AddEditServerViewModel(params.get(), get(), get()) }
}
