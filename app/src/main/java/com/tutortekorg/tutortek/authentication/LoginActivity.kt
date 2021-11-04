package com.tutortekorg.tutortek.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.HomeActivity
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.RequestSingleton
import com.tutortekorg.tutortek.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityLoginBinding
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { onLoginClick() }
        binding.imgNewAccount.setOnClickListener { onRegisterClick() }
        binding.txtRegister.setOnClickListener { onRegisterClick() }
    }

    private fun onLoginClick() {
        val url = "${TutortekConstants.BASE_URL}/login"
        val body = formRequestBody()
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                navigateTomHomeScreen()
            },
            {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun formRequestBody(): JSONObject {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        return body
    }

    private fun navigateTomHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        finish()
    }

    private fun onRegisterClick() {
        startActivity(Intent(this, RegisterActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        finish()
    }
}
