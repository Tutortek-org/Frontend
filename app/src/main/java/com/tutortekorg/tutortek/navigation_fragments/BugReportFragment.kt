package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentBugReportBinding

class BugReportFragment : Fragment() {
    private lateinit var binding: FragmentBugReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBugReportBinding.inflate(inflater, container, false)
        return binding.root
    }
}
