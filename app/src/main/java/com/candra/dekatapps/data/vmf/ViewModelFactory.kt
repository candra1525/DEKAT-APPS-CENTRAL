package com.candra.dekatapps.data.vmf

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.candra.dekatapps.data.repository.CuacaRepository
import com.candra.dekatapps.di.Injection
import com.candra.dekatapps.ui.cuaca.CuacaViewModel

class ViewModelFactory (private val repository: CuacaRepository) : ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CuacaViewModel::class.java) -> CuacaViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}