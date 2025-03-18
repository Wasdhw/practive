package com.example.practive.database.borrow

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BorrowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrow(borrow: BorrowRecord) //


    @Query("SELECT * FROM borrow_table WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun isBookBorrowed(userId: Int, bookId: Int): BorrowRecord?


    @Query("UPDATE borrow_table SET isReturned = 1, returnDate = :returnDate WHERE borrowId = :borrowId")
    suspend fun returnBook(borrowId: Int, returnDate: Long)


    @Query("SELECT * FROM borrow_table WHERE userId = :userId AND isReturned = 0")
    fun getUserBorrows(userId: Int): LiveData<List<BorrowRecord>>


    @Query("SELECT * FROM borrow_table ORDER BY borrowDate DESC")
    fun getAllBorrows(): LiveData<List<BorrowRecord>>


    @Query("UPDATE borrow_table SET isReturned = :isReturned WHERE borrowId = :borrowId")
    suspend fun updateReturnStatus(borrowId: Int, isReturned: Boolean)




    @Query("""
    SELECT 
        borrow_table.borrowId, 
        borrow_table.userId, 
        users.username, 
        borrow_table.bookId, 
        book_data.bookname AS bookTitle,  -- ✅ Fetch from book_data instead of borrow_table
        borrow_table.borrowDate, 
        borrow_table.returnDate, 
        borrow_table.isReturned, 
        book_data.photo AS bookPhoto  -- ✅ Fetch from book_data instead of borrow_table
    FROM borrow_table
    INNER JOIN users ON borrow_table.userId = users.id
    INNER JOIN book_data ON borrow_table.bookId = book_data.bookId  -- ✅ Proper Join

""")


    fun getAllBorrowsWithUsers(): LiveData<List<BorrowWithUser>>

    @Query("""
    SELECT 
        borrow_table.borrowId, 
        borrow_table.userId, 
        users.username, 
        borrow_table.bookId, 
        book_data.bookname AS bookTitle,  
        borrow_table.borrowDate, 
        borrow_table.returnDate, 
        borrow_table.isReturned, 
        book_data.photo AS bookPhoto  
    FROM borrow_table
    INNER JOIN users ON borrow_table.userId = users.id
    INNER JOIN book_data ON borrow_table.bookId = book_data.bookId  
    WHERE borrow_table.userId = :userId
""")
    fun getUserBorrowsWithUsers(userId: Int): LiveData<List<BorrowWithUser>>


}
