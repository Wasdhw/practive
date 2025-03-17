package com.example.practive.database.borrow

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.practive.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BorrowViewModel(application: Application) : AndroidViewModel(application) {
    private val borrowDao = UserDatabase.getDatabase(application).borrowDao()
    private val bookDao = UserDatabase.getDatabase(application).bookDao()

    fun borrowBook(userId: Int, bookId: Int, returnDays: Int = 7) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bookTitle = bookDao.getBookTitle(bookId) ?: "Unknown Title"
                val bookPhoto = bookDao.getBookPhoto(bookId) // ✅ Fix: Retrieve the book cover photo

                val borrow = BorrowRecord(
                    userId = userId,
                    bookId = bookId,
                    bookTitle = bookTitle,
                    borrowDate = System.currentTimeMillis(),
                    returnDate = System.currentTimeMillis() + (returnDays * 24 * 60 * 60 * 1000),
                    isReturned = false,
                    bookPhoto = bookPhoto // ✅ Fix: Pass bookPhoto (BLOB)
                )

                borrowDao.insertBorrow(borrow)
                Log.d("BorrowViewModel", "Inserted new borrow record: $borrow")
            } catch (e: Exception) {
                Log.e("BorrowViewModel", "Error borrowing book", e)
            }
        }
    }

    suspend fun isBookAlreadyBorrowed(userId: Int, bookId: Int): Boolean {
        return borrowDao.isBookBorrowed(userId, bookId) != null
    }

    fun getUserBorrows(userId: Int): LiveData<List<BorrowRecord>> {
        return borrowDao.getUserBorrows(userId).also {
            it.observeForever { list ->
                Log.d("BorrowViewModel", "Retrieved ${list.size} borrowed books for user ID: $userId")
            }
        }
    }
}
