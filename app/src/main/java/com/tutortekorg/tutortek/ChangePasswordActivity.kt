package com.tutortekorg.tutortek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityChangePasswordBinding
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSavePassword.setOnClickListener { changePassword() }
        binding.editTextNewPassword.addTextChangedListener { binding.txtInputNewPassword.error = null }
    }

    private fun changePassword() {
        binding.btnSavePassword.startAnimation()
        binding.txtInputNewPassword.error = null
        if(binding.editTextNewPassword.text?.length!! >= 8) sendLoginRequest(true)
        else {
            binding.txtInputNewPassword.error = getString(R.string.password_too_short)
            binding.btnSavePassword.revertAnimation()
        }
    }

    private fun sendLoginRequest(shouldSendChangeRequest: Boolean) {
        val url = "${TutortekConstants.BASE_URL}/login"
        val body = formLoginBody(shouldSendChangeRequest)
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                JwtUtils.saveJwtToken(this, it)
                if(shouldSendChangeRequest) sendChangeRequest()
                else onBackPressed()
            },
            {
                if(!shouldSendChangeRequest) navigateToLoginScreen()
                else {
                    Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show()
                    binding.btnSavePassword.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun sendChangeRequest() {
        val url = "${TutortekConstants.BASE_URL}/password"
        val body = formChangePasswordBody()
        val request = TutortekRequest(this, Request.Method.PUT, url, body,
            {
                Toast.makeText(this, R.string.password_change_success, Toast.LENGTH_SHORT).show()
                sendLoginRequest(false)
            },
            {
                Toast.makeText(this, R.string.error_change_password, Toast.LENGTH_SHORT).show()
                binding.btnSavePassword.revertAnimation()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun formChangePasswordBody(): JSONObject {
        val password = binding.editTextNewPassword.text.toString()
        val body = JSONObject()
        body.put("password", password)
        return body
    }

    private fun formLoginBody(shouldSendChangeRequest: Boolean): JSONObject {
        val email = JwtUtils.getEmailFromSavedToken(this)
        var password = binding.editTextCurrentPassword.text.toString()
        if(!shouldSendChangeRequest) password = binding.editTextNewPassword.text.toString()
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        return body
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
