package com.vku.myapplication.fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vku.myapplication.database.PedometerDatabaseDAO

class HomeViewModelFactory(
    private val dataSource: PedometerDatabaseDAO,
    private val application: Application,
    private val date: Long,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataSource, application, date) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}