package com.vku.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedometer_table")
data class Pedometer(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "day")
    var day: Long = 0L,
    @ColumnInfo(name = "number_steps")
    var numberSteps: Float = 0f,
    @ColumnInfo(name = "distance")
    var distance: Double = 0.0,
    @ColumnInfo(name = "countTime")
    var countTime: Long = 0L,
    @ColumnInfo(name = "speed")
    var speed: Float = 0f,
    @ColumnInfo(name = "caloriesBurned")
    var caloriesBurned: Float = 0f
)