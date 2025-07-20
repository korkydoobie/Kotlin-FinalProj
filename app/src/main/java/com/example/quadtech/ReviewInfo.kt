package com.example.quadtech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quadtech.databinding.ActivityReviewInfoBinding

class ReviewInfo : AppCompatActivity() {

    private lateinit var binding: ActivityReviewInfoBinding
    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReviewInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = UserDBHelper(this)
        val bundle = intent.extras

        val firstName = bundle?.getString("firstName") ?: ""
        val middleName = bundle?.getString("middleName") ?: ""
        val lastName = bundle?.getString("lastName") ?: ""
        val email = bundle?.getString("email") ?: ""
        val password = bundle?.getString("password") ?: ""

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.tvFirstNameLabel.text = bundle?.getString("firstName")
        binding.tvMiddleNameLabel.text = bundle?.getString("middleName")
        binding.tvLastNameLabel.text = bundle?.getString("lastName")
        binding.tvEmailLabel.text = bundle?.getString("email")

        binding.btnSignUp2.setOnClickListener{
            Log.d("DEBUG", "Confirm clicked")
            Toast.makeText(this, "Trying to save...", Toast.LENGTH_SHORT).show()
            val success = dbHelper.insertUser(firstName, middleName, lastName, email, password)
            Log.d("DEBUG", "Insert success: $success")

            if (success) {
                Log.d("DEBUG", "Navigating to Homepage with username: $firstName")
                val intent = Intent(this, Homepage::class.java)
                intent.putExtra("username", firstName)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel2.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}