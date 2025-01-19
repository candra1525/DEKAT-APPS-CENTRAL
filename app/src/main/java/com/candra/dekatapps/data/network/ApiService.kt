package com.candra.dekatapps.data.network

import com.candra.dekatapps.data.response.CuacaResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/api/cuaca")
    suspend fun getAllDataCuaca(): CuacaResponse

    @DELETE("/api/api/cuaca/{id}")
    suspend fun deleteDataCuaca( @Path("id") id: String): CuacaResponse
}