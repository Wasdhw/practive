package com.example.practive.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practive.R
import com.example.practive.database.book.Book
import com.example.practive.database.UserDatabase
import com.example.practive.databinding.ActivityInsertbooksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class insertbooks : AppCompatActivity() {

    private lateinit var binding: ActivityInsertbooksBinding
    private lateinit var bookDatabase: UserDatabase
    private var selectedImage: ByteArray? = null
    private lateinit var adbuks2: TextView
    private lateinit var adakawnt2: TextView

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
            Glide.with(this)
                .asBitmap()
                .load(uri)
                .override(500, 500)
                .into(binding.frontview)

            // Convert selected image to ByteArray
            uri?.let { imageUri ->
                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    selectedImage = inputStream.readBytes() // Save ByteArray for database
                }
            }
        }
    }


    private fun registerBook() {
        val title = binding.Title.text.toString().trim()
        val author = binding.author.text.toString().trim()
        val publishDate = binding.publish.text.toString().trim()
        val desc = binding.description.text.toString().trim()
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
                desc = desc,
                publish = publishDate,
                photo = selectedImage,
                totalCopies = totalCopies,
                borrowCount = 0
            )
            bookDatabase.bookDao().addBook(book)

            Log.d("InsertBooks", "Book added to database - Title: $title, Desc: $desc")

            runOnUiThread {
                Toast.makeText(this@insertbooks, "Book added successfully!", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this@insertbooks, adbooks::class.java))
                finish()
            }
        }
    }

}