package com.seif.salatukalyawm.ui.adhkar

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdhkarViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdhkarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdhkarViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}