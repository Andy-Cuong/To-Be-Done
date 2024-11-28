package com.example.tobedone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tobedone.model.TextNote

@Database(entities = [TextNote::class], version = 1, exportSchema = false)
abstract class ToBeDoneDatabase : RoomDatabase() {
    abstract fun getTextNoteDao(): TextNoteDao

    companion object {
        @Volatile
        private var Instance: ToBeDoneDatabase? = null

        fun getDatabase(context: Context): ToBeDoneDatabase =
            Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = ToBeDoneDatabase::class.java,
                    name = "to_be_done_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }.also { Instance = it }
    }
}