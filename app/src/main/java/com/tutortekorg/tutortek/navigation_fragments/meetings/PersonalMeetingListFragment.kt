package com.tutortekorg.tutortek.navigation_fragments.meetings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.adapters.PersonalMeetingAdapter
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentPersonalMeetingListBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONArray

class PersonalMeetingListFragment : Fragment() {
    private lateinit var binding: FragmentPersonalMeetingListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalMeetingListBinding.inflate(inflater, container, false)
        binding.refreshMeetings.setOnRefreshListener { bindDataToUI() }
        activity?.let { SystemUtils.changeBackgroundColorToThemeDependant(it) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        bindDataToUI()
    }

    private fun bindDataToUI() {
        val url = "${TutortekConstants.BASE_URL}/meetings/personal"
        val request = TutortekArrayRequest(requireContext(), Request.Method.GET, url, null,
            {
                val meetings = parseMeetingList(it)
                val topics = parseTopicList(it)
                binding.recyclerMeetings.scheduleLayoutAnimation()
                binding.recyclerMeetings.adapter = PersonalMeetingAdapter(meetings, findNavController(), topics)
                binding.refreshMeetings.isRefreshing = false
            },
            {
                binding.refreshMeetings.isRefreshing = false
                if(!JwtUtils.wasResponseUnauthorized(it))
                    Toast.makeText(requireContext(), R.string.error_meetings_get, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun parseMeetingList(array: JSONArray): List<Meeting> {
        val meetings = mutableListOf<Meeting>()
        for(i in 0 until array.length()) {
            meetings.add(Meeting(array.getJSONObject(i)))
        }
        return meetings
    }

    private fun parseTopicList(array: JSONArray): List<Topic> {
        val topics = mutableListOf<Topic>()
        for(i in 0 until array.length()) {
            val meetingJson = array.getJSONObject(i)
            topics.add(Topic(meetingJson.getJSONObject("topic")))
        }
        return topics
    }
}
