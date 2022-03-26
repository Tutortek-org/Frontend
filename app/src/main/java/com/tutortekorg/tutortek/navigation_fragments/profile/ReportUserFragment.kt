package com.tutortekorg.tutortek.navigation_fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentReportUserBinding
import com.tutortekorg.tutortek.utils.SystemUtils

class ReportUserFragment : Fragment() {
    private lateinit var binding: FragmentReportUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportUserBinding.inflate(inflater, container, false)
        activity?.let { SystemUtils.setupConstraints(it) }
        return binding.root
    }
}
