package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val userProfile = ProfileSingleton.getInstance().userProfile
        fillOutUI(userProfile)
        return binding.root
    }

    private fun fillOutUI(userProfile: UserProfile?) {
        binding.txtProfileName.text = getString(R.string.profile_full_name, userProfile?.firstName, userProfile?.lastName)
        binding.txtProfileExtra.text = getString(R.string.profile_birth_date, userProfile?.birthDate)
        binding.txtProfileRating.text = userProfile?.rating.toString()
    }
}
