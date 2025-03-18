package com.example.practive.database.borrow

import androidx.room.ColumnInfo

data class BorrowWithUser(
    val borrowId: Int,
    val userId: Int,
    val username: String,

    @ColumnInfo(name = "bookId")
    val bookId: Int,

    @ColumnInfo(name = "bookTitle")  // ✅ Must match the alias in DAO query
    val bookTitle: String,

    val borrowDate: Long,
    val returnDate: Long?,
    var isReturned: Boolean = false,

    @ColumnInfo(name = "bookPhoto")  // ✅ Must match alias in DAO query
    val bookPhoto: ByteArray?
)