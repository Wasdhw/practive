package com.example.practive.database.borrow

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practive.R
import kotlinx.coroutines.launch

class BorrowBookActivity : AppCompatActivity() {
    private val borrowViewModel: BorrowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_book)

        val bookId = intent.getIntExtra("BOOK_ID", -1)
        val bookTitle = intent.getStringExtra("BOOK_TITLE") ?: "Unknown Title"
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR") ?: "Unknown Author"
        val bookPublish = intent.getStringExtra("BOOK_PUBLISH") ?: "Unknown Date"
        val bookImageBytes = intent.getByteArrayExtra("BOOK_IMAGE")

        val titleTextView: TextView = findViewById(R.id.bookTitle)
        val authorTextView: TextView = findViewById(R.id.bookAuthor)
        val publishTextView: TextView = findViewById(R.id.bookPublish)
        val bookImageView: ImageView = findViewById(R.id.borrowphoto)
        val borrowButton: Button = findViewById(R.id.borrowButton)

        titleTextView.text = "Title: $bookTitle"
        authorTextView.text = "Author: $bookAuthor"
        publishTextView.text = "Published: $bookPublish"

        if (bookImageBytes != null) {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(bookImageBytes, 0, bookImageBytes.size)
            bookImageView.setImageBitmap(bitmap)
        } else {
            bookImageView.setImageResource(R.drawable.user)
        }

        borrowButton.setOnClickListener {
            if (bookId != -1) {
                val userId = 1 // Change this to fetch actual user ID
                lifecycleScope.launch {
                    val isBorrowed = borrowViewModel.isBookAlreadyBorrowed(userId, bookId)
                    if (isBorrowed) {
                        Toast.makeText(this@BorrowBookActivity, "You have already borrowed this book!", Toast.LENGTH_LONG).show()
                        finish()

                    } else {
                        borrowViewModel.borrowBook(userId, bookId)
                        Toast.makeText(this@BorrowBookActivity, "Book Borrowed Successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Error: Invalid Book ID", Toast.LENGTH_LONG).show()
            }
        }
    }
}
