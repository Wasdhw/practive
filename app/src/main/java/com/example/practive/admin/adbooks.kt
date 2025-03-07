package com.example.practive.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ListAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.R
import com.example.practive.database.BookViewmodel
import com.example.practive.database.MyListAdapter

class adbooks : AppCompatActivity() {


    private lateinit var adbarrow3: TextView
    private lateinit var adakawnt3: TextView
    private lateinit var mUserBookViewmodel: BookViewmodel

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adbooks)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adbookpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /* Initialize RecyclerView */
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = MyListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize ViewModel
        mUserBookViewmodel = ViewModelProvider(this).get(BookViewmodel::class.java)

        // Observe LiveData and update RecyclerView
        mUserBookViewmodel.readAllData.observe(this) { Book ->
            adapter.setData(Book)
        }


        adbarrow3 = findViewById(R.id.adBorrow3)
        adakawnt3 = findViewById(R.id.adAccount3)

        //RecycleView


        adbarrow3.setOnClickListener {
            startActivity(Intent(this, insertbooks::class.java))
        }
        adakawnt3.setOnClickListener {
            startActivity(Intent(this, adacc::class.java))
        }
    }
}