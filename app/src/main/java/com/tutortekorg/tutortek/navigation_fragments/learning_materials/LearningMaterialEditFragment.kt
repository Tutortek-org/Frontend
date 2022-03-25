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
import com.tutortekorg.tutortek.data.LearningMaterial
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentLearningMaterialEditBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class LearningMaterialEditFragment : Fragment() {
    private lateinit var binding: FragmentLearningMaterialEditBinding
    private lateinit var meeting: Meeting
    private lateinit var topic: Topic
    private lateinit var learningMaterial: LearningMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningMaterialEditBinding.inflate(inflater, container, false)
        binding.btnConfirmEditLearningMaterial.setOnClickListener { saveEditedLearningMaterial() }
        activity?.let { SystemUtils.setupConstraints(it) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        topic = arguments?.getSerializable("topic") as Topic
        meeting = arguments?.getSerializable("meeting") as Meeting
        learningMaterial = arguments?.getSerializable("learningMaterial") as LearningMaterial

        binding.editTextLearningMaterialDescriptionEdit.setText(learningMaterial.description)
        binding.editTextLearningMaterialLinkEdit.setText(learningMaterial.link)
        binding.editTextLearningMaterialNameEdit.setText(learningMaterial.name)
    }

    private fun saveEditedLearningMaterial() {
        binding.btnConfirmEditLearningMaterial.startAnimation()
        clearPreviousErrors()
        if(validateForm()) sendLearningMaterialPutRequest()
        else binding.btnConfirmEditLearningMaterial.revertAnimation()
    }

    private fun clearPreviousErrors() {
        binding.txtInputLearningMaterialDescriptionEdit.error = null
        binding.txtInputLearningMaterialLinkEdit.error = null
        binding.txtInputLearningMaterialNameEdit.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextLearningMaterialDescriptionEdit.text.isNullOrBlank()) {
            binding.txtInputLearningMaterialDescriptionEdit.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextLearningMaterialNameEdit.text.isNullOrBlank()) {
            binding.txtInputLearningMaterialNameEdit.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextLearningMaterialLinkEdit.text.isNullOrBlank()) {
            binding.txtInputLearningMaterialLinkEdit.error = getString(R.string.field_empty)
            result = false
        }

        return result
    }

    private fun sendLearningMaterialPutRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}/materials/${learningMaterial.id}"
        val body = formEditLearningMaterialBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.PUT, url, body,
            {
                goBackToDetailsFragment()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_learning_material_edit, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmEditLearningMaterial.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formEditLearningMaterialBody(): JSONObject {
        val name = binding.editTextLearningMaterialNameEdit.text.toString()
        val link = binding.editTextLearningMaterialLinkEdit.text.toString()
        val description = binding.editTextLearningMaterialDescriptionEdit.text.toString()

        val body = JSONObject().apply {
            put("name", name)
            put("link", link)
            put("description", description)
        }

        return body
    }

    private fun goBackToDetailsFragment() {
        learningMaterial.name = binding.editTextLearningMaterialNameEdit.text.toString()
        learningMaterial.link = binding.editTextLearningMaterialLinkEdit.text.toString()
        learningMaterial.description = binding.editTextLearningMaterialDescriptionEdit.text.toString()

        try {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("learningMaterial", learningMaterial)
            navController.popBackStack()
        }
        catch (e: Exception){}
    }
}
