package com.tutortekorg.tutortek.navigation_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.ProfileEditActivity
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
        binding.btnEditProfile.setOnClickListener { openEditForm() }
        val userProfile = ProfileSingleton.getInstance().userProfile
        fillOutUI(userProfile)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val userProfile = ProfileSingleton.getInstance().userProfile
        fillOutUI(userProfile)
    }

    private fun openEditForm() {
        val intent = Intent(activity, ProfileEditActivity::class.java)
        startActivity(intent)
    }

    private fun fillOutUI(userProfile: UserProfile?) {
        binding.txtProfileName.text = getString(R.string.profile_full_name, userProfile?.firstName, userProfile?.lastName)
        binding.txtProfileExtra.text = getString(R.string.profile_birth_date, userProfile?.birthDate)
        binding.txtProfileRating.text = userProfile?.rating.toString()
        binding.txtProfileCourseCount.text = userProfile?.topicCount.toString()
        binding.txtProfileDescription.text = userProfile?.description
        binding.txtProfileRole.text = userProfile?.roles?.let { getRoleNamesForUI(it) }
    }

    private fun getRoleNamesForUI(roles: List<String>): String {
        var result = ""
        for(role in roles) {
            result += when(role) {
                "TUTOR" -> "${getString(R.string.radio_text_tutor)}, "
                "STUDENT" -> "${getString(R.string.radio_text_student)}, "
                else -> "${getString(R.string.role_admin)}, "
            }
        }
        return result.dropLast(2)
    }
}
