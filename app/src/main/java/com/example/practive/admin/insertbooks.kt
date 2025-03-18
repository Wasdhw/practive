package com.example.practive.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.practive.R
import com.example.practive.database.book.Book
import com.example.practive.database.UserDatabase
import com.example.practive.databinding.ActivityInsertbooksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class insertbooks : AppCompatActivity() {

    private lateinit var binding: ActivityInsertbooksBinding
    private lateinit var bookDatabase: UserDatabase
    private var selectedImage: ByteArray? = null
    private lateinit var adbuks2:TextView
    private lateinit var adakawnt2:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertbooksBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        adbuks2 = findViewById(R.id.adBooks2)
        adakawnt2 = findViewById(R.id.adAccount2)

        adbuks2.setOnClickListener {
            startActivity(Intent(this, adbooks::class.java))
        }
        adakawnt2.setOnClickListener {
            startActivity(Intent(this, adacc::class.java))
        }

        bookDatabase = UserDatabase.getDatabase(this)

        binding.frontview.setOnClickListener {
            pickImageFromGallery()
        }

        binding.addbtn.setOnClickListener {
            registerBook()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = result.data!!.data
            val inputStream = contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmapToByteArray(bitmap)
            binding.frontview.setImageBitmap(bitmap)
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun registerBook() {
        val title = binding.Title.text.toString().trim()
        val author = binding.author.text.toString().trim()
        val publishDate = binding.publish.text.toString().trim()
        val totalCopiesStr = binding.totalCopies.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || publishDate.isEmpty() || totalCopiesStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val totalCopies = totalCopiesStr.toIntOrNull() ?: 1

        CoroutineScope(Dispatchers.IO).launch {
            val book = Book(
                bookname = title,
                author = author,
                publish = publishDate,
                photo = selectedImage,
                totalCopies = totalCopies,
                borrowCount = 0
            )
            bookDatabase.bookDao().addBook(book)

            runOnUiThread {
                Toast.makeText(this@insertbooks, "Book added successfully!", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this@insertbooks, adbooks::class.java))
                finish()
            }
        }
    }
}
