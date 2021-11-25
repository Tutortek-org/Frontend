package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.adapters.TopicAdapter
import com.tutortekorg.tutortek.authentication.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicListBinding
import com.tutortekorg.tutortek.requests.TutortekArrayRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONArray

class TopicListFragment : Fragment() {
    private lateinit var binding: FragmentTopicListBinding
    private lateinit var adapter: TopicAdapter
    private lateinit var request: TutortekArrayRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicListBinding.inflate(inflater, container, false)
        binding.refreshTopics.setOnRefreshListener { bindDataToUI() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        bindDataToUI()
    }

    private fun bindDataToUI() {
        val url = "${TutortekConstants.BASE_URL}/topics"
        request = TutortekArrayRequest(requireContext(), Request.Method.GET, url, null,
            {
                val topics = parseTopicList(it)
                adapter = TopicAdapter(topics)
                binding.recyclerTopics.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerTopics.adapter = adapter
                binding.refreshTopics.isRefreshing = false
            },
            { err ->
                binding.refreshTopics.isRefreshing = false
                if(JwtUtils.wasResponseUnauthorized(err))
                    activity?.let { JwtUtils.sendRefreshRequest(it, false, request) }
                else Toast.makeText(requireContext(), R.string.error_topic_get, Toast.LENGTH_SHORT).show()
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
