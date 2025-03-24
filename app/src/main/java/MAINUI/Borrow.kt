package MAINUI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.MainActivity
import com.example.practive.R
import com.example.practive.database.borrow.BorrowAdapter
import com.example.practive.database.borrow.BorrowViewModel
import com.example.practive.database.borrow.BorrowWithUser

class Borrow : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var buks2: TextView
    private lateinit var barrow2: TextView
    private lateinit var akawnt2: TextView
    private lateinit var borrowViewModel: BorrowViewModel
    private lateinit var borrowAdapter: BorrowAdapter
    private var userId: Int = -1
    private var borrowList: List<BorrowWithUser> = emptyList() // Store original data

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_borrow)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.borrowpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("USER_ID", -1)
        Log.d("BorrowActivity", "✅ Retrieved USER_ID from SharedPreferences: $userId")

        if (userId == -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Initialize UI elements
        searchView = findViewById(R.id.Search123)
        buks2 = findViewById(R.id.Books2)
        barrow2 = findViewById(R.id.Borrow2)
        akawnt2 = findViewById(R.id.Account2)

        buks2.setOnClickListener {
            startActivity(Intent(this, Books::class.java))
        }
        barrow2.setOnClickListener {
            startActivity(Intent(this, Borrow::class.java))
        }
        akawnt2.setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }

        // Initialize ViewModel and RecyclerView
        borrowViewModel = ViewModelProvider(this).get(BorrowViewModel::class.java)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView12)

        borrowAdapter = BorrowAdapter(isAdminPage = false) { borrowItem ->
            borrowViewModel.markAsReturned(borrowItem.borrowId)
            borrowItem.isReturned = true
            borrowAdapter.notifyDataSetChanged()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = borrowAdapter

        // Observe and store original data for filtering
        borrowViewModel.getUserBorrows(userId).observe(this, Observer { list ->
            Log.d("BorrowActivity", "🔄 Updating UI - Found ${list.size} borrowed books")
            borrowList = list // Store all borrowed books
            borrowAdapter.submitList(borrowList)
            borrowAdapter.notifyDataSetChanged()
        })

        // SearchView Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterBorrowList(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBorrowList(newText)
                return false
            }
        })
    }

    // 🔍 Filter books based on search input
    private fun filterBorrowList(query: String?) {
        val filteredList = if (!query.isNullOrEmpty()) {
            borrowList.filter { it.bookTitle.contains(query, ignoreCase = true) }
        } else {
            borrowList // Show all books if no query
        }
        borrowAdapter.submitList(filteredList)
        borrowAdapter.notifyDataSetChanged()
    }
}
