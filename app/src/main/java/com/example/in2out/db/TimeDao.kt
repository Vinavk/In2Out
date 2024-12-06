package com.example.in2out.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimeDao {

    @Query("SELECT * FROM NoteData")
    suspend fun getAllTodo(): List<NoteData>

    @Insert
    suspend fun insert(noteData: NoteData)

    @Delete
    suspend fun delete(noteData: NoteData)
}
