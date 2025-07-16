package com.example.quadtech

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quadtech.databinding.ActivitySignUpBinding
import kotlin.toString

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, Launcher::class.java)
            startActivity(intent)
        }

        binding.btnProceed.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("firstName", binding.etFirstName.text.toString())
            bundle.putString("middleName", binding.etMiddleName.text.toString())
            bundle.putString("lastName", binding.etLastName.text.toString())
            bundle.putString("email", binding.etSignUpEmail.text.toString())
            bundle.putString("password", binding.etSignUpPass.text.toString())

            if(binding.etSignUpPass.text.toString().equals(binding.etConfirmPass.text.toString())) {
                val intent = Intent(this, ReviewInfo::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}