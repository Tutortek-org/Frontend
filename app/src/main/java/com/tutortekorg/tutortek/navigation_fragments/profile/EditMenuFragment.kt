package com.tutortekorg.tutortek.navigation_fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentEditMenuBinding

class EditMenuFragment : Fragment() {
    private lateinit var binding: FragmentEditMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditMenuBinding.inflate(inflater, container, false)
        binding.btnChangeProfileData.setOnClickListener {
            it.findNavController().navigate(R.id.action_editMenuFragment_to_profileEditFragment)
        }
        binding.btnChangePassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_editMenuFragment_to_changePasswordFragment)
        }
        binding.btnProfilePhoto.setOnClickListener {
            it.findNavController().navigate(R.id.action_editMenuFragment_to_profilePhotoFragment)
        }
        return binding.root
    }
}
