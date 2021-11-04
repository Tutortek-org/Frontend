package com.tutortekorg.tutortek.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutortekorg.tutortek.HomeActivity
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { onRegisterClick() }
        binding.txtAlreadyHaveAnAccount.setOnClickListener { onBackClick() }
        binding.imgRegisterBack.setOnClickListener { onBackClick() }
    }

    private fun onRegisterClick() {
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }

    private fun onBackClick() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }

    override fun onBackPressed() = onBackClick()
}
