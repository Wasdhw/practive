package com.example.practive

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.practive.admin.Admin
import com.example.practive.database.user.UserDao
import com.example.practive.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView
    private lateinit var adminIcon: ImageView
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

        // Initialize UI elements
        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.passcode)
        loginButton = findViewById(R.id.button)
        registerText = findViewById(R.id.Regis)
        adminIcon = findViewById(R.id.admin)

        registerText.paintFlags = registerText.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Initialize database
        val userDatabase = UserDatabase.getDatabase(this)
        userDao = userDatabase.userDao()

        // Click Listeners
        registerText.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        loginButton.setOnClickListener {
            loginUser()
        }

        adminIcon.setOnClickListener {
            startActivity(Intent(this, Admin::class.java))
        }

        // Fetch and log user data
        fetchAllUsers()
    }

    // Fetch all users from the database
    private fun fetchAllUsers() {
        userDao.getAllData().observe(this) { userList ->
            userList?.forEach { user ->
                Log.d("UserData", "Name: ${user.fullName}, Username: ${user.username}, Password: ${user.password}")
            }
        }
    }

    // Handle user login
    private fun loginUser() {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByUsername(username)

            withContext(Dispatchers.Main) {
                if (!isFinishing && !isDestroyed) {
                    if (user == null) {
                        Toast.makeText(this@MainActivity, "User does not exist!", Toast.LENGTH_SHORT).show()
                    } else if (user.password == password) {
                        // ✅ Save user ID in SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putInt("USER_ID", user.id)
                            apply()  // ✅ Use `apply()` for better performance
                        }
                        Log.d("LoginActivity", "✅ Saved USER_ID to SharedPreferences: ${user.id}")

                        // ✅ Redirect to Login activity
                        val intent = Intent(this@MainActivity, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Incorrect password!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
