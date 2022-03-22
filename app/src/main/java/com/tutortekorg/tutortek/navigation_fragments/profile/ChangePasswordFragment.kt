package com.tutortekorg.tutortek.navigation_fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentChangePasswordBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import org.json.JSONObject
import java.lang.Exception

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
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
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            {
                try {
                    JwtUtils.saveJwtToken(requireContext(), it)
                    if(shouldSendChangeRequest) sendChangeRequest()
                    else findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!shouldSendChangeRequest) navigateToLoginScreen()
                else {
                    Toast.makeText(requireContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show()
                    binding.btnSavePassword.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun sendChangeRequest() {
        val url = "${TutortekConstants.BASE_URL}/password"
        val body = formChangePasswordBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.PUT, url, body,
            {
                Toast.makeText(requireContext(), R.string.password_change_success, Toast.LENGTH_SHORT).show()
                sendLoginRequest(false)
            },
            {
                Toast.makeText(requireContext(), R.string.error_change_password, Toast.LENGTH_SHORT).show()
                binding.btnSavePassword.revertAnimation()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formChangePasswordBody(): JSONObject {
        val password = binding.editTextNewPassword.text.toString()
        val body = JSONObject().apply {
            put("password", password)
        }
        return body
    }

    private fun formLoginBody(shouldSendChangeRequest: Boolean): JSONObject {
        val email = JwtUtils.getEmailFromSavedToken(requireContext())
        var password = binding.editTextCurrentPassword.text.toString()
        if(!shouldSendChangeRequest) password = binding.editTextNewPassword.text.toString()
        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        return body
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }
}
