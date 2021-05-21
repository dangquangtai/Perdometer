package com.vku.myapplication.fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vku.myapplication.database.PedometerDatabaseDAO

class DataViewModelFactory(
    private val dataSource: PedometerDatabaseDAO,
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
            return DataViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}