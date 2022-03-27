package com.tutortekorg.tutortek.navigation_fragments.profile

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
import com.tutortekorg.tutortek.databinding.FragmentReportUserBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONObject
import java.lang.Exception

class ReportUserFragment : Fragment() {
    private lateinit var binding: FragmentReportUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportUserBinding.inflate(inflater, container, false)
        binding.btnConfirmUserReport.setOnClickListener { submitReport() }
        activity?.let { SystemUtils.changeBackgroundColorToPrimary(it) }
        return binding.root
    }

    private fun submitReport() {
        binding.btnConfirmUserReport.startAnimation()
        binding.txtInputReportDescription.error = null
        if(!binding.editTextReportDescription.text.isNullOrBlank()) sendReportPostRequest()
        else {
            binding.txtInputReportDescription.error = getString(R.string.field_empty)
            binding.btnConfirmUserReport.revertAnimation()
        }
    }

    private fun sendReportPostRequest() {
        val url = "${TutortekConstants.BASE_URL}/userreports"
        val body = formBody()
        val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body,
            {
                try {
                    Toast.makeText(requireContext(), R.string.user_report_success, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                catch (e: Exception){}
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    Toast.makeText(requireContext(), R.string.error_user_report, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmUserReport.startAnimation()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun formBody(): JSONObject {
        val description = binding.editTextReportDescription.text.toString()
        val userId = arguments?.getLong("userId", 0)
        val body = JSONObject().apply {
            put("description", description)
            put("reportOf", userId)
        }
        return body
    }
}
