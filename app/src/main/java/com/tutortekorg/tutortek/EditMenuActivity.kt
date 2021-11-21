package com.tutortekorg.tutortek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutortekorg.tutortek.databinding.ActivityEditMenuBinding

class EditMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangeProfileData.setOnClickListener { onEditProfileClick() }
    }

    private fun onEditProfileClick() {
        val intent = Intent(this, ProfileEditActivity::class.java)
        startActivity(intent)
    }
}
