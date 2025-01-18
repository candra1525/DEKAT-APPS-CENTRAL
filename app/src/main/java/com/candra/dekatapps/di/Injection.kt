package com.candra.dekatapps.di

import android.content.Context
import com.candra.dekatapps.data.network.ApiConfig
import com.candra.dekatapps.data.repository.CuacaRepository

object Injection {
    fun provideRepository(context: Context): CuacaRepository {
        val apiService = ApiConfig.getDefaultApi()
        return CuacaRepository(apiService)
    }
}