package com.example.practive.database.book

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practive.R
import com.example.practive.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

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

        bookTitle = findViewById(R.id.editBookTitle)
        bookAuthor = findViewById(R.id.editBookAuthor)
        bookPublish = findViewById(R.id.editBookPublish)
        imageView = findViewById(R.id.editphoto)
        saveBtn = findViewById(R.id.btnSave)
        deleteBtn = findViewById(R.id.deleteBtn)

        bookViewModel = ViewModelProvider(this).get(BookViewmodel::class.java)

        bookId = intent.getIntExtra("BOOK_ID", -1)

        if (bookId != -1) {
            loadBookData()
        } else {
            Toast.makeText(this, "Error: Invalid Book ID", Toast.LENGTH_SHORT).show()
        }

        imageView.setOnClickListener {
            pickImageFromGallery()
        }

        saveBtn.setOnClickListener {
            updateBook()
        }

        deleteBtn.setOnClickListener {
            confirmDelete()
        }
    }

    // Load book details with resized image
    private fun loadBookData() {
        CoroutineScope(Dispatchers.IO).launch {
            val bookDao = UserDatabase.getDatabase(this@EditBookActivity).bookDao()
            val existingBook = bookDao.getBookById(bookId)

            if (existingBook != null) {
                val resizedImage = existingBook.photo?.let { decodeSampledBitmap(it, 200, 200) }

                withContext(Dispatchers.Main) {
                    bookTitle.setText(existingBook.bookname)
                    bookAuthor.setText(existingBook.author)
                    bookPublish.setText(existingBook.publish)

                    Glide.with(this@EditBookActivity)
                        .load(resizedImage)
                        .apply(RequestOptions().override(200, 200))
                        .placeholder(R.drawable.user)
                        .into(imageView)

                    Log.d("EditBookActivity", "Book ID: $bookId, Resized Photo Size: ${existingBook.photo?.size ?: 0}")
                }
            }
        }
    }

    // Open image picker
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    // Handle image selection
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = result.data!!.data

            Glide.with(this)
                .asBitmap()
                .load(uri)
                .override(300, 300)
                .into(imageView)

            CoroutineScope(Dispatchers.IO).launch {
                selectedImage = uri?.let { uriToByteArray(it) }
            }
        }
    }

    // Convert image Uri to ByteArray (with resizing)
    private fun uriToByteArray(uri: Uri): ByteArray {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Resize image before saving
            val resizedBitmap = decodeSampledBitmapFromBitmap(bitmap, 300, 300)

            val stream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
            stream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    // Update book in database
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
                    photo = selectedImage ?: existingBook.photo
                )

                bookDao.updateBook(updatedBook)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditBookActivity, "Book Updated Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    // Show delete confirmation dialog
    private fun confirmDelete() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Book")
        builder.setMessage("Are you sure you want to delete this book?")
        builder.setPositiveButton("Yes") { _, _ -> deleteBook() }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // Delete book
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

    // Efficiently decode ByteArray to Bitmap
    private fun decodeSampledBitmap(byteArray: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, this)

            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
        }

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    }

    // Resize Bitmap
    private fun decodeSampledBitmapFromBitmap(bitmap: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true)
    }

    // Calculate optimal inSampleSize for image scaling
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        while ((height / inSampleSize) >= reqHeight && (width / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }

        return inSampleSize
    }
}
