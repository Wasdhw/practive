package com.example.practive.admin

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.MainActivity
import com.example.practive.R
import com.example.practive.database.borrow.BorrowAdapter
import com.example.practive.database.borrow.BorrowViewModel
import com.example.practive.database.borrow.BorrowWithUser

class adacc : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var adbuks1: TextView
    private lateinit var adbarrow1: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adlogoutButton: Button
    private lateinit var borrowAdapter: BorrowAdapter
    private val borrowViewModel: BorrowViewModel by viewModels()

    private var originalBorrowList: List<BorrowWithUser> = emptyList() // 🔹 Store original list

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adacc)

        adbuks1 = findViewById(R.id.adBooks1)
        adbarrow1 = findViewById(R.id.adBorrow1)
        recyclerView = findViewById(R.id.recyclerView12)
        adlogoutButton = findViewById(R.id.adLogoutbtn)
        searchView = findViewById(R.id.searchView11)

        //  RecyclerView
        borrowAdapter = BorrowAdapter(isAdminPage = true) { borrowItem ->
            showRetrieveDialog(borrowItem)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = borrowAdapter


        loadAllBorrowTransactions()

        // SearchView Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })


        adbuks1.setOnClickListener {
            startActivity(Intent(this, adbooks::class.java))
        }
        adbarrow1.setOnClickListener {
            startActivity(Intent(this, insertbooks::class.java))
        }

        // Logout Button
        adlogoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showRetrieveDialog(borrowItem: BorrowWithUser) {
        AlertDialog.Builder(this)
            .setTitle("Retrieve Book")
            .setMessage("Do you want to retrieve ${borrowItem.bookTitle}?")
            .setPositiveButton("Retrieve") { _, _ ->
                borrowViewModel.markAsReturned(borrowItem.borrowId)

                // ✅ Update item in the list
                val updatedList = borrowAdapter.currentList.map {
                    if (it.borrowId == borrowItem.borrowId) it.copy(isReturned = true) else it
                }
                borrowAdapter.submitList(updatedList)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ -> logout() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadAllBorrowTransactions() {
        borrowViewModel.getAllBorrowsWithUsers().observe(this) { borrowList ->
            originalBorrowList = borrowList //
            borrowAdapter.submitList(borrowList)
        }
    }

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            borrowAdapter.submitList(originalBorrowList)
        } else {
            val filteredList = originalBorrowList.filter {
                it.bookTitle.contains(query, ignoreCase = true) ||
                        it.username.contains(query, ignoreCase = true)
            }
            borrowAdapter.submitList(filteredList)
        }
    }
}
