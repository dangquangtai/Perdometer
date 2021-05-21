package com.vku.myapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PedometerDatabaseDAO {

    @Insert
    suspend fun insert(param: Pedometer)

    @Update
    suspend fun update(param: Pedometer)

//    @Query("SELECT * from pedometer_table WHERE id = :key")
//    suspend fun get(key: Long): Pedometer?

    @Query("SELECT * from pedometer_table WHERE  day= :key")
    suspend fun getDay(key: Long): Pedometer?

    @Query("DELETE FROM pedometer_table")
    suspend fun clear()

//    @Query("SELECT * FROM pedometer_table ORDER BY id DESC LIMIT 1")
//    suspend fun getTonight(): Pedometer?

    @Query("SELECT * FROM pedometer_table WHERE  day= :key")
    fun getListPedometer(key: Long): LiveData<List<Pedometer>>

    @Query("SELECT * FROM pedometer_table WHERE day BETWEEN :begin AND :end")
    fun getBetweenPedometer(begin: Long, end: Long): LiveData<List<Pedometer>>
}