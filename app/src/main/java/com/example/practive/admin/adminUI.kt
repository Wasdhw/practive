package com.example.practive.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practive.R

class adminUI : AppCompatActivity() {

    private lateinit var adbuks: TextView
    private lateinit var adbarrow: TextView
    private lateinit var adakawnt: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_ui)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminfirst)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adbuks = findViewById(R.id.adBooks)
        adbarrow = findViewById(R.id.adBorrow)
        adakawnt = findViewById(R.id.adAccount)

        adbuks.setOnClickListener {
            startActivity(Intent(this, adbooks::class.java))
        }
        adbarrow.setOnClickListener {
            startActivity(Intent(this, insertbooks::class.java))
        }
        adakawnt.setOnClickListener {
            startActivity(Intent(this, adacc::class.java))
        }

    }
}