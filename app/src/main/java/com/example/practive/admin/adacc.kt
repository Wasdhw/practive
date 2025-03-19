package com.example.practive.admin

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.MainActivity
import com.example.practive.R
import com.example.practive.database.borrow.BorrowAdapter
import com.example.practive.database.borrow.BorrowViewModel
import com.example.practive.database.borrow.BorrowWithUser

class adacc : AppCompatActivity() {

    private lateinit var adbuks1: TextView
    private lateinit var adbarrow1: TextView
    private lateinit var recyclerView: RecyclerView
    private val borrowViewModel: BorrowViewModel by viewModels()
    private lateinit var adlogoutButton: Button
    private lateinit var borrowAdapter: BorrowAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adacc)

        // Initialize UI elements
        adbuks1 = findViewById(R.id.adBooks1)
        adbarrow1 = findViewById(R.id.adBorrow1)
        recyclerView = findViewById(R.id.recyclerView12)
        adlogoutButton = findViewById(R.id.adLogoutbtn)

        // Setup RecyclerView
        borrowAdapter = BorrowAdapter(isAdminPage = true) { borrowItem ->
            showRetrieveDialog(borrowItem)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = borrowAdapter

        // Navigation Click Listeners
        adbuks1.setOnClickListener {
            startActivity(Intent(this, adbooks::class.java))
        }
        adbarrow1.setOnClickListener {
            startActivity(Intent(this, insertbooks::class.java))
        }

        // Load All Borrow Transactions (Admin View)
        loadAllBorrowTransactions()

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
                borrowAdapter.submitList(updatedList) // Refresh UI properly
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
            borrowAdapter.submitList(borrowList)
            borrowAdapter.notifyDataSetChanged() // ✅ Refresh UI
        }
    }
}
