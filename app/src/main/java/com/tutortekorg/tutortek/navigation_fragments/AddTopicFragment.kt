package com.tutortekorg.tutortek.navigation_fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.authentication.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentAddTopicBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class AddTopicFragment : Fragment() {
    private lateinit var binding: FragmentAddTopicBinding
    private lateinit var request: TutortekObjectRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTopicBinding.inflate(inflater, container, false)
        binding.btnConfirmAddTopic.setOnClickListener { addTopic() }
        return binding.root
    }

    private fun addTopic() {
        binding.btnConfirmAddTopic.startAnimation()
        binding.txtInputTopicName.error = null
        if(!binding.editTextTopicName.text.isNullOrBlank()) sendTopicPostRequest()
        else {
            binding.txtInputTopicName.error = getString(R.string.field_empty)
            binding.btnConfirmAddTopic.revertAnimation()
        }
    }

    private fun sendTopicPostRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics"
        val body = getJsonBody()
        request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body,
            {
                Toast.makeText(requireContext(), R.string.topic_add_success, Toast.LENGTH_SHORT).show()
                closeKeyboard()
                activity?.onBackPressed()
            },
            { err ->
                if(JwtUtils.wasResponseUnauthorized(err))
                    activity?.let { JwtUtils.sendRefreshRequest(it, false, request) }
                else {
                    Toast.makeText(requireContext(), R.string.error_topic_post, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmAddTopic.startAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun getJsonBody(): JSONObject {
        val body = JSONObject()
        val name = binding.editTextTopicName.text.toString()
        body.put("name", name)
        return body
    }

    private fun closeKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val windowToken = activity?.currentFocus?.windowToken
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
