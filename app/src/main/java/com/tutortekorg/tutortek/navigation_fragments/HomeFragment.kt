package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnAllTopics.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_topicListFragment)
        }
        binding.btnMyTopics.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_personalTopicListFragment)
        }

        return binding.root
    }
}
