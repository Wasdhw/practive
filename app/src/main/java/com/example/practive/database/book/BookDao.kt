package com.example.practive.database.book

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBook(book: Book)

    @Query("SELECT * FROM book_data ORDER BY bookId ASC")
    fun readAllData(): LiveData<List<Book>>

    @Query("SELECT bookname FROM book_data WHERE bookId = :bookId")
    suspend fun getBookTitle(bookId: Int): String?

    @Query("SELECT * FROM book_data WHERE bookname LIKE '%' || :query || '%'")
    fun searchBooks(query: String): LiveData<List<Book>>

    @Query("SELECT * FROM book_data WHERE bookId = :bookId")
    suspend fun getBookById(bookId: Int): Book?

    @Query("SELECT photo FROM book_data WHERE bookId = :bookId")
    fun getBookPhoto(bookId: Int): ByteArray?

    @Query("SELECT borrowCount FROM book_data WHERE bookId = :bookId")
    suspend fun getBorrowCount(bookId: Int): Int

    @Query("UPDATE book_data SET borrowCount = borrowCount + 1 WHERE bookId = :bookId")
    suspend fun incrementBorrowCount(bookId: Int): Int

    @Query("SELECT totalCopies FROM book_data WHERE bookId = :bookId")
    suspend fun getTotalCopies(bookId: Int): Int


    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}
