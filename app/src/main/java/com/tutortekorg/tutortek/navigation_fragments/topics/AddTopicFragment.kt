package com.tutortekorg.tutortek.navigation_fragments.topics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentTopicAddBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class AddTopicFragment : Fragment() {
    private lateinit var binding: FragmentTopicAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicAddBinding.inflate(inflater, container, false)
        binding.btnConfirmAddTopic.setOnClickListener { addTopic() }
        activity?.let { SystemUtils.setupConstraints(it) }
        return binding.root
    }

    private fun addTopic() {
        binding.btnConfirmAddTopic.startAnimation()
        clearErrors()
        if(validateForm()) sendTopicPostRequest()
        else binding.btnConfirmAddTopic.revertAnimation()
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if(binding.editTextTopicName.text.isNullOrBlank()) {
            binding.txtInputTopicName.error = getString(R.string.field_empty)
            isValid = false
        }

        if(binding.editTextTopicDescription.text.isNullOrBlank()) {
            binding.txtInputTopicDescription.error = getString(R.string.field_empty)
            isValid = false
        }

        return isValid
    }

    private fun clearErrors() {
        binding.txtInputTopicName.error = null
        binding.txtInputTopicDescription.error = null
    }

    private fun sendTopicPostRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics"
        val body = getJsonBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body,
            {
                try {
                    Toast.makeText(requireContext(), R.string.topic_add_success, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_topic_post, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmAddTopic.startAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun getJsonBody(): JSONObject {
        val name = binding.editTextTopicName.text.toString()
        val description = binding.editTextTopicDescription.text.toString()
        val body = JSONObject().apply {
            put("name", name)
            put("description", description)
        }
        return body
    }
}
