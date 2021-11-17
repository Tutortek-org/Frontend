package com.tutortekorg.tutortek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.auth0.android.jwt.JWT
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
        val token = TutortekUtils.getJwtToken(this)
        val email = token?.let { getEmail(it) }
        val body = email?.let { formLoginRequestBody(it) }
        val url = "${TutortekConstants.BASE_URL}/login"
        val request = JsonObjectRequest(Request.Method.POST, url, body,
            {
                TutortekUtils.saveJwtToken(this, it)
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
        val token = TutortekUtils.getJwtToken(this)
        val url = "${TutortekConstants.BASE_URL}/delete"
        val request = object : JsonObjectRequest(Method.DELETE, url, null,
            {
                finishDeleting()
            },
            {
                if(it.networkResponse == null || it.networkResponse.statusCode == 401)
                    finishDeleting()
                else {
                    Toast.makeText(this, R.string.error_account_delete, Toast.LENGTH_SHORT).show()
                    binding.btnDeleteAccount.revertAnimation()
                }
            }
        ){
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

    private fun finishDeleting() {
        Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_SHORT).show()
        TutortekUtils.invalidateJwtToken(this)
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun getEmail(token: String): String? {
        val jwt = JWT(token)
        val profileId = jwt.getClaim("sub")
        return profileId.asString()
    }

    private fun formLoginRequestBody(email: String): JSONObject {
        val password = binding.editTextPasswordDelete.text.toString()
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        return body
    }
}
