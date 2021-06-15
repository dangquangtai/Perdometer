package com.vku.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PersonalInfo::class], version = 5, exportSchema = false)
abstract class PersonalDatabase : RoomDatabase() {
    abstract val personalDatabaseDAO: PersonalDatabaseDAO

    companion object {

        @Volatile
        private var INSTANCE: PersonalDatabase? = null

        fun getInstance(context: Context): PersonalDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PersonalDatabase::class.java,
                        "personal_history_database"
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