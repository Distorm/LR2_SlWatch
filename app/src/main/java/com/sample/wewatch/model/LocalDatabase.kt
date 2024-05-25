package com.sample.wewatch.model

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Movie::class], version = 1)
@TypeConverters(IntegerListTypeConverter::class)
abstract class LocalDatabase : RoomDatabase() {

  // --- DAO ---
  abstract fun movieDao(): MovieDao

    companion object {
      private val lock = Any()
      
      private const val DB_NAME = "movie_database"
      private var INSTANCE: LocalDatabase? = null

      fun getInstance(application: Application): LocalDatabase {
        synchronized(lock) {
          if (INSTANCE == null) {
            INSTANCE =
                Room.databaseBuilder(application, LocalDatabase::class.java, DB_NAME)
                    .allowMainThreadQueries()
                    .build()
          }
        }
        return INSTANCE!!
      }
    }
}
