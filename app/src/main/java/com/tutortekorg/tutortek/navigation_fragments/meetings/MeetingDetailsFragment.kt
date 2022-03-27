package com.tutortekorg.tutortek.navigation_fragments.meetings

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
import com.tutortekorg.tutortek.adapters.LearningMaterialAdapter
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.LearningMaterial
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentMeetingDetailsBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONArray

class MeetingDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMeetingDetailsBinding
    private lateinit var meeting: Meeting
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingDetailsBinding.inflate(inflater, container, false)
        handleButtonVisibility()
        bindEvents()
        bindDataToUI()
        activity?.let { SystemUtils.changeBackgroundColorToThemeDependant(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry
            ?.savedStateHandle?.getLiveData<Meeting>("meeting")
            ?.observe(viewLifecycleOwner) {
                meeting = it
                binding.txtMeetingAddress.text = meeting.address
                binding.txtMeetingAttendants.text = meeting.maxAttendants.toString()
                binding.txtMeetingDate.text = meeting.date
                binding.txtMeetingDescription.text = meeting.description
                binding.txtMeetingName.text = meeting.name
            }
    }

    private fun handleButtonVisibility() {
        JwtUtils.isUserStudent(requireContext())?.let {
            if(it) {
                binding.btnAddLearningMaterial.visibility = View.GONE
                binding.btnDeleteMeeting.visibility = View.GONE
                binding.btnEditMeeting.visibility = View.GONE
            }
        }
        val showRegisterButton = arguments?.getBoolean("showRegisterButton", false)
        if(showRegisterButton != null && !showRegisterButton)
            binding.btnPaymentDetails.visibility = View.GONE
    }

    private fun bindDataToUI() {
        meeting = arguments?.getSerializable("meeting") as Meeting
        topic = arguments?.getSerializable("topic") as Topic

        binding.txtMeetingAddress.text = meeting.address
        binding.txtMeetingAttendants.text = meeting.maxAttendants.toString()
        binding.txtMeetingDate.text = meeting.date
        binding.txtMeetingDescription.text = meeting.description
        binding.txtMeetingName.text = meeting.name
        binding.txtMeetingPrice.text = getString(R.string.meeting_price_header, meeting.price.toString())
    }

    private fun bindEvents() {
        binding.btnDeleteMeeting.setOnClickListener {
            SystemUtils.showConfirmDeleteDialog(
                requireContext(),
                R.string.meeting_delete_question,
                meeting.name,
                ::sendMeetingDeleteRequest
            )
        }
        binding.btnEditMeeting.setOnClickListener {
            val bundle = bundleOf("meeting" to meeting, "topic" to topic)
            it.findNavController()
                .navigate(R.id.action_meetingDetailsFragment_to_meetingEditFragment, bundle)
        }
        binding.btnGetLearningMaterials.setOnClickListener { sendLearningMaterialsGetRequest() }
        binding.btnAddLearningMaterial.setOnClickListener {
            val bundle = bundleOf("meeting" to meeting, "topic" to topic)
            it.findNavController()
                .navigate(R.id.action_meetingDetailsFragment_to_learningMaterialAddFragment, bundle)
        }
        binding.refreshMeeting.setOnRefreshListener { sendMeetingGetRequest() }
        binding.btnPaymentDetails.setOnClickListener {
            val bundle = bundleOf("meeting" to meeting)
            it.findNavController()
                .navigate(R.id.action_meetingDetailsFragment_to_meetingSignupFragment, bundle)
        }
    }

    private fun sendMeetingGetRequest() {
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}"
        val request = TutortekObjectRequest(requireContext(), Request.Method.GET, url, null,
            {
                meeting = Meeting(it)
                binding.txtMeetingAddress.text = meeting.address
                binding.txtMeetingAttendants.text = meeting.maxAttendants.toString()
                binding.txtMeetingDate.text = meeting.date
                binding.txtMeetingDescription.text = meeting.description
                binding.txtMeetingName.text = meeting.name
                binding.txtMeetingPrice.text = getString(R.string.meeting_price_header, meeting.price.toString())
                binding.refreshMeeting.isRefreshing = false
            },
            {
                binding.refreshMeeting.isRefreshing = false
                if(!JwtUtils.wasResponseUnauthorized(it))
                    Toast.makeText(requireContext(), R.string.error_meeting_get, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
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

    private fun sendLearningMaterialsGetRequest() {
        startButtonAnimations()
        val url = "${TutortekConstants.BASE_URL}/topics/${topic.id}/meetings/${meeting.id}/materials"
        val request = TutortekArrayRequest(requireContext(), Request.Method.GET, url, null,
            {
                if(it.length() == 0)
                    Toast.makeText(requireContext(), R.string.no_learning_materials, Toast.LENGTH_SHORT).show()
                else showLearningMaterialBottomSheetDialog(it)
                revertButtonAnimations()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)) {
                    revertButtonAnimations()
                    Toast.makeText(requireContext(), R.string.error_learning_materials_get, Toast.LENGTH_SHORT).show()
                }
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun showLearningMaterialBottomSheetDialog(array: JSONArray) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = View.inflate(requireContext(), R.layout.layout_bottom_sheet, null)
        val learningMaterials = parseMeetingsList(array)
        val recyclerView = view.findViewById(R.id.recycler_drawer) as RecyclerView
        recyclerView.adapter = LearningMaterialAdapter(learningMaterials, findNavController(), dialog, topic, meeting)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun parseMeetingsList(array: JSONArray): List<LearningMaterial> {
        val learningMaterials = mutableListOf<LearningMaterial>()
        for(i in 0 until array.length()) {
            learningMaterials.add(LearningMaterial(array.getJSONObject(i)))
        }
        return learningMaterials
    }

    private fun startButtonAnimations() {
        binding.btnDeleteMeeting.startAnimation()
        binding.btnEditMeeting.startAnimation()
        binding.btnGetLearningMaterials.startAnimation()
        binding.btnAddLearningMaterial.startAnimation()
        binding.btnPaymentDetails.startAnimation()
    }

    private fun revertButtonAnimations() {
        binding.btnDeleteMeeting.revertAnimation()
        binding.btnEditMeeting.revertAnimation()
        binding.btnGetLearningMaterials.revertAnimation()
        binding.btnAddLearningMaterial.revertAnimation()
        binding.btnPaymentDetails.revertAnimation()
    }
}
