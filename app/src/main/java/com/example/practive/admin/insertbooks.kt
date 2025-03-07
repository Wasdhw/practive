package com.example.practive.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practive.R
import com.example.practive.database.Book
import com.example.practive.database.UserDatabase
import com.example.practive.databinding.ActivityInsertbooksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class insertbooks : AppCompatActivity() {

    private lateinit var adbuks2: TextView
    private lateinit var adakawnt2: TextView
    private lateinit var addbtn: Button
    private lateinit var bookDatabase: UserDatabase
    private lateinit var nbinding: ActivityInsertbooksBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insertbooks)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adinsertpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        nbinding = ActivityInsertbooksBinding.inflate(LayoutInflater.from(this))
        enableEdgeToEdge()
        setContentView(nbinding.root)

        bookDatabase = UserDatabase.getDatabase(this)
        adbuks2 = findViewById(R.id.adBooks2)
        adakawnt2 = findViewById(R.id.adAccount2)
        addbtn = findViewById(R.id.addbtn)

        nbinding.addbtn.setOnClickListener {
            registerBook()
        }

        adbuks2.setOnClickListener {
            startActivity(Intent(this, adbooks::class.java))
        }
        adakawnt2.setOnClickListener {
            startActivity(Intent(this, adacc::class.java))
        }
    }

    private fun registerBook() {
        val title = nbinding.Title.text.toString().trim()
        val author = nbinding.author.text.toString().trim()
        val publishDate = nbinding.publish.text.toString().trim()

        if (title.isEmpty()) {
            nbinding.Title.error = "Title is required"
            return
        }
        if (author.isEmpty()) {
            nbinding.author.error = "Author is required"
            return
        }
        if (publishDate.isEmpty()) {
            nbinding.publish.error = "Publish date is required"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val book = Book(bookname = title, author = author, publish = publishDate)
            bookDatabase.bookDao().addBook(book)

            runOnUiThread {
                startActivity(Intent(this@insertbooks, adbooks::class.java))
                finish()
            }
        }
    }

}