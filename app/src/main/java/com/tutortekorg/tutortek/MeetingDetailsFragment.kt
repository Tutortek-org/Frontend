package com.tutortekorg.tutortek

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.databinding.FragmentMeetingDetailsBinding

class MeetingDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMeetingDetailsBinding
    private lateinit var meeting: Meeting

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingDetailsBinding.inflate(inflater, container, false)
        bindDataToUI()
        return binding.root
    }

    private fun bindDataToUI() {
        meeting = arguments?.getSerializable("meeting") as Meeting
        binding.txtMeetingAddress.text = meeting.address
        binding.txtMeetingAttendants.text = meeting.maxAttendants.toString()
        binding.txtMeetingDate.text = meeting.date
        binding.txtMeetingDescription.text = meeting.description
        binding.txtMeetingName.text = meeting.name
    }
}
