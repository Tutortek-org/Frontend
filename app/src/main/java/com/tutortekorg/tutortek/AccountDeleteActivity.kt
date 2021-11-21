package com.tutortekorg.tutortek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.ActivityAccountDeleteBinding
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class AccountDeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountDeleteBinding
    private lateinit var request: TutortekRequest

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
                if(JwtUtils.wasResponseUnauthorized(it))
                    JwtUtils.sendRefreshRequest(this, false, request)
                else {
                    Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show()
                    binding.btnDeleteAccount.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun deleteAccount() {
        val url = "${TutortekConstants.BASE_URL}/delete"
        request = TutortekRequest(this, Request.Method.DELETE, url, null,
            {
                finishDeleting()
            },
            {
                if(it.networkResponse == null
                    || it.networkResponse.statusCode == 401
                    || it.networkResponse.statusCode == 204)
                        finishDeleting()
                else {
                    Toast.makeText(this, R.string.error_account_delete, Toast.LENGTH_SHORT).show()
                    binding.btnDeleteAccount.revertAnimation()
                }
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
