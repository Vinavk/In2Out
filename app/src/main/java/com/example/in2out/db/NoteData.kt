package com.example.in2out.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var intime: String,
    var outime: String
)