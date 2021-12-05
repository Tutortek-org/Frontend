package com.tutortekorg.tutortek.navigation_fragments.topics

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.authentication.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
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

        bindEventsToButtons()
        bindDataToUI()

        return binding.root
    }

    private fun bindEventsToButtons() {
        binding.btnDeleteTopic.setOnClickListener { showConfirmDialog() }
        binding.btnGetMeetings.setOnClickListener { sendMeetingsRequest() }
        binding.btnEditTopic.setOnClickListener {
            val bundle = bundleOf("topic" to topic)
            it.findNavController()
                .navigate(R.id.action_topicDetailsFragment_to_topicEditFragment, bundle)
        }
    }

    private fun bindDataToUI() {
        topic = arguments?.getSerializable("topic") as Topic
        binding.txtTopicDetailsName.text = topic.name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry
            ?.savedStateHandle?.getLiveData<Topic>("topic")
            ?.observe(viewLifecycleOwner) {
                topic = it
                binding.txtTopicDetailsName.text = topic.name
            }
    }

    private fun sendMeetingsRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings"
        val request =  TutortekArrayRequest(requireContext(), Request.Method.GET, url, null,
            {
                if(it.length() == 0)
                    Toast.makeText(requireContext(), R.string.no_meetings, Toast.LENGTH_SHORT).show()
                else showMeetingBottomSheetDialog()
                reverseButtonAnimations()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    reverseButtonAnimations()
                    Toast.makeText(requireContext(), R.string.error_meeting_get, Toast.LENGTH_SHORT).show()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun showMeetingBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = View.inflate(requireContext(), R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)
        dialog.show()
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
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.DELETE, url, null,
            {
                activity?.onBackPressed()
            },
            {
                reverseButtonAnimations()
                Toast.makeText(requireContext(), R.string.error_topic_delete, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun reverseButtonAnimations() {
        binding.btnDeleteTopic.revertAnimation()
        binding.btnEditTopic.revertAnimation()
        binding.btnGetMeetings.revertAnimation()
    }

    private fun startButtonAnimations() {
        binding.btnDeleteTopic.startAnimation()
        binding.btnEditTopic.startAnimation()
        binding.btnGetMeetings.startAnimation()
    }
}
