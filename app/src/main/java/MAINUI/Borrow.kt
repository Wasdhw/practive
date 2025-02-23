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

class Borrow : AppCompatActivity() {

    private lateinit var buks2: TextView
    private lateinit var barrow2: TextView
    private lateinit var akawnt2: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_borrow)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.borrowpage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buks2 = findViewById(R.id.Books2)
        barrow2 = findViewById(R.id.Borrow2)
        akawnt2 = findViewById(R.id.Account2)

        buks2.setOnClickListener {
            startActivity(Intent(this, Books::class.java))
        }
        barrow2.setOnClickListener {
            startActivity(Intent(this, Borrow::class.java))
        }
        akawnt2.setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }


    }
}