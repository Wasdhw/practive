package com.example.practive

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var btn: Button
    private lateinit var reg: TextView
    private lateinit var userDatabase: UserDatabase
    private lateinit var userDao: UserDao
    private lateinit var admin: ImageView

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
        admin = findViewById(R.id.admin)

        reg.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        btn.setOnClickListener {
            loginUser()
        }
        admin.setOnClickListener {
            startActivity(Intent(this, Admin::class.java))
        }
        // Observe user data here
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        userDao.getAllData().observe(this) { userList ->
            userList?.forEach { user ->
                Log.d("UserData", "Name: ${user.fullName}, Username: ${user.username}, Password: ${user.password}")
            }
        }
    }



    private fun loginUser() {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDatabase.userDao().getUserByUsername(username)
            runOnUiThread {
                if (user == null ) {
                    Toast.makeText(this@MainActivity, "User does not exist!", Toast.LENGTH_SHORT).show()
                } else if (user.password == password) {
                    // ✅ Save user ID in SharedPreferences

                    val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("USER_ID", user.id)
                    editor.commit()
                    Log.d("LoginActivity", "✅ Saved USER_ID to SharedPreferences: ${user.id}")


                    val savedUserId = sharedPreferences.getInt("USER_ID", -1)
                    Log.d("LoginActivity", "✅ Confirming saved USER_ID: $savedUserId")

                    // ✅ Redirect to Login activity
                    val intent = Intent(this@MainActivity, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Close this activity to prevent going back
                } else {
                    Toast.makeText(this@MainActivity, "Incorrect password!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}