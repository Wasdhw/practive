package com.example.practive.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_data")
data class Book (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookname: String,
    val author: String,
    val publish: String

)
