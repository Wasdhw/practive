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
    suspend fun getBookTitle(bookId: Int): String? // ✅ FIX: Return nullable String

    @Query("SELECT * FROM book_data WHERE bookname LIKE '%' || :query || '%'")
    fun searchBooks(query: String): LiveData<List<Book>> // ✅ FIX: Use LiveData for auto updates

    @Query("SELECT * FROM book_data WHERE bookId = :bookId")
    suspend fun getBookById(bookId: Int): Book?

    @Query("SELECT photo FROM book_data WHERE bookId = :bookId")
    fun getBookPhoto(bookId: Int): ByteArray?

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}
