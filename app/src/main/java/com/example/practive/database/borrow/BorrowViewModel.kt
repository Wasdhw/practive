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
        Log.d("BorrowViewModel", "📚 Borrow request received for User ID: $userId and Book ID: $bookId")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bookTitle = bookDao.getBookTitle(bookId) ?: "Unknown Title"
                val bookPhoto = bookDao.getBookPhoto(bookId)

                val borrow = BorrowRecord(
                    borrowId = 0,
                    userId = userId,
                    bookId = bookId,
                    bookTitle = bookTitle,
                    borrowDate = System.currentTimeMillis(),
                    returnDate = System.currentTimeMillis() + (returnDays * 24 * 60 * 60 * 1000),
                    isReturned = false,
                    bookPhoto = bookPhoto
                )
                borrowDao.insertBorrow(borrow)
                Log.d("BorrowViewModel", "✅ Inserted new borrow record: $borrow")
            } catch (e: Exception) {
                Log.e("BorrowViewModel", "❌ Error borrowing book", e)
            }
        }
    }

    suspend fun isBookAlreadyBorrowed(userId: Int, bookId: Int): Boolean {
        return borrowDao.isBookBorrowed(userId, bookId) != null
    }

    suspend fun getBorrowCount(bookId: Int): Int = bookDao.getBorrowCount(bookId)

    suspend fun getTotalCopies(bookId: Int): Int = bookDao.getTotalCopies(bookId)


    suspend fun incrementBorrowCount(bookId: Int) {
        bookDao.incrementBorrowCount(bookId)
    }

    fun decreaseTotalCopies(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = bookDao.getBookById(bookId)
            if (book != null && book.totalCopies > 0) {
                bookDao.updateTotalCopies(bookId, book.totalCopies - 1)
            }
        }
    }

    fun updateTotalCopies(bookId: Int, newTotal: Int) {
        viewModelScope.launch {
            bookDao.updateTotalCopies(bookId, newTotal)
        }
    }


    fun markAsReturned(borrowId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            borrowDao.updateReturnStatus(borrowId, true)
            Log.d("BorrowViewModel", "✅ Book with Borrow ID: $borrowId marked as returned")

            // Fetch updated list (forces LiveData to refresh)
            val updatedList = borrowDao.getAllBorrowsWithUsers().value
            Log.d("BorrowViewModel", "📌 Updated Borrow List: $updatedList")
        }
    }

    fun updateBorrowCount(bookId: Int, newBorrowCount: Int) {
        viewModelScope.launch {
            borrowDao.updateBorrowCount(bookId, newBorrowCount)
        }
    }


    fun getAllBorrowsWithUsers(): LiveData<List<BorrowWithUser>> {
        return borrowDao.getAllBorrowsWithUsers()
    }

    fun getUserBorrows(userId: Int): LiveData<List<BorrowWithUser>> {
        return borrowDao.getUserBorrowsWithUsers(userId)
    }
}
