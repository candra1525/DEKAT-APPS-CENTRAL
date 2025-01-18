package com.candra.dekatapps.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.candra.dekatapps.data.common.Result
import com.candra.dekatapps.data.network.ApiService
import com.candra.dekatapps.data.response.CuacaResponse

class CuacaRepository constructor(
    private val apiService: ApiService,
) {
    fun getAllDataCuaca() : LiveData<Result<CuacaResponse>> = liveData{
        emit(Result.Loading)
        try {
            val data = apiService.getAllDataCuaca()
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Error Occurred!"))
        }
    }

}