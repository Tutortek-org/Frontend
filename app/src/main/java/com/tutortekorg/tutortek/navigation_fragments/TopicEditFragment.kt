package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Topic
import com.tutortekorg.tutortek.databinding.FragmentTopicEditBinding

class TopicEditFragment : Fragment() {
    private lateinit var binding: FragmentTopicEditBinding
    private lateinit var topic: Topic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicEditBinding.inflate(inflater, container, false)
        binding.btnConfirmEditTopic.setOnClickListener { saveEditedTopic() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        topic = arguments?.getSerializable("topic") as Topic
        binding.editTextTopicNameEdit.setText(topic.name)
    }

    private fun saveEditedTopic() {

    }
}
