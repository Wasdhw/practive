package MAINUI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.R
import com.example.practive.database.MyListAdapter
import com.example.practive.database.book.BookViewmodel
import com.example.practive.database.borrow.BorrowBookActivity
import com.example.practive.database.borrow.BorrowViewModel
import com.example.practive.database.book.Book

class Books : AppCompatActivity() {

    private lateinit var adapter1: MyListAdapter
    private lateinit var searchView1: SearchView
    private lateinit var recyclerView1: RecyclerView
    private lateinit var bookViewModel1: BookViewmodel
    private lateinit var borrowViewModel: BorrowViewModel
    private lateinit var buks3: TextView
    private lateinit var barrow3: TextView
    private lateinit var akawnt3: TextView
    private var fullBookList1 = mutableListOf<Book>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_books)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchView1 = findViewById(R.id.searchView1)
        recyclerView1 = findViewById(R.id.recyclerView1)

        buks3 = findViewById(R.id.Books3)
        barrow3 = findViewById(R.id.Borrow3)
        akawnt3 = findViewById(R.id.Account3)

        barrow3.setOnClickListener {
            startActivity(Intent(this, Borrow::class.java))
        }
        akawnt3.setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }

        // Initialize RecyclerView
        recyclerView1.layoutManager = LinearLayoutManager(this)
        adapter1 = MyListAdapter { selectedBook ->
            Log.d("Books", "Book selected: ${selectedBook.bookname}") // Debug Log
            openBorrowBookActivity(selectedBook)
        }
        recyclerView1.adapter = adapter1

        // Initialize ViewModels
        bookViewModel1 = ViewModelProvider(this).get(BookViewmodel::class.java)
        borrowViewModel = ViewModelProvider(this).get(BorrowViewModel::class.java)

        // Observe book data
        bookViewModel1.readAllData.observe(this) { books ->
            fullBookList1.clear()
            fullBookList1.addAll(books)
            adapter1.setData(books)
        }

        // Search Filtering
        searchView1.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBooks(newText ?: "")
                return true
            }
        })
    }

    private fun openBorrowBookActivity(book: Book) {
        val intent = Intent(this, BorrowBookActivity::class.java).apply {
            putExtra("BOOK_ID", book.bookId)
            putExtra("BOOK_TITLE", book.bookname)
            putExtra("BOOK_AUTHOR", book.author)
            putExtra("BOOK_PUBLISH", book.publish)

            // Pass the image as a ByteArray
            book.photo?.let {
                putExtra("BOOK_IMAGE", it)
            }
        }
        startActivity(intent)
    }

    private fun filterBooks(query: String) {
        val filteredList = fullBookList1.filter {
            it.bookname.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true)
        }
        adapter1.setData(filteredList)
    }
}
