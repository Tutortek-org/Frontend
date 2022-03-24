package com.tutortekorg.tutortek.navigation_fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.FragmentForeignProfileBinding

class ForeignProfileFragment : Fragment() {
    private lateinit var binding: FragmentForeignProfileBinding
    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForeignProfileBinding.inflate(inflater, container, false)
        bindDataToUI()
        return binding.root
    }

    private fun bindDataToUI() {
        userProfile = arguments?.getSerializable("userProfile") as UserProfile
        val titles = getRoleNamesForUI(userProfile.roles)
        binding.txtForeignProfileRole.text = titles
        binding.txtForeignProfileCourseCount.text = userProfile.topicCount.toString()
        binding.txtForeignProfileDescription.text = userProfile.description
        binding.txtForeignProfileName.text = getString(R.string.profile_full_name, userProfile.firstName, userProfile.lastName)
        binding.txtForeignProfileRating.text = userProfile.rating.toString()
        binding.txtForeignProfileExtra.text = getString(R.string.profile_birth_date, userProfile.birthDate)
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
