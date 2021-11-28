package com.tutortekorg.tutortek.navigation_fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.authentication.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton

class TopicDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTopicDetailsBinding
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicDetailsBinding.inflate(inflater, container, false)

        binding.btnDeleteTopic.setOnClickListener { showConfirmDialog() }
        binding.btnEditTopic.setOnClickListener {

        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        topic = arguments?.getSerializable("topic") as Topic
        binding.txtTopicDetailsName.text = topic.name
    }

    private fun showConfirmDialog() {
        val message = getString(R.string.topic_delete_question, topic.name)
        val alert = AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirm_delete)
            .setMessage(message)
            .setPositiveButton(R.string.btn_no) { _: DialogInterface, _: Int -> }
            .setNegativeButton(R.string.btn_yes) { _: DialogInterface, _: Int ->
                sendTopicDeleteRequest()
            }
            .create()
        val color = requireContext().getColor(R.color.color_primary)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color)
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color)
    }

    private fun sendTopicDeleteRequest() {
        binding.btnDeleteTopic.startAnimation()
        binding.btnEditTopic.startAnimation()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.DELETE, url, null,
            {
                activity?.onBackPressed()
            },
            {
                if(it.networkResponse == null || it.networkResponse.statusCode == 204)
                    activity?.onBackPressed()
                else {
                    binding.btnDeleteTopic.revertAnimation()
                    binding.btnEditTopic.revertAnimation()
                    Toast.makeText(requireContext(), R.string.error_topic_delete, Toast.LENGTH_SHORT).show()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }
}
