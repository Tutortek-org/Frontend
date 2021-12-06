package com.tutortekorg.tutortek.navigation_fragments.topics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.SystemUtils
import com.tutortekorg.tutortek.adapters.MeetingAdapter
import com.tutortekorg.tutortek.authentication.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONArray

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
        binding.btnDeleteTopic.setOnClickListener {
            SystemUtils.showConfirmDeleteDialog(
                requireContext(),
                R.string.meeting_delete_question,
                topic.name,
                ::sendTopicDeleteRequest
            )
        }
        binding.btnGetMeetings.setOnClickListener { sendMeetingsRequest() }
        binding.btnEditTopic.setOnClickListener {
            val bundle = bundleOf("topic" to topic)
            it.findNavController()
                .navigate(R.id.action_topicDetailsFragment_to_topicEditFragment, bundle)
        }
        binding.btnAddMeeting.setOnClickListener {
            val bundle = bundleOf("topic" to topic)
            it.findNavController()
                .navigate(R.id.action_topicDetailsFragment_to_meetingAddFragment, bundle)
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
                else showMeetingBottomSheetDialog(it)
                revertButtonAnimations()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    revertButtonAnimations()
                    Toast.makeText(requireContext(), R.string.error_meeting_get, Toast.LENGTH_SHORT).show()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun showMeetingBottomSheetDialog(array: JSONArray) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = View.inflate(requireContext(), R.layout.layout_bottom_sheet, null)
        val meetings = parseMeetingsList(array)
        val recyclerView = view.findViewById(R.id.recycler_meetings) as RecyclerView
        recyclerView.adapter = MeetingAdapter(meetings, findNavController(), dialog, topic)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun sendTopicDeleteRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}"
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

    private fun parseMeetingsList(array: JSONArray): List<Meeting> {
        val topics = mutableListOf<Meeting>()
        for(i in 0 until array.length()) {
            topics.add(Meeting(array.getJSONObject(i)))
        }
        return topics
    }

    private fun revertButtonAnimations() {
        binding.btnDeleteTopic.revertAnimation()
        binding.btnEditTopic.revertAnimation()
        binding.btnGetMeetings.revertAnimation()
        binding.btnAddMeeting.revertAnimation()
    }

    private fun startButtonAnimations() {
        binding.btnDeleteTopic.startAnimation()
        binding.btnEditTopic.startAnimation()
        binding.btnGetMeetings.startAnimation()
        binding.btnAddMeeting.startAnimation()
    }
}
