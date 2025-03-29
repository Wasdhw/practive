package com.example.practive.admin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.R
import com.example.practive.database.MyListAdapter
import com.example.practive.database.book.BookViewmodel
import com.example.practive.database.book.EditBookActivity

class adbooks : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyListAdapter
    private lateinit var bookViewModel: BookViewmodel
    private lateinit var adbarrow3: TextView
    private lateinit var adakawnt3: TextView

    private var fullBookList = mutableListOf<com.example.practive.database.book.Book>() // Store all books

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adbooks)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)


        adbarrow3 = findViewById(R.id.adBorrow3)
        adakawnt3 = findViewById(R.id.adAccount3)

        adbarrow3.setOnClickListener {
            startActivity(Intent(this, insertbooks::class.java))
        }
        adakawnt3.setOnClickListener {
            startActivity(Intent(this, adacc::class.java))
        }

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyListAdapter { selectedBook ->
            val intent = Intent(this, EditBookActivity::class.java).apply {
                putExtra("BOOK_ID", selectedBook.bookId)
                putExtra("BOOK_TITLE", selectedBook.bookname)
                putExtra("BOOK_AUTHOR", selectedBook.author)
                putExtra("BOOK_PUBLISH", selectedBook.publish)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Initialize ViewModel
        bookViewModel = ViewModelProvider(this).get(BookViewmodel::class.java)

        // Observe book data
        bookViewModel.readAllData.observe(this) { books ->
            fullBookList.clear()
            fullBookList.addAll(books)
            adapter.setData(books)
        }

        // Search Filtering
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBooks(newText ?: "")
                return true
            }
        })
    }

    private fun filterBooks(query: String) {
        val filteredList = fullBookList.filter {
            it.bookname.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true)
                        it.publish.contains(query, ignoreCase = true )
        }
        adapter.setData(filteredList) // Update adapter with filtered list
    }
}