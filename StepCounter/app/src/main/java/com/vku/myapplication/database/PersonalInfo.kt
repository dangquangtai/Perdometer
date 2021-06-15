package com.vku.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_table")
data class PersonalInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "sex")
    var sex: String = "male",
    @ColumnInfo(name = "age")
    var age: Int = 25,
    @ColumnInfo(name = "weight")
    var weight: Int = 70,
    @ColumnInfo(name = "height")
    var height: Int = 170,
    @ColumnInfo(name = "stepLength")
    var stepLength: Int = 70
)