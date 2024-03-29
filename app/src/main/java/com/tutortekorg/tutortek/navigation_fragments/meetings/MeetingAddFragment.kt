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
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentMeetingAddBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MeetingAddFragment : Fragment() {
    private lateinit var binding: FragmentMeetingAddBinding
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingAddBinding.inflate(inflater, container, false)
        topic = arguments?.getSerializable("topic") as Topic

        binding.editTextMeetingDate.keyListener = null
        binding.editTextMeetingDate.setOnClickListener { onDateClick() }
        binding.btnConfirmAddMeeting.setOnClickListener { onAddClick() }

        activity?.let { SystemUtils.changeBackgroundColorToPrimary(it) }
        return binding.root
    }

    private fun onDateClick() {
        val datePickerFragment = DatePickerFragment(binding.editTextMeetingDate)
        activity?.supportFragmentManager?.let { datePickerFragment.show(it, "datePicker") }
    }

    private fun onAddClick() {
        binding.btnConfirmAddMeeting.startAnimation()
        clearPreviousErrors()
        if(validateForm()) sendMeetingPostRequest()
        else binding.btnConfirmAddMeeting.revertAnimation()
    }

    private fun clearPreviousErrors() {
        binding.txtInputMeetingAddress.error = null
        binding.txtInputMeetingAttendants.error = null
        binding.txtInputMeetingDate.error = null
        binding.txtInputMeetingDescription.error = null
        binding.txtInputMeetingName.error = null
        binding.txtInputMeetingPrice.error = null
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextMeetingName.text.isNullOrBlank()) {
            binding.txtInputMeetingName.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextMeetingAddress.text.isNullOrBlank()) {
            binding.txtInputMeetingAddress.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextMeetingDescription.text.isNullOrBlank()) {
            binding.txtInputMeetingDescription.error = getString(R.string.field_empty)
            result = false
        }

        if(!isAttendantCountValid(binding.editTextMeetingAttendants.text.toString())) {
            binding.txtInputMeetingAttendants.error = getString(R.string.error_invalid_integer)
            result = false
        }

        if(binding.editTextMeetingDate.text.isNullOrBlank()
            || !isDateValid(binding.editTextMeetingDate.text.toString())) {
            binding.txtInputMeetingDate.error = getString(R.string.error_invalid_meeting_date)
            result = false
        }

        if(binding.editTextMeetingPrice.text.isNullOrBlank()
            || !isPriceValid(binding.editTextMeetingPrice.text.toString())) {
            binding.txtInputMeetingPrice.error = getString(R.string.error_invalid_price)
            result = false
        }

        return result
    }

    private fun isPriceValid(price: String): Boolean {
        val regex = "^\\d{0,8}(\\.\\d{1,4})?\$".toRegex()
        return regex.matches(price)
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

    private fun sendMeetingPostRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings"
        val body = formCreateMeetingBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body,
            {
                try {
                    Toast.makeText(requireContext(), R.string.meeting_add_success, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_meeting_post, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmAddMeeting.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formCreateMeetingBody(): JSONObject {
        val name = binding.editTextMeetingName.text.toString()
        val date = binding.editTextMeetingDate.text.toString()
        val maxAttendants = binding.editTextMeetingAttendants.text.toString().toInt()
        val address = binding.editTextMeetingAddress.text.toString()
        val description = binding.editTextMeetingDescription.text.toString()
        val price = binding.editTextMeetingPrice.text.toString().toDouble()

        val body = JSONObject().apply {
            put("name", name)
            put("date", date)
            put("maxAttendants", maxAttendants)
            put("address", address)
            put("description", description)
            put("price", price)
        }

        return body
    }
}
