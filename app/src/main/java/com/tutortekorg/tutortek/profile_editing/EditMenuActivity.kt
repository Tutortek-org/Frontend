package com.tutortekorg.tutortek.profile_editing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutortekorg.tutortek.ProfileEditActivity
import com.tutortekorg.tutortek.databinding.ActivityEditMenuBinding

class EditMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangeProfileData.setOnClickListener { onEditProfileClick() }
        binding.btnChangePassword.setOnClickListener { onChangePasswordClick() }
    }

    private fun onEditProfileClick() {
        val intent = Intent(this, ProfileEditActivity::class.java)
        startActivity(intent)
    }

    private fun onChangePasswordClick() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }
}
