package com.candra.dekatapps.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val BASE_URL = "https://laravel-api-cuaca-v1.vercel.app/"
        fun getApiService(url: String): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }

        fun getDefaultApi(): ApiService {
            return getApiService(BASE_URL)
        }
    }
}