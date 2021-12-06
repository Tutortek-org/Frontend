package com.tutortekorg.tutortek.profile_editing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityAccountDeleteBinding
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class AccountDeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountDeleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDeleteAccount.setOnClickListener { confirmDelete() }
    }

    private fun confirmDelete() {
        binding.btnDeleteAccount.startAnimation()
        val email = JwtUtils.getEmailFromSavedToken(this)
        val body = email?.let { formLoginRequestBody(it) }
        val url = "${TutortekConstants.BASE_URL}/login"
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                JwtUtils.saveJwtToken(this, it)
                deleteAccount()
            },
            {
                Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show()
                binding.btnDeleteAccount.revertAnimation()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun deleteAccount() {
        val url = "${TutortekConstants.BASE_URL}/delete"
        val request = TutortekObjectRequest(this, Request.Method.DELETE, url, null,
            {
                finishDeleting()
            },
            {
                Toast.makeText(this, R.string.error_account_delete, Toast.LENGTH_SHORT).show()
                binding.btnDeleteAccount.revertAnimation()
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun finishDeleting() {
        Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_SHORT).show()
        JwtUtils.invalidateJwtToken(this)
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun formLoginRequestBody(email: String): JSONObject {
        val password = binding.editTextPasswordDelete.text.toString()
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        return body
    }
}
