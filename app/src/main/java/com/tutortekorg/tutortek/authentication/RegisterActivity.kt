package com.tutortekorg.tutortek.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.*
import com.tutortekorg.tutortek.constants.ErrorSlug
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityRegisterBinding
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var role = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { onRegisterClick() }
        binding.txtAlreadyHaveAnAccount.setOnClickListener { onBackClick() }
        binding.imgRegisterBack.setOnClickListener { onBackClick() }
        binding.radioTutor.setOnClickListener { role = 1 }
        binding.radioStudent.setOnClickListener { role = 2 }
    }

    private fun onRegisterClick() {
        binding.btnRegister.startAnimation()
        clearPreviousErrors()
        if(validateForm()) sendRegisterRequest()
        else binding.btnRegister.revertAnimation()
    }

    private fun sendRegisterRequest() {
        val url = "${TutortekConstants.BASE_URL}/register"
        val body = formUserCreateRequestBody()
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                sendLoginRequest(body)
            },
            {
                binding.btnRegister.revertAnimation()
                Toast.makeText(this, ErrorSlug.REGISTER_ERROR, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun clearPreviousErrors() {
        binding.txtInputName.error = null
        binding.txtInputSurname.error = null
        binding.txtInputPasswordRegister.error = null
        binding.txtInputEmailRegister.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextName.text.isNullOrBlank()) {
            binding.txtInputName.error = ErrorSlug.FIELD_EMPTY
            result = false
        }
        if(binding.editTextSurname.text.isNullOrBlank()) {
            binding.txtInputSurname.error = ErrorSlug.FIELD_EMPTY
            result = false
        }
        if(binding.editTextPasswordRegister.text?.length!! < 8) {
            binding.txtInputPasswordRegister.error = ErrorSlug.PASSWORD_TOO_SHORT
            result = false
        }
        if(!isEmailValid(binding.editTextEmailRegister.text.toString())) {
            binding.txtInputEmailRegister.error = ErrorSlug.INVALID_EMAIL
            result = false
        }

        return result
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun formUserCreateRequestBody(): JSONObject {
        val email = binding.editTextEmailRegister.text.toString()
        val password = binding.editTextPasswordRegister.text.toString()
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        body.put("role", role)
        return body
    }

    private fun sendLoginRequest(body: JSONObject) {
        val url = "${TutortekConstants.BASE_URL}/login"
        body.remove("role")
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                TutortekUtils.saveJwtToken(this, it)
                sendCreateProfileRequest()
            },
            {
                binding.btnRegister.revertAnimation()
                Toast.makeText(this, ErrorSlug.LOGIN_ERROR, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun sendCreateProfileRequest() {
        val url = "${TutortekConstants.BASE_URL}/profiles"
        val token = TutortekUtils.getJwtToken(this)
        val body = formProfileCreateRequestBody()
        val request = object : JsonObjectRequest(Request.Method.POST, url, body,
            {
                navigateToHomeScreen()
            },
            {
                binding.btnRegister.revertAnimation()
                Toast.makeText(this, ErrorSlug.PROFILE_CREATE_ERROR, Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun formProfileCreateRequestBody(): JSONObject {
        val body = JSONObject()
        val firstName = binding.editTextName.text.toString()
        val lastName = binding.editTextSurname.text.toString()
        body.put("firstName", firstName)
        body.put("lastName", lastName)
        body.put("rating", 0.0)
        body.put("birthDate", "2000-02-06") // TODO: Change this to a non-hardcoded value
        return body
    }

    private fun navigateToHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        finish()
    }

    private fun onBackClick() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }

    override fun onBackPressed() = onBackClick()
}
