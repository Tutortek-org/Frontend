package com.tutortekorg.tutortek.navigation_fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentEditMenuBinding
import com.tutortekorg.tutortek.profile_editing.ChangePasswordActivity

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
        binding.btnChangePassword.setOnClickListener { onChangePasswordClick() }
        return binding.root
    }

    private fun onChangePasswordClick() {
        val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
        startActivity(intent)
    }
}
