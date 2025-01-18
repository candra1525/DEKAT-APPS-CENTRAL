package com.candra.dekatapps.data.network

import com.candra.dekatapps.data.response.CuacaResponse
import retrofit2.http.GET

interface ApiService {
    @GET("/api/api/cuaca")
    suspend fun getAllDataCuaca(): CuacaResponse
}