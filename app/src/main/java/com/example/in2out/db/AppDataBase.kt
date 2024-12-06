package com.example.in2out.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun GetTimeDao(): TimeDao
}