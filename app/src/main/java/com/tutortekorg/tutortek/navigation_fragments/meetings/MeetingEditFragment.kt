package com.tutortekorg.tutortek.navigation_fragments.meetings

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
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentMeetingEditBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MeetingEditFragment : Fragment() {
    private lateinit var binding: FragmentMeetingEditBinding
    private lateinit var meeting: Meeting
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingEditBinding.inflate(inflater, container, false)
        binding.btnConfirmEditMeeting.setOnClickListener { saveEditedMeeting() }
        binding.editTextMeetingDateEdit.setOnClickListener { onDateClick() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        topic = arguments?.getSerializable("topic") as Topic
        meeting = arguments?.getSerializable("meeting") as Meeting

        binding.editTextMeetingAddressEdit.setText(meeting.address)
        binding.editTextMeetingAttendantsEdit.setText(meeting.maxAttendants.toString())
        binding.editTextMeetingDateEdit.setText(meeting.date)
        binding.editTextMeetingDescriptionEdit.setText(meeting.description)
        binding.editTextMeetingNameEdit.setText(meeting.name)
    }

    private fun onDateClick() {
        val datePickerFragment = DatePickerFragment(binding.editTextMeetingDateEdit)
        activity?.supportFragmentManager?.let { datePickerFragment.show(it, "datePicker") }
    }

    private fun saveEditedMeeting() {
        binding.btnConfirmEditMeeting.startAnimation()
        clearPreviousErrors()
        if(validateForm()) sendMeetingPutRequest()
        else binding.btnConfirmEditMeeting.revertAnimation()
    }

    private fun clearPreviousErrors() {
        binding.txtInputMeetingAddressEdit.error = null
        binding.txtInputMeetingAttendantsEdit.error = null
        binding.txtInputMeetingDateEdit.error = null
        binding.txtInputMeetingDescriptionEdit.error = null
        binding.txtInputMeetingNameEdit.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextMeetingNameEdit.text.isNullOrBlank()) {
            binding.txtInputMeetingNameEdit.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextMeetingAddressEdit.text.isNullOrBlank()) {
            binding.txtInputMeetingAddressEdit.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextMeetingDescriptionEdit.text.isNullOrBlank()) {
            binding.txtInputMeetingDescriptionEdit.error = getString(R.string.field_empty)
            result = false
        }

        if(!isAttendantCountValid(binding.editTextMeetingAttendantsEdit.text.toString())) {
            binding.txtInputMeetingAttendantsEdit.error = getString(R.string.error_invalid_integer)
            result = false
        }

        if(binding.editTextMeetingDateEdit.text.isNullOrBlank()
            || !isDateValid(binding.editTextMeetingDateEdit.text.toString())) {
            binding.txtInputMeetingDateEdit.error = getString(R.string.error_invalid_meeting_date)
            result = false
        }

        return result
    }

    private fun isAttendantCountValid(attendants: String): Boolean {
        val regex = "[1-9]([0-9]?)+".toRegex()
        return regex.matches(attendants)
    }

    private fun isDateValid(date: String): Boolean {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = parser.parse(date)
        return parsedDate!!.after(Date())
    }

    private fun sendMeetingPutRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}"
        val body = formUpdateMeetingBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.PUT, url, body,
            {
                goBackToDetailsFragment()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_meeting_edit, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmEditMeeting.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formUpdateMeetingBody(): JSONObject {
        val name = binding.editTextMeetingNameEdit.text.toString()
        val date = binding.editTextMeetingDateEdit.text.toString()
        val maxAttendants = binding.editTextMeetingAttendantsEdit.text.toString().toInt()
        val address = binding.editTextMeetingAddressEdit.text.toString()
        val description = binding.editTextMeetingDescriptionEdit.text.toString()

        val body = JSONObject().apply {
            put("name", name)
            put("date", date)
            put("maxAttendants", maxAttendants)
            put("address", address)
            put("description", description)
        }

        return body
    }

    private fun goBackToDetailsFragment() {
        meeting.name = binding.editTextMeetingNameEdit.text.toString()
        meeting.date = binding.editTextMeetingDateEdit.text.toString()
        meeting.maxAttendants = binding.editTextMeetingAttendantsEdit.text.toString().toInt()
        meeting.address = binding.editTextMeetingAddressEdit.text.toString()
        meeting.description = binding.editTextMeetingDescriptionEdit.text.toString()

        try {
            val navController = findNavController()
            navController.previousBackStackEntry?.savedStateHandle?.set("meeting", meeting)
            navController.popBackStack()
        }
        catch (e: Exception){}
    }
}
