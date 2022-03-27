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
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicEditBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject
import java.lang.Exception

class TopicEditFragment : Fragment() {
    private lateinit var binding: FragmentTopicEditBinding
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicEditBinding.inflate(inflater, container, false)
        binding.btnConfirmEditTopic.setOnClickListener { saveEditedTopic() }
        activity?.let { SystemUtils.changeBackgroundColorToPrimary(it) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        topic = arguments?.getSerializable("topic") as Topic
        binding.editTextTopicNameEdit.setText(topic.name)
        binding.editTextTopicDescriptionEdit.setText(topic.description)
    }

    private fun saveEditedTopic() {
        binding.btnConfirmEditTopic.startAnimation()
        clearErrors()
        if(validateForm()) sendTopicPutUpdate()
        else binding.btnConfirmEditTopic.revertAnimation()
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if(binding.editTextTopicNameEdit.text.isNullOrBlank()) {
            binding.txtInputTopicNameEdit.error = getString(R.string.field_empty)
            isValid = false
        }

        if(binding.editTextTopicDescriptionEdit.text.isNullOrBlank()) {
            binding.txtInputTopicDescriptionEdit.error = getString(R.string.field_empty)
            isValid = false
        }

        return isValid
    }

    private fun clearErrors() {
        binding.txtInputTopicNameEdit.error = null
        binding.txtInputTopicDescriptionEdit.error = null
    }

    private fun sendTopicPutUpdate() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}"
        val body = formRequestBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.PUT, url, body,
            {
                goBackToDetailsFragment()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_topic_edit, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmEditTopic.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formRequestBody(): JSONObject {
        val name = binding.editTextTopicNameEdit.text.toString()
        val description = binding.editTextTopicDescriptionEdit.text.toString()
        val body = JSONObject().apply {
            put("name", name)
            put("description", description)
        }
        return body
    }

    private fun goBackToDetailsFragment() {
        topic.name = binding.editTextTopicNameEdit.text.toString()
        topic.description = binding.editTextTopicDescriptionEdit.text.toString()

        try {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("topic", topic)
            navController.popBackStack()
        }
        catch (e: Exception){}
    }
}
