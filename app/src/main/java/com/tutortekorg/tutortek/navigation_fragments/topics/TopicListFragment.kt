package com.tutortekorg.tutortek.navigation_fragments.topics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.adapters.TopicAdapter
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicListBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.SystemUtils
import org.json.JSONArray

class TopicListFragment : Fragment() {
    private lateinit var binding: FragmentTopicListBinding
    private lateinit var adapter: TopicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicListBinding.inflate(inflater, container, false)
        handleButtonVisibility()

        binding.refreshTopics.setOnRefreshListener { bindDataToUI() }
        binding.btnAddTopic.setOnClickListener {
            it.findNavController().navigate(R.id.action_topicListFragment_to_addTopicFragment)
        }

        activity?.let { SystemUtils.changeBackgroundColorToThemeDependant(it) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        bindDataToUI()
    }

    private fun handleButtonVisibility() {
        JwtUtils.isUserStudent(requireContext())?.let {
            if(it) binding.btnAddTopic.visibility = View.GONE
        }
    }

    private fun bindDataToUI() {
        val endpoint = arguments?.getString("endpoint", "")
        val url = "${TutortekConstants.BASE_URL}/topics$endpoint"
        val request = TutortekArrayRequest(requireContext(), Request.Method.GET, url, null,
            {
                val topics = parseTopicList(it)
                adapter = TopicAdapter(topics)
                binding.recyclerTopics.scheduleLayoutAnimation()
                binding.recyclerTopics.adapter = adapter
                binding.refreshTopics.isRefreshing = false
            },
            {
                binding.refreshTopics.isRefreshing = false
                if(!JwtUtils.wasResponseUnauthorized(it))
                    Toast.makeText(requireContext(), R.string.error_topics_get, Toast.LENGTH_SHORT).show()
            }
        )
        RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun parseTopicList(array: JSONArray): List<Topic> {
        val topics = mutableListOf<Topic>()
        for(i in 0 until array.length()) {
            topics.add(Topic(array.getJSONObject(i)))
        }
        return topics
    }
}
