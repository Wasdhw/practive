package com.example.practive

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practive.database.UserDao
import com.example.practive.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var btn: Button
    private lateinit var reg: TextView
    private lateinit var userDatabase: UserDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userDatabase = UserDatabase.getDatabase(this)
        userDao = userDatabase.userDao()

        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.passcode)
        btn = findViewById(R.id.button)
        reg = findViewById(R.id.Regis)

        reg.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        btn.setOnClickListener {
            loginUser()
        }
    }


    private fun loginUser() {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val user = userDatabase.userDao().getUserByUsername(username)
            runOnUiThread {
                if (user == null) {
                    Toast.makeText(this@MainActivity, "User does not exist!", Toast.LENGTH_SHORT).show()
                } else if (user.password == password) {
                    startActivity(Intent(this@MainActivity, Login::class.java))
                } else {
                    Toast.makeText(this@MainActivity, "Incorrect password!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}