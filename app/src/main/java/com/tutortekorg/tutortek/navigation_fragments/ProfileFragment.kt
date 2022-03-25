package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.FragmentProfileBinding
import com.tutortekorg.tutortek.utils.SystemUtils

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.btnEditProfile.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_editMenuFragment)
        }
        binding.btnDeleteAccount.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_deleteAccountFragment)
        }
        binding.refreshProfile.setOnRefreshListener {
            val userProfile = ProfileSingleton.getInstance().userProfile
            fillOutUI(userProfile)
            binding.refreshProfile.isRefreshing = false
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val userProfile = ProfileSingleton.getInstance().userProfile
        fillOutUI(userProfile)
    }

    private fun fillOutUI(userProfile: UserProfile?) {
        binding.txtProfileName.text = getString(R.string.profile_full_name, userProfile?.firstName, userProfile?.lastName)
        binding.txtProfileExtra.text = getString(R.string.profile_birth_date, userProfile?.birthDate)
        binding.txtProfileRating.text = getString(R.string.rating_format, userProfile?.rating)
        binding.txtProfileCourseCount.text = userProfile?.topicCount.toString()
        binding.txtProfileDescription.text = userProfile?.description
        binding.txtProfileRole.text = userProfile?.roles?.let { SystemUtils.getRoleNamesForUI(it, this) }
        val picasso = Picasso.get()
        picasso.invalidate("file://${userProfile?.photoPath}")
        picasso.load("file://${userProfile?.photoPath}")
            .placeholder(R.drawable.ic_launcher_foreground)
            .fit()
            .into(binding.imgProfilePicture)
    }
}
