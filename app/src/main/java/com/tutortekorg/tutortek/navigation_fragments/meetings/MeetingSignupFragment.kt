package com.tutortekorg.tutortek.navigation_fragments.meetings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.databinding.FragmentMeetingSignupBinding

class MeetingSignupFragment : Fragment() {
    private lateinit var binding: FragmentMeetingSignupBinding
    private lateinit var meeting: Meeting

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingSignupBinding.inflate(inflater, container, false)
        meeting = arguments?.getSerializable("meeting") as Meeting
        binding.txtMeetingNamePay.text = getString(R.string.meeting_name_header, meeting.name)
        binding.txtMeetingPricePay.text = getString(R.string.meeting_price_header, meeting.price.toString())
        return binding.root
    }
}
