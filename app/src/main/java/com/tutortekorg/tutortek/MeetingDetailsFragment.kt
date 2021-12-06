package com.tutortekorg.tutortek

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentMeetingDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton

class MeetingDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMeetingDetailsBinding
    private lateinit var meeting: Meeting
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingDetailsBinding.inflate(inflater, container, false)

        bindEventsToButtons()
        bindDataToUI()

        return binding.root
    }

    private fun bindDataToUI() {
        meeting = arguments?.getSerializable("meeting") as Meeting
        topic = arguments?.getSerializable("topic") as Topic

        binding.txtMeetingAddress.text = meeting.address
        binding.txtMeetingAttendants.text = meeting.maxAttendants.toString()
        binding.txtMeetingDate.text = meeting.date
        binding.txtMeetingDescription.text = meeting.description
        binding.txtMeetingName.text = meeting.name
    }

    private fun bindEventsToButtons() {
        binding.btnDeleteMeeting.setOnClickListener {
            SystemUtils.showConfirmDeleteDialog(
                requireContext(),
                R.string.meeting_delete_question,
                meeting.name,
                ::sendMeetingDeleteRequest
            )
        }
    }

    private fun sendMeetingDeleteRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}"
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

    private fun startButtonAnimations() {
        binding.btnDeleteMeeting.startAnimation()
        binding.btnEditMeeting.startAnimation()
    }

    private fun revertButtonAnimations() {
        binding.btnDeleteMeeting.revertAnimation()
        binding.btnEditMeeting.revertAnimation()
    }
}
