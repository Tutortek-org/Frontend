package com.tutortekorg.tutortek.navigation_fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentDeleteAccountBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class DeleteAccountFragment : Fragment() {
    private lateinit var binding: FragmentDeleteAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        binding.btnDeleteAccount.setOnClickListener { confirmDelete() }
        activity?.let { SystemUtils.changeBackgroundColorToPrimary(it) }
        return binding.root
    }

    private fun confirmDelete() {
        binding.btnDeleteAccount.startAnimation()
        val email = JwtUtils.getEmailFromSavedToken(requireContext())
        val body = email?.let { formLoginRequestBody(it) }
        val url = "${TutortekConstants.BASE_URL}/login"
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            {
                JwtUtils.saveJwtToken(requireContext(), it)
                deleteAccount()
            },
            {
                Toast.makeText(requireContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show()
                binding.btnDeleteAccount.revertAnimation()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun deleteAccount() {
        val url = "${TutortekConstants.BASE_URL}/delete"
        val request = TutortekObjectRequest(requireContext(), Request.Method.DELETE, url, null,
            {
                finishDeleting()
            },
            {
                Toast.makeText(requireContext(), R.string.error_account_delete, Toast.LENGTH_SHORT).show()
                binding.btnDeleteAccount.revertAnimation()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun finishDeleting() {
        Toast.makeText(requireContext(), R.string.account_deleted, Toast.LENGTH_SHORT).show()
        JwtUtils.invalidateJwtToken(requireContext())
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }

    private fun formLoginRequestBody(email: String): JSONObject {
        val password = binding.editTextPasswordDelete.text.toString()
        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        return body
    }
}
