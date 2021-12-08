package com.tutortekorg.tutortek

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.LearningMaterial
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentLearningMaterialsDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.SystemUtils

class LearningMaterialsDetailsFragment : Fragment() {
    private lateinit var binding: FragmentLearningMaterialsDetailsBinding
    private lateinit var learningMaterial: LearningMaterial
    private lateinit var topic: Topic
    private lateinit var meeting: Meeting

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningMaterialsDetailsBinding.inflate(inflater, container, false)

        bindDataToUI()
        bindEventsToButtons()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry
            ?.savedStateHandle?.getLiveData<LearningMaterial>("learningMaterial")
            ?.observe(viewLifecycleOwner) {
                learningMaterial = it
                binding.txtLearningMaterialDescription.text = learningMaterial.description
                binding.txtLearningMaterialLink.text = learningMaterial.link
                binding.txtLearningMaterialName.text = learningMaterial.name
            }
    }

    private fun bindDataToUI() {
        learningMaterial = arguments?.getSerializable("learningMaterial") as LearningMaterial
        topic = arguments?.getSerializable("topic") as Topic
        meeting = arguments?.getSerializable("meeting") as Meeting

        binding.txtLearningMaterialDescription.text = learningMaterial.description
        binding.txtLearningMaterialLink.text = learningMaterial.link
        binding.txtLearningMaterialName.text = learningMaterial.name
    }

    private fun bindEventsToButtons() {
        binding.btnDeleteLearningMaterial.setOnClickListener {
            SystemUtils.showConfirmDeleteDialog(
                requireContext(),
                R.string.learning_material_delete_question,
                learningMaterial.name,
                ::sendLearningMaterialDeleteRequest
            )
        }
        binding.btnEditLearningMaterial.setOnClickListener {
            val bundle = bundleOf("meeting" to meeting, "topic" to topic, "learningMaterial" to learningMaterial)
            it.findNavController()
                .navigate(R.id.action_learningMaterialsDetailsFragment_to_learningMaterialEditFragment, bundle)
        }
    }

    private fun sendLearningMaterialDeleteRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}/materials/${learningMaterial.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.DELETE, url, null,
            {
                activity?.onBackPressed()
            },
            {
                revertButtonAnimations()
                Toast.makeText(requireContext(), R.string.error_learning_material_delete, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun startButtonAnimations() {
        binding.btnDeleteLearningMaterial.startAnimation()
        binding.btnEditLearningMaterial.startAnimation()
    }

    private fun revertButtonAnimations() {
        binding.btnEditLearningMaterial.revertAnimation()
        binding.btnDeleteLearningMaterial.revertAnimation()
    }
}
