package com.example.practive.database.borrow

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.practive.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BorrowBookActivity : AppCompatActivity() {
    private val borrowViewModel: BorrowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_book)

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val bookId = intent.getIntExtra("BOOK_ID", -1)
        val bookTitle = intent.getStringExtra("BOOK_TITLE") ?: "Unknown Title"
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR") ?: "Unknown Author"
        val bookDesc = intent.getStringExtra("BOOK_DESC") ?: "Unknown Desc"
        val bookPublish = intent.getStringExtra("BOOK_PUBLISH") ?: "Unknown Date"
        val bookImageBytes = intent.getByteArrayExtra("BOOK_IMAGE")


        val titleTextView: TextView = findViewById(R.id.bookTitle)
        val authorTextView: TextView = findViewById(R.id.bookAuthor)
        val publishTextView: TextView = findViewById(R.id.bookPublish)
        val descriptionTextView: TextView = findViewById(R.id.bookDesc)
        val bookImageView: ImageView = findViewById(R.id.borrowphoto)
        val borrowButton: Button = findViewById(R.id.borrowButton)

        titleTextView.text = "Title: $bookTitle"
        authorTextView.text = "Author: $bookAuthor"
        publishTextView.text = "Published: $bookPublish"
        descriptionTextView.text = "$bookDesc"

        Glide.with(this)
            .load(bookImageBytes)
            .override(500, 500)
            .placeholder(R.drawable.user)
            .into(bookImageView)

        borrowButton.setOnClickListener {
            if (bookId == -1) {
                Toast.makeText(this, "Error: Invalid Book ID", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            borrowButton.isEnabled = false

            lifecycleScope.launch {
                try {
                    val borrowCount = borrowViewModel.getBorrowCount(bookId) ?: 0
                    val totalCopies = borrowViewModel.getTotalCopies(bookId) ?: 0

                    Log.d("BorrowBookActivity", "Debug: borrowCount=$borrowCount, totalCopies=$totalCopies")

                    when {
                        borrowViewModel.isBookAlreadyBorrowed(userId, bookId) -> {
                            Toast.makeText(this@BorrowBookActivity, "You have already borrowed this book!", Toast.LENGTH_LONG).show()
                        }
                        borrowCount >= totalCopies ->  {
                            Toast.makeText(this@BorrowBookActivity, "This book is no longer available!", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            borrowViewModel.borrowBook(userId, bookId)
                            borrowViewModel.incrementBorrowCount(bookId)
                            borrowViewModel.decreaseTotalCopies(bookId)

                            Toast.makeText(this@BorrowBookActivity, "Book borrowed successfully!", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BorrowBookActivity", "Error: ${e.message}", e)
                    Toast.makeText(this@BorrowBookActivity, "Failed to borrow book. Try again.", Toast.LENGTH_LONG).show()
                } finally {
                    delay(1000)
                    finish()
                }
            }
        }
    }
}