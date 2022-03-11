package com.tutortekorg.tutortek.navigation_fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.DatePickerFragment
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.FragmentProfileEditBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import org.json.JSONObject

class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        displayCurrentInformation()
        binding.btnEditSave.setOnClickListener { saveNewInformation() }
        binding.profileEditTextBirthdate.setOnClickListener { onBirthDateClick() }
        binding.profileEditTextBirthdate.keyListener = null
        return binding.root
    }

    private fun displayCurrentInformation() {
        val userProfile = ProfileSingleton.getInstance().userProfile
        binding.profileEditTextName.setText(userProfile?.firstName)
        binding.profileEditTextSurname.setText(userProfile?.lastName)
        binding.profileEditTextBirthdate.setText(userProfile?.birthDate)
        binding.profileEditTextDescription.setText(userProfile?.description)
    }

    private fun onBirthDateClick() {
        val datePickerFragment = DatePickerFragment(binding.profileEditTextBirthdate)
        activity?.supportFragmentManager?.let { datePickerFragment.show(it, "datePicker") }
    }

    private fun saveNewInformation() {
        binding.btnEditSave.startAnimation()
        clearPreviousErrors()
        if(validateForm()) sendEditProfileRequest()
        else binding.btnEditSave.revertAnimation()
    }

    private fun clearPreviousErrors() {
        binding.profileTxtInputBirthdate.error = null
        binding.profileTxtInputName.error = null
        binding.profileTxtInputSurname.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.profileEditTextName.text.isNullOrBlank()) {
            binding.profileTxtInputName.error = getString(R.string.field_empty)
            result = false
        }
        if(binding.profileEditTextSurname.text.isNullOrBlank()) {
            binding.profileTxtInputSurname.error = getString(R.string.field_empty)
            result = false
        }
        if(binding.profileEditTextBirthdate.text.isNullOrBlank()) {
            binding.profileTxtInputBirthdate.error = getString(R.string.field_empty)
            result = false
        }

        return result
    }

    private fun sendEditProfileRequest() {
        val profileId = JwtUtils.getProfileIdFromSavedToken(requireContext())
        val url = "${TutortekConstants.BASE_URL}/profiles/$profileId"
        val body = formProfileUpdateRequestBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.PUT, url, body,
            {
                try {
                    updateSingletonData(body)
                    Toast.makeText(requireContext(), R.string.success_profile_edit, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_profile_edit, Toast.LENGTH_SHORT).show()
                    binding.btnEditSave.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formProfileUpdateRequestBody(): JSONObject {
        val firstName = binding.profileEditTextName.text.toString()
        val lastName = binding.profileEditTextSurname.text.toString()
        val birthDate = binding.profileEditTextBirthdate.text.toString()
        val description = binding.profileEditTextDescription.text.toString()
        val rating = ProfileSingleton.getInstance().userProfile?.rating
        val body = JSONObject()
        body.put("firstName", firstName)
        body.put("lastName", lastName)
        body.put("birthDate", birthDate)
        body.put("rating", rating?.toDouble())
        body.put("description", description)
        return body
    }

    private fun updateSingletonData(body: JSONObject) {
        val topicCount = ProfileSingleton.getInstance().userProfile?.topicCount
        val roles = ProfileSingleton.getInstance().userProfile?.roles
        body.put("topicCount", topicCount)
        val updatedProfile = roles?.let { UserProfile(body, it) }
        ProfileSingleton.getInstance().userProfile = updatedProfile
    }
}
