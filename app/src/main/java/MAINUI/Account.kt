package MAINUI

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.practive.MainActivity
import com.example.practive.R
import com.example.practive.database.UserDatabase
import com.example.practive.database.user.UserRepository
import com.example.practive.database.user.UserViewModel
import com.example.practive.database.user.UserViewModelFactory
import kotlinx.coroutines.launch

class Account : AppCompatActivity() {

    private lateinit var fullNameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var updateButton: Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var buks1: TextView
    private lateinit var barrow1: TextView
    private lateinit var akawnt1: TextView
    private lateinit var sharedPreferences: SharedPreferences  // SharedPreferences instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        fullNameTextView = findViewById(R.id.fullname)
        usernameTextView = findViewById(R.id.newuser)
        logoutButton = findViewById(R.id.Logoutbtn)
        updateButton = findViewById(R.id.UpdateButton)

        val currentPasswordEditText: EditText = findViewById(R.id.currentpass)
        val newPasswordEditText: EditText = findViewById(R.id.newpass)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmpass)

        buks1 = findViewById(R.id.Books1)
        barrow1 = findViewById(R.id.Borrow1)
        akawnt1 = findViewById(R.id.Account1)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)

        barrow1.setOnClickListener {
            startActivity(Intent(this, Borrow::class.java))
        }
        buks1.setOnClickListener {
            startActivity(Intent(this, Books::class.java))
        }

        logoutButton.paintFlags = logoutButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val userDao = UserDatabase.getDatabase(this).userDao()
        val repository = UserRepository(userDao)
        val viewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        // 🔥 Get Logged-in User ID from SharedPreferences
        val userId = sharedPreferences.getInt("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            logout()  // Force logout if no user is found
        } else {
            // Fetch User Details
            userViewModel.getUserById(userId).observe(this, Observer { user ->
                user?.let {
                    fullNameTextView.text = it.fullName
                    usernameTextView.text = it.username
                }
            })
        }

        updateButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword.length < 6) {
                    Toast.makeText(this@Account, "New password must be at least 6 characters long!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPassword == confirmPassword) {
                    lifecycleScope.launch {
                        val user = userViewModel.getUserByIdSync(userId)
                        if (user != null && user.password == currentPassword) {
                            userViewModel.updatePassword(userId, newPassword)
                            Toast.makeText(this@Account, "Password updated successfully!", Toast.LENGTH_LONG).show()
                            logout()
                        } else {
                            Toast.makeText(this@Account, "Incorrect current password!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@Account, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@Account, "All password fields must be filled!", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout Button Click Listener
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            logout()
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()  // Close the dialog without logging out
        }
        builder.create().show()
    }

    private fun logout() {
        // Clear stored user session
        sharedPreferences.edit().clear().apply()

        // Navigate back to the login screen
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
