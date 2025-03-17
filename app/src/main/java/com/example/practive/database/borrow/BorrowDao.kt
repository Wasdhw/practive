package com.example.practive.database.borrow

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BorrowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrow(borrow: BorrowRecord)

    @Query("SELECT * FROM borrow_table WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun isBookBorrowed(userId: Int, bookId: Int): BorrowRecord?

    @Query("UPDATE borrow_table SET isReturned = 1, returnDate = :returnDate WHERE borrowId = :borrowId")
    suspend fun returnBook(borrowId: Int, returnDate: Long)

    @Query("SELECT * FROM borrow_table WHERE userId = :userId AND isReturned = 0")
    fun getUserBorrows(userId: Int): LiveData<List<BorrowRecord>>

}

