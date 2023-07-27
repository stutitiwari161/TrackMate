package com.example.trackmate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [ContactModel::class], version = 1, exportSchema = false)
public abstract class TrackMateDatabase: RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {

        @Volatile
        private var INSTANCE: TrackMateDatabase? = null


        fun getDatabase(context: Context): TrackMateDatabase {

            INSTANCE?.let {
                return it
            }
            return synchronized(TrackMateDatabase::class.java) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackMateDatabase::class.java,
                    "track_mate_db"
                ).build()

                INSTANCE = instance
                instance
            }

        }
    }
}
