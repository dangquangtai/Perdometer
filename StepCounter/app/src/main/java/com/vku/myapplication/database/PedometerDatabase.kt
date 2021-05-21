package com.vku.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pedometer::class], version = 1, exportSchema = false)
abstract class PedometerDatabase : RoomDatabase() {
    abstract val pedometerDatabaseDAO: PedometerDatabaseDAO

    companion object {

        @Volatile
        private var INSTANCE: PedometerDatabase? = null

        fun getInstance(context: Context): PedometerDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PedometerDatabase::class.java,
                        "pedometer_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}