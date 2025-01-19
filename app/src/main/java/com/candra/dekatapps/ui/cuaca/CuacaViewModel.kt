package com.candra.dekatapps.ui.cuaca

import androidx.lifecycle.ViewModel
import com.candra.dekatapps.data.repository.CuacaRepository

class CuacaViewModel(private val repository: CuacaRepository) : ViewModel() {
    fun getAllDataCuaca() = repository.getAllDataCuaca()
    fun deleteDataCuaca(id: String) = repository.deleteDataCuaca(id)
}