package MAINUI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practive.R

class Account : AppCompatActivity() {

    private lateinit var buks1: TextView
    private lateinit var barrow1: TextView
    private lateinit var akawnt1: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.accountpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buks1 = findViewById(R.id.Books1)
        barrow1 = findViewById(R.id.Borrow1)
        akawnt1 = findViewById(R.id.Account1)

        buks1.setOnClickListener {
            startActivity(Intent(this, Books::class.java))
        }

        barrow1.setOnClickListener {
            startActivity(Intent(this, Borrow::class.java))
        }
        akawnt1.setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }

    }
}