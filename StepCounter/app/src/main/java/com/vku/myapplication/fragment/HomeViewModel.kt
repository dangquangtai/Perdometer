package com.vku.myapplication.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vku.myapplication.database.Pedometer
import com.vku.myapplication.database.PedometerDatabaseDAO
import kotlinx.coroutines.launch

class HomeViewModel(
    val database: PedometerDatabaseDAO,
    application: Application, date: Long
) : AndroidViewModel(application) {
    var todayPedometer: Pedometer? = null
    val pedometerAll = database.getListPedometer(date)


    fun onInsertToday(param: Pedometer) {
        viewModelScope.launch {
            insert(param)
            todayPedometer = param
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            todayPedometer = null
        }
    }

    fun onUpdate(param: Pedometer) {
        viewModelScope.launch {
            update(param)
            todayPedometer = param
        }
    }


    private suspend fun insert(night: Pedometer) {
        database.insert(night)
    }

    private suspend fun update(param: Pedometer) {
        database.update(param)
    }

    suspend fun clear() {
        database.clear()
    }

}