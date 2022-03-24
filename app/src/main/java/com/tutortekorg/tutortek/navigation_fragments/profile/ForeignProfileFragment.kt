package com.tutortekorg.tutortek.navigation_fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.FragmentForeignProfileBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class ForeignProfileFragment : Fragment() {
    private lateinit var binding: FragmentForeignProfileBinding
    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForeignProfileBinding.inflate(inflater, container, false)
        binding.refreshForeignProfile.setOnRefreshListener { refreshData() }
        userProfile = arguments?.getSerializable("userProfile") as UserProfile
        bindDataToUI()
        return binding.root
    }

    private fun refreshData() {
        val url = "${TutortekConstants.BASE_URL}/profiles/${userProfile.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.GET, url, null,
            {
                val roles = parseRoles(it)
                userProfile = UserProfile(it, roles)
                bindDataToUI()
                binding.refreshForeignProfile.isRefreshing = false
            },
            {
                binding.refreshForeignProfile.isRefreshing = false
                if(!JwtUtils.wasResponseUnauthorized(it))
                    Toast.makeText(requireContext(), R.string.error_profile_retrieval, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun parseRoles(responseBody: JSONObject): MutableList<String> {
        val roles = mutableListOf<String>()
        val jsonRoles = responseBody.getJSONArray("roles")
        for(i in 0 until jsonRoles.length()) {
            roles.add(jsonRoles.getString(i))
        }
        return roles
    }

    private fun bindDataToUI() {
        context?.let { SystemUtils.downloadProfilePhoto(it, userProfile, binding.imgForeignProfilePicture) }
        val titles = SystemUtils.getRoleNamesForUI(userProfile.roles, this)
        binding.txtForeignProfileRole.text = titles
        binding.txtForeignProfileCourseCount.text = userProfile.topicCount.toString()
        binding.txtForeignProfileDescription.text = userProfile.description
        binding.txtForeignProfileName.text = getString(R.string.profile_full_name, userProfile.firstName, userProfile.lastName)
        binding.txtForeignProfileRating.text = userProfile.rating.toString()
        binding.txtForeignProfileExtra.text = getString(R.string.profile_birth_date, userProfile.birthDate)
    }
}
