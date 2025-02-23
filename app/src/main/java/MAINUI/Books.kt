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

class Books : AppCompatActivity() {

    private lateinit var buks3: TextView
    private lateinit var barrow3: TextView
    private lateinit var akawnt3: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_books)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bookpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buks3 = findViewById(R.id.Books3)
        barrow3 = findViewById(R.id.Borrow3)
        akawnt3 = findViewById(R.id.Account3)

        buks3.setOnClickListener {
            startActivity(Intent(this, Books::class.java))
        }
        barrow3.setOnClickListener {
            startActivity(Intent(this, Borrow::class.java))
        }
        akawnt3.setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }

    }
}