package com.tutortekorg.tutortek.navigation_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentSettingsBinding
import com.tutortekorg.tutortek.utils.SystemUtils

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.btnLogout.setOnClickListener { logout() }
        binding.darkTheme.setOnClickListener { changeTheme() }
        binding.notifications.setOnClickListener { toggleNotifications() }
        binding.btnReportBug.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingsFragment_to_bugReportFragment)
        }
        activity?.let { SystemUtils.changeBackgroundColorToThemeDependant(it) }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val preferences = requireContext().getSharedPreferences(TutortekConstants.SETTING_PREFERENCES, Context.MODE_PRIVATE)
        binding.darkTheme.isChecked = preferences.getBoolean(TutortekConstants.DARK_MODE_FLAG, false)
        binding.notifications.isChecked = preferences.getBoolean(TutortekConstants.NOTIFICATIONS_FLAG, true)
    }

    private fun toggleNotifications() {
        val preferences = requireContext().getSharedPreferences(TutortekConstants.SETTING_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val areNotificationsAllowed = binding.notifications.isChecked
        editor.putBoolean(TutortekConstants.NOTIFICATIONS_FLAG, areNotificationsAllowed)
        editor.apply()
    }

    private fun changeTheme() {
        val preferences = requireContext().getSharedPreferences(TutortekConstants.SETTING_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val isDarkModeOn = binding.darkTheme.isChecked
        editor.putBoolean(TutortekConstants.DARK_MODE_FLAG, isDarkModeOn)
        editor.apply()

        if(isDarkModeOn) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun logout() {
        binding.btnLogout.isClickable = false
        activity?.let { JwtUtils.invalidateJwtToken(it) }
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        activity?.finish()
    }
}
