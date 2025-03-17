package com.example.practive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practive.database.user.User
import com.example.practive.database.UserDatabase
import com.example.practive.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    private lateinit var boton: TextView
    private lateinit var mbinding: ActivityRegisterBinding
    private lateinit var userDatabase: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        enableEdgeToEdge()
        setContentView(mbinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userDatabase = UserDatabase.getDatabase(this)

        boton = findViewById(R.id.signin)
        boton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        mbinding.registerbutton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        if (validateFullName() && validateUser() && validatePassword() && validateConfirmPass() && validatePass()) {
            CoroutineScope(Dispatchers.IO).launch {
                val existingUser = userDatabase.userDao().getUserByUsername(mbinding.usernem.text.toString())
                if (existingUser != null) {
                    runOnUiThread {
                        mbinding.usernem.error = "Username already exists"
                    }
                } else {
                    val user = User(
                        fullName = mbinding.name.text.toString(),
                        username = mbinding.usernem.text.toString(),
                        password = mbinding.pass.text.toString()
                    )
                    userDatabase.userDao().insertUser(user)
                    runOnUiThread {
                        startActivity(Intent(this@Register, MainActivity::class.java))
                        finish()
                    }

                }
            }
        }
    }

    private fun validateFullName(): Boolean {
        val value: String = mbinding.name.text.toString()
        return if (value.isEmpty()) {
            mbinding.name.error = "Full Name is Required"
            false
        } else {
            true
        }
    }

    private fun validateUser(): Boolean {
        val value: String = mbinding.usernem.text.toString()
        return if (value.isEmpty()) {
            mbinding.usernem.error = "Username is Required"
            false
        } else {
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value: String = mbinding.pass.text.toString()
        return if (value.isEmpty()) {
            mbinding.pass.error = "Password is Required"
            false
        } else if (value.length < 6) {
            mbinding.pass.error = "Password must be at least 6 characters long"
            false
        } else {
            true
        }
    }

    private fun validateConfirmPass(): Boolean {
        val value: String = mbinding.confirm.text.toString()
        return if (value.isEmpty()) {
            mbinding.confirm.error = "Confirm Password is Required"
            false
        } else if (value.length < 6) {
            mbinding.confirm.error = "Confirm Password must be at least 6 characters long"
            false
        } else {
            true
        }
    }

    private fun validatePass(): Boolean {
        val pass: String = mbinding.pass.text.toString()
        val conf: String = mbinding.confirm.text.toString()
        return if (pass != conf) {
            mbinding.confirm.error = "Passwords Don't Match"
            false
        } else {
            true
        }
    }
}
