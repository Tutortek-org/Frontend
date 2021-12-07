package com.tutortekorg.tutortek

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.data.LearningMaterial
import com.tutortekorg.tutortek.databinding.FragmentLearningMaterialsDetailsBinding

class LearningMaterialsDetailsFragment : Fragment() {
    private lateinit var binding: FragmentLearningMaterialsDetailsBinding
    private lateinit var learningMaterial: LearningMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningMaterialsDetailsBinding.inflate(inflater, container, false)
        bindDataToUI()
        return binding.root
    }

    private fun bindDataToUI() {
        learningMaterial = arguments?.getSerializable("learningMaterial") as LearningMaterial
        binding.txtLearningMaterialDescription.text = learningMaterial.description
        binding.txtLearningMaterialLink.text = learningMaterial.link
        binding.txtLearningMaterialName.text = learningMaterial.name
    }
}
