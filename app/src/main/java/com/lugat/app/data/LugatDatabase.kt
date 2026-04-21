package com.lugat.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lugat.app.data.dao.LugatDao
import com.lugat.app.data.entity.Mistake
import com.lugat.app.data.entity.Progress
import com.lugat.app.data.entity.Word

@Database(entities = [Word::class, Mistake::class, Progress::class], version = 1, exportSchema = false)
abstract class LugatDatabase : RoomDatabase() {
    abstract fun lugatDao(): LugatDao

    companion object {
        @Volatile
        private var INSTANCE: LugatDatabase? = null

        fun getDatabase(context: Context): LugatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LugatDatabase::class.java,
                    "lugat_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
