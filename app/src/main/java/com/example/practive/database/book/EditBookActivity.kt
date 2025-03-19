package com.example.practive.database.book

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.practive.R
import com.example.practive.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class EditBookActivity : AppCompatActivity() {

    private lateinit var bookTitle: EditText
    private lateinit var bookAuthor: EditText
    private lateinit var bookPublish: EditText
    private lateinit var imageView: ImageView
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: ImageButton
    private var bookId: Int = -1
    private var selectedImage: ByteArray? = null
    private lateinit var bookViewModel: BookViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        // Initialize UI elements
        bookTitle = findViewById(R.id.editBookTitle)
        bookAuthor = findViewById(R.id.editBookAuthor)
        bookPublish = findViewById(R.id.editBookPublish)
        imageView = findViewById(R.id.editphoto)
        saveBtn = findViewById(R.id.btnSave)
        deleteBtn = findViewById(R.id.deleteBtn)

        bookViewModel = ViewModelProvider(this).get(BookViewmodel::class.java)

        // Get book details from Intent or Database
        bookId = intent.getIntExtra("BOOK_ID", -1)

        if (bookId != -1) {
            loadBookData()
        } else {
            Toast.makeText(this, "Error: Invalid Book ID", Toast.LENGTH_SHORT).show()
        }

        // Open image picker when clicking on ImageView
        imageView.setOnClickListener {
            pickImageFromGallery()
        }

        // Save book updates
        saveBtn.setOnClickListener {
            updateBook()
        }

        // Delete book
        deleteBtn.setOnClickListener {
            confirmDelete()
        }
    }

    // Load book details and image from the database
    private fun loadBookData() {
        CoroutineScope(Dispatchers.IO).launch {
            val bookDao = UserDatabase.getDatabase(this@EditBookActivity).bookDao()
            val existingBook = bookDao.getBookById(bookId)

            if (existingBook != null) {
                withContext(Dispatchers.Main) {
                    bookTitle.setText(existingBook.bookname)
                    bookAuthor.setText(existingBook.author)
                    bookPublish.setText(existingBook.publish)

                    Glide.with(this@EditBookActivity)
                        .load(existingBook.photo)
                        .placeholder(R.drawable.user) // Default image
                        .into(imageView)

                    // Debugging: Log image data
                    Log.d("EditBookActivity", "Book ID: $bookId, Photo Size: ${existingBook.photo?.size ?: 0}")
                }
            }
        }
    }

    // Function to pick an image from the gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    // Handle image selection result
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = result.data!!.data
            Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(imageView)
        }
    }

    // Convert Bitmap to ByteArray
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    // Update book details
    private fun updateBook() {
        if (bookId == -1) {
            Toast.makeText(this, "Error: Invalid book ID", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bookDao = UserDatabase.getDatabase(this@EditBookActivity).bookDao()
            val existingBook = bookDao.getBookById(bookId)

            if (existingBook != null) {
                val updatedBook = existingBook.copy(
                    bookname = bookTitle.text.toString(),
                    author = bookAuthor.text.toString(),
                    publish = bookPublish.text.toString(),
                    photo = selectedImage ?: existingBook.photo // Keep old image if no new one selected
                )

                bookDao.updateBook(updatedBook)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditBookActivity, "Book Updated Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    // confirmation
    private fun confirmDelete() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Book")
        builder.setMessage("Are you sure you want to delete this book?")
        builder.setPositiveButton("Yes") { _, _ -> deleteBook() }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // Delete
    private fun deleteBook() {
        if (bookId == -1) {
            Toast.makeText(this, "Error: Invalid book ID", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bookDao = UserDatabase.getDatabase(this@EditBookActivity).bookDao()
            val bookToDelete = bookDao.getBookById(bookId)

            if (bookToDelete != null) {
                bookDao.deleteBook(bookToDelete)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditBookActivity, "Book Deleted Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditBookActivity, "Error: Book Not Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}