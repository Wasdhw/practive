package com.example.practive.admin

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practive.MainActivity
import com.example.practive.R

class Admin : AppCompatActivity() {

    private lateinit var adminuser: EditText
    private lateinit var adminpass: EditText
    private lateinit var adbtn: Button
    private lateinit var b2h: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminlogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adminuser = findViewById(R.id.aduser)
        adminpass = findViewById(R.id.adpass)
        adbtn = findViewById(R.id.adbutton)
        b2h = findViewById(R.id.back2home)
        b2h.paintFlags = b2h.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val ent = Intent(this, adminUI::class.java)
        val bk2 = Intent(this,MainActivity::class.java )


        b2h.setOnClickListener{
            startActivity(bk2)
        }

        adbtn.setOnClickListener {

            if (adminuser.text.toString() == "admin" && adminpass.text.toString() == "1234") {
                startActivity(ent)
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    }
