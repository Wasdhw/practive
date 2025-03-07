package com.example.practive.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBook(book: Book)

    @Query("SELECT * FROM book_data ORDER BY id ASC")
    fun  readAllData(): LiveData<List<Book>>


}