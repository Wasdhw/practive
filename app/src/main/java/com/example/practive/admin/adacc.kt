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

class adacc : AppCompatActivity() {

    private lateinit var adbuks1: TextView
    private lateinit var adbarrow1: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adacc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adaccountpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adbuks1 = findViewById(R.id.adBooks1)
        adbarrow1 = findViewById(R.id.adBorrow1)


        adbuks1.setOnClickListener {
            startActivity(Intent(this, adbooks::class.java))
        }

        adbarrow1.setOnClickListener {
            startActivity(Intent(this, insertbooks::class.java))
        }

    }
}