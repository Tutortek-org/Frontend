package com.tutortekorg.tutortek.navigation_fragments.learning_materials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentLearningMaterialAddBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class LearningMaterialAddFragment : Fragment() {
    private lateinit var binding: FragmentLearningMaterialAddBinding
    private lateinit var topic: Topic
    private lateinit var meeting: Meeting

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningMaterialAddBinding.inflate(inflater, container, false)
        topic = arguments?.getSerializable("topic") as Topic
        meeting = arguments?.getSerializable("meeting") as Meeting

        binding.btnConfirmAddLearningMaterial.setOnClickListener { onAddClick() }
        activity?.let { SystemUtils.changeBackgroundColorToPrimary(it) }
        return binding.root
    }

    private fun onAddClick() {
        binding.btnConfirmAddLearningMaterial.startAnimation()
        clearPreviousErrors()
        if(validateForm()) sendLearningMaterialPostRequest()
        else binding.btnConfirmAddLearningMaterial.revertAnimation()
    }

    private fun clearPreviousErrors() {
        binding.txtInputLearningMaterialDescription.error = null
        binding.txtInputLearningMaterialLink.error = null
        binding.txtInputLearningMaterialName.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextLearningMaterialDescription.text.isNullOrBlank()) {
            binding.txtInputLearningMaterialDescription.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextLearningMaterialName.text.isNullOrBlank()) {
            binding.txtInputLearningMaterialName.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextLearningMaterialLink.text.isNullOrBlank()) {
            binding.txtInputLearningMaterialLink.error = getString(R.string.field_empty)
            result = false
        }

        return result
    }

    private fun sendLearningMaterialPostRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}/materials"
        val body = formCreateLearningMaterialBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body,
            {
                try {
                    Toast.makeText(requireContext(), R.string.learning_material_add_success, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_learning_material_post, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmAddLearningMaterial.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formCreateLearningMaterialBody(): JSONObject {
        val name = binding.editTextLearningMaterialName.text.toString()
        val link = binding.editTextLearningMaterialLink.text.toString()
        val description = binding.editTextLearningMaterialDescription.text.toString()

        val body = JSONObject().apply {
            put("name", name)
            put("link", link)
            put("description", description)
        }

        return body
    }
}
