package com.vku.myapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonalDatabaseDAO {

    @Insert
    suspend fun insert(param: PersonalInfo)

    @Update
    suspend fun update(param: PersonalInfo)

    @Query("SELECT * FROM info_table")
    fun getListPersonalInfo(): LiveData<List<PersonalInfo>>

}
