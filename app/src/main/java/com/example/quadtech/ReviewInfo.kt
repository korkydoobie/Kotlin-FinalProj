package com.example.quadtech

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quadtech.databinding.ActivityReviewInfoBinding

class ReviewInfo : AppCompatActivity() {

    private lateinit var binding: ActivityReviewInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReviewInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras
        binding.tvFirstNameLabel.text = bundle?.getString("firstName")
        binding.tvMiddleNameLabel.text = bundle?.getString("middleName")
        binding.tvLastNameLabel.text = bundle?.getString("lastName")
        binding.tvEmailLabel.text = bundle?.getString("email")

        binding.btnCancel2.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}