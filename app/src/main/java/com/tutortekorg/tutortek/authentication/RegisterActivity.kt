package com.tutortekorg.tutortek.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.*
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityRegisterBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

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

        binding.editTextBirthdate.keyListener = null
        binding.editTextBirthdate.setOnClickListener { onBirthDateClick() }

        binding.editTextPasswordRegister.addTextChangedListener { binding.txtInputPasswordRegister.error = null }
    }

    private fun onBirthDateClick() {
        val datePickerFragment = DatePickerFragment(binding.editTextBirthdate)
        datePickerFragment.show(supportFragmentManager, "datePicker")
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
                Toast.makeText(this, R.string.error_register, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun clearPreviousErrors() {
        binding.txtInputName.error = null
        binding.txtInputSurname.error = null
        binding.txtInputBirthdate.error = null
        binding.txtInputPasswordRegister.error = null
        binding.txtInputEmailRegister.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextName.text.isNullOrBlank()) {
            binding.txtInputName.error = getString(R.string.field_empty)
            result = false
        }
        if(binding.editTextSurname.text.isNullOrBlank()) {
            binding.txtInputSurname.error = getString(R.string.field_empty)
            result = false
        }
        if(binding.editTextBirthdate.text.isNullOrBlank()
            || !isDateValid(binding.editTextBirthdate.text.toString())) {
            binding.txtInputBirthdate.error = getString(R.string.error_invalid_birthdate)
            result = false
        }
        if(binding.editTextPasswordRegister.text?.length!! < 8) {
            binding.txtInputPasswordRegister.error = getString(R.string.password_too_short)
            result = false
        }
        if(!isEmailValid(binding.editTextEmailRegister.text.toString())) {
            binding.txtInputEmailRegister.error = getString(R.string.invalid_email)
            result = false
        }

        return result
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isDateValid(date: String): Boolean {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = parser.parse(date)
        return parsedDate!!.before(Date())
    }

    private fun formUserCreateRequestBody(): JSONObject {
        val email = binding.editTextEmailRegister.text.toString()
        val password = binding.editTextPasswordRegister.text.toString()
        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("role", role)
        }
        return body
    }

    private fun sendLoginRequest(body: JSONObject) {
        val url = "${TutortekConstants.BASE_URL}/login"
        body.remove("role")
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                JwtUtils.saveJwtToken(this, it)
                sendCreateProfileRequest()
            },
            {
                binding.btnRegister.revertAnimation()
                Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun sendCreateProfileRequest() {
        val url = "${TutortekConstants.BASE_URL}/profiles"
        val body = formProfileCreateRequestBody()
        val request = TutortekObjectRequest(this, Request.Method.POST, url, body,
            {
                Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
                onBackClick()
            },
            {
                binding.btnRegister.revertAnimation()
                Toast.makeText(this, R.string.error_profile_create, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun formProfileCreateRequestBody(): JSONObject {
        val firstName = binding.editTextName.text.toString()
        val lastName = binding.editTextSurname.text.toString()
        val birthDate = binding.editTextBirthdate.text.toString()
        val body = JSONObject().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("birthDate", birthDate)
        }
        return body
    }

    private fun onBackClick() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }

    override fun onBackPressed() = onBackClick()
}
