package com.example.practive.database.borrow

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "borrow_table")
data class BorrowRecord(
    @PrimaryKey(autoGenerate = true)
    val borrowId: Int = 0,
    val userId: Int,
    val bookId: Int,
    val borrowDate: Long,
    val returnDate: Long?,
    val isReturned: Boolean,
    val bookTitle: String,
    val bookPhoto: ByteArray?
)
