package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.auth0.android.jwt.JWT
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.RequestSingleton
import com.tutortekorg.tutortek.TutortekUtils
import com.tutortekorg.tutortek.constants.ErrorSlug
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentProfileBinding
import org.json.JSONObject

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        sendProfileGetRequest()
        return binding.root
    }

    private fun sendProfileGetRequest() {
        val token = activity?.let { TutortekUtils.getJwtToken(it) }
        val profileId = token?.let { getProfileId(it) }
        val url = "${TutortekConstants.BASE_URL}/profiles/$profileId"
        val request = object : JsonObjectRequest(Method.GET, url, null,
            {
                fillOutUI(it)
            },
            {
                Toast.makeText(requireContext(), ErrorSlug.PROFILE_RETRIEVAL_ERROR, Toast.LENGTH_SHORT).show()
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
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun getProfileId(token: String): Long? {
        val jwt = JWT(token)
        val profileId = jwt.getClaim("pid")
        return profileId.asLong()
    }

    private fun fillOutUI(body: JSONObject) {
        val firstName = body.getString("firstName")
        val lastName = body.getString("lastName")
        binding.txtProfileName.text = getString(R.string.profile_full_name, firstName, lastName)

        val birthDate = body.getString("birthDate")
        binding.txtProfileExtra.text = getString(R.string.profile_birth_date, birthDate)

        binding.txtProfileRating.text = body.getDouble("rating").toString()
    }
}
