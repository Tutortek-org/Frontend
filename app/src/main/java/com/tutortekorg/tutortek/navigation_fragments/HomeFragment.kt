package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessaging
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentHomeBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnAllTopics.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_topicListFragment)
        }
        binding.btnMyTopics.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_personalTopicListFragment)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val profileId = JwtUtils.getProfileIdFromSavedToken(requireContext())
        context?.let {
            if (profileId != null) SystemUtils.downloadProfilePhoto(it, profileId)
        }
        registerDeviceToken()
    }

    private fun registerDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val url = "${TutortekConstants.BASE_URL}/notifications"
                val body = JSONObject()
                body.put("deviceToken", it.result)
                val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body, {}, {})
                RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
            }
        }
    }
}
