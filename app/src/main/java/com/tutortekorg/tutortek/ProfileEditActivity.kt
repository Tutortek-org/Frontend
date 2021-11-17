package com.tutortekorg.tutortek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.auth0.android.jwt.JWT
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.ActivityProfileEditBinding
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayCurrentInformation()

        binding.btnEditSave.setOnClickListener { saveNewInformation() }
        binding.profileEditTextBirthdate.setOnClickListener { onBirthDateClick() }
        binding.profileEditTextBirthdate.keyListener = null
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
        datePickerFragment.show(supportFragmentManager, "datePicker")
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
        val token = TutortekUtils.getJwtToken(this)
        val profileId = token?.let { getProfileId(it) }
        val url = "${TutortekConstants.BASE_URL}/profiles/$profileId"
        val body = formProfileUpdateRequestBody()
        val request = object : JsonObjectRequest(
            Method.PUT, url, body,
            {
                updateSingletonData(body)
                Toast.makeText(this, R.string.success_profile_edit, Toast.LENGTH_SHORT).show()
                onBackPressed()
            },
            {
                Toast.makeText(this, R.string.error_profile_edit, Toast.LENGTH_SHORT).show()
                binding.btnEditSave.revertAnimation()
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun getProfileId(token: String): Long? {
        val jwt = JWT(token)
        val profileId = jwt.getClaim("pid")
        return profileId.asLong()
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