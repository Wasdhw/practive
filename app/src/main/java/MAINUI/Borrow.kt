package MAINUI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class Borrow : AppCompatActivity() {

    private lateinit var buks2: TextView
    private lateinit var barrow2: TextView
    private lateinit var akawnt2: TextView
    private lateinit var borrowViewModel: BorrowViewModel
    private lateinit var borrowAdapter: BorrowAdapter
    private var userId: Int = -1

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

        borrowViewModel = ViewModelProvider(this).get(BorrowViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView12)
        borrowAdapter = BorrowAdapter(isAdminPage = false) { borrowItem ->
            borrowViewModel.markAsReturned(borrowItem.borrowId)

            borrowItem.isReturned = true
            borrowAdapter.notifyDataSetChanged()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = borrowAdapter

        borrowViewModel.getUserBorrows(userId).observe(this, Observer { borrowList ->
            Log.d("BorrowActivity", "🔄 Updating UI - Found ${borrowList.size} borrowed books")
            borrowAdapter.submitList(borrowList)
            borrowAdapter.notifyDataSetChanged()
        })
    }
}