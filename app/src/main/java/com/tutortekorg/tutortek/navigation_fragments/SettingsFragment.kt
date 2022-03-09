package com.tutortekorg.tutortek.navigation_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.btnLogout.setOnClickListener { logout() }
        binding.darkTheme.setOnClickListener { changeTheme() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val preferences = requireContext().getSharedPreferences(TutortekConstants.SETTING_PREFERENCES, Context.MODE_PRIVATE)
        val isDarkModeOn = preferences.getBoolean(TutortekConstants.DARK_MODE_FLAG, false)
        binding.darkTheme.isChecked = isDarkModeOn
    }

    private fun changeTheme() {
        val preferences = requireContext().getSharedPreferences(TutortekConstants.SETTING_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(TutortekConstants.DARK_MODE_FLAG, binding.darkTheme.isChecked)
        editor.apply()
    }

    private fun logout() {
        activity?.let { JwtUtils.invalidateJwtToken(it) }
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        activity?.finish()
    }
}
