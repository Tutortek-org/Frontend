package com.tutortekorg.tutortek.navigation_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.authentication.JwtUtils
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.btnLogout.setOnClickListener { logout() }
        return binding.root
    }

    private fun logout() {
        activity?.let { JwtUtils.invalidateJwtToken(it) }
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        activity?.finish()
    }
}
