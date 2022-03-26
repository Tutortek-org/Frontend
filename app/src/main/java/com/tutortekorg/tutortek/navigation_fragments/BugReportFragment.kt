package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentBugReportBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject

class BugReportFragment : Fragment() {
    private lateinit var binding: FragmentBugReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBugReportBinding.inflate(inflater, container, false)
        binding.btnSendReport.setOnClickListener { sendReport() }
        activity?.let { SystemUtils.setupConstraints(it) }
        return binding.root
    }

    private fun sendReport() {
        binding.btnSendReport.startAnimation()
        binding.txtInputBugName.error = null
        binding.txtInputBugDescription.error = null
        if(validateForm()) sendCreateReportRequest()
        else binding.btnSendReport.revertAnimation()
    }

    private fun validateForm(): Boolean {
        var result = true

        if(binding.editTextBugName.text.isNullOrBlank()) {
            binding.txtInputBugName.error = getString(R.string.field_empty)
            result = false
        }

        if(binding.editTextBugDescription.text.isNullOrBlank()) {
            binding.txtInputBugDescription.error = getString(R.string.field_empty)
            result = false
        }

        return result
    }

    private fun sendCreateReportRequest() {
        val url = "${TutortekConstants.BASE_URL}/bugreports"
        val body = formRequestBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body,
            {
                try {
                    Toast.makeText(requireContext(), R.string.bug_report_success, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_bug_report_send, Toast.LENGTH_SHORT).show()
                    binding.btnSendReport.revertAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formRequestBody(): JSONObject {
        val name = binding.editTextBugName.text.toString()
        val description = binding.editTextBugDescription.text.toString()
        val body = JSONObject().apply {
            put("name", name)
            put("description", description)
        }
        return body
    }
}
