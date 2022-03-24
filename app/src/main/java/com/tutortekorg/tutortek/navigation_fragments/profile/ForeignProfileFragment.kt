package com.tutortekorg.tutortek.navigation_fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentForeignProfileBinding

class ForeignProfileFragment : Fragment() {
    private lateinit var binding: FragmentForeignProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForeignProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
}
