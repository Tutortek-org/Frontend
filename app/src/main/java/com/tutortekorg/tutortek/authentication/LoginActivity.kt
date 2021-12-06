package com.tutortekorg.tutortek.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.*
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityLoginBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
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
        binding.btnLogin.startAnimation()
        val url = "${TutortekConstants.BASE_URL}/login"
        val body = formRequestBody()
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                JwtUtils.saveJwtToken(this, it)
                sendProfileGetRequest()
            },
            {
                Toast.makeText(this, R.string.incorrect_credentials, Toast.LENGTH_SHORT).show()
                binding.btnLogin.revertAnimation()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun sendProfileGetRequest() {
        val profileId = JwtUtils.getProfileIdFromSavedToken(this)
        val token = JwtUtils.getJwtToken(this)
        val url = "${TutortekConstants.BASE_URL}/profiles/$profileId"
        val request = TutortekObjectRequest(this, Request.Method.GET, url, null,
            {
                if (token != null)
                    JwtUtils.addUserProfileBundle(it, token)
                navigateToHomeScreen()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)){
                    Toast.makeText(this, R.string.error_profile_retrieval, Toast.LENGTH_SHORT).show()
                    binding.btnLogin.revertAnimation()
                }
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

    private fun navigateToHomeScreen() {
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
