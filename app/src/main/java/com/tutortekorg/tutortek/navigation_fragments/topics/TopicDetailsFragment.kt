package com.tutortekorg.tutortek.navigation_fragments.topics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.utils.SystemUtils
import com.tutortekorg.tutortek.adapters.MeetingAdapter
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.FragmentTopicDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONArray
import org.json.JSONObject

class TopicDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTopicDetailsBinding
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicDetailsBinding.inflate(inflater, container, false)
        handleButtonVisibility()
        bindEvents()
        bindDataToUI()
        activity?.let { SystemUtils.changeBackgroundColorToThemeDependant(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry
            ?.savedStateHandle?.getLiveData<Topic>("topic")
            ?.observe(viewLifecycleOwner) {
                topic = it
                binding.txtTopicDetailsName.text = topic.name
                binding.txtTopicDescription.text = topic.description
            }
    }

    private fun handleButtonVisibility() {
        JwtUtils.isUserStudent(requireContext())?.let {
            if(it) {
                binding.btnAddMeeting.visibility = View.GONE
                binding.btnEditTopic.visibility = View.GONE
                binding.btnDeleteTopic.visibility = View.GONE
            }
        }
    }

    private fun bindEvents() {
        binding.btnDeleteTopic.setOnClickListener {
            SystemUtils.showConfirmDeleteDialog(
                requireContext(),
                R.string.meeting_delete_question,
                topic.name,
                ::sendTopicDeleteRequest
            )
        }
        binding.btnGetMeetings.setOnClickListener { sendMeetingsRequest() }
        binding.btnEditTopic.setOnClickListener {
            val bundle = bundleOf("topic" to topic)
            it.findNavController()
                .navigate(R.id.action_topicDetailsFragment_to_topicEditFragment, bundle)
        }
        binding.btnAddMeeting.setOnClickListener {
            val bundle = bundleOf("topic" to topic)
            it.findNavController()
                .navigate(R.id.action_topicDetailsFragment_to_meetingAddFragment, bundle)
        }
        binding.btnSeeProfile.setOnClickListener { getTutorProfile() }
        binding.refreshTopic.setOnRefreshListener { sendTopicGetRequest() }
    }

    private fun getTutorProfile() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/profiles/${topic.profileId}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.GET, url, null,
            {
                val roles = parseRoles(it)
                val userProfile = UserProfile(it, roles)
                val bundle = bundleOf("userProfile" to userProfile)
                findNavController().navigate(R.id.action_topicDetailsFragment_to_foreignProfileFragment, bundle)
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    revertButtonAnimations()
                    Toast.makeText(requireContext(), R.string.error_profile_retrieval, Toast.LENGTH_SHORT).show()
                }
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
        topic = arguments?.getSerializable("topic") as Topic
        binding.txtTopicDetailsName.text = topic.name
        binding.txtTopicDescription.text = topic.description
    }

    private fun sendTopicGetRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.GET, url, null,
            {
                topic = Topic(it)
                binding.txtTopicDetailsName.text = topic.name
                binding.txtTopicDescription.text = topic.description
                binding.refreshTopic.isRefreshing = false
            },
            {
                binding.refreshTopic.isRefreshing = false
                if(!JwtUtils.wasResponseUnauthorized(it))
                    Toast.makeText(requireContext(), R.string.error_topic_get, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun sendMeetingsRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings"
        val request = TutortekArrayRequest(requireContext(), Request.Method.GET, url, null,
            {
                if(it.length() == 0)
                    Toast.makeText(requireContext(), R.string.no_meetings, Toast.LENGTH_SHORT).show()
                else showMeetingBottomSheetDialog(it)
                revertButtonAnimations()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    revertButtonAnimations()
                    Toast.makeText(requireContext(), R.string.error_meetings_get, Toast.LENGTH_SHORT).show()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun showMeetingBottomSheetDialog(array: JSONArray) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = View.inflate(requireContext(), R.layout.layout_bottom_sheet, null)
        val meetings = parseMeetingsList(array)
        val recyclerView = view.findViewById(R.id.recycler_drawer) as RecyclerView
        recyclerView.adapter = MeetingAdapter(meetings, findNavController(), dialog, topic)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun sendTopicDeleteRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.DELETE, url, null,
            {
                activity?.onBackPressed()
            },
            {
                revertButtonAnimations()
                Toast.makeText(requireContext(), R.string.error_topic_delete, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun parseMeetingsList(array: JSONArray): List<Meeting> {
        val meetings = mutableListOf<Meeting>()
        for(i in 0 until array.length()) {
            meetings.add(Meeting(array.getJSONObject(i)))
        }
        return meetings
    }

    private fun revertButtonAnimations() {
        binding.btnDeleteTopic.revertAnimation()
        binding.btnEditTopic.revertAnimation()
        binding.btnGetMeetings.revertAnimation()
        binding.btnAddMeeting.revertAnimation()
        binding.btnSeeProfile.revertAnimation()
    }

    private fun startButtonAnimations() {
        binding.btnDeleteTopic.startAnimation()
        binding.btnEditTopic.startAnimation()
        binding.btnGetMeetings.startAnimation()
        binding.btnAddMeeting.startAnimation()
        binding.btnSeeProfile.startAnimation()
    }
}
