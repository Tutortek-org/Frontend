package com.tutortekorg.tutortek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tutortek.R
import com.example.tutortek.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: OnboardingAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingAdapter()
        binding.viewPagerOnboarding.adapter = adapter
    }

    private fun setupOnboardingAdapter() {
        val list = mutableListOf<OnboardingItem>()

        list.add(OnboardingItem(
            R.mipmap.ic_books,
            "Learn",
            "Learn new and exciting thins!")
        )
        list.add(OnboardingItem(
            R.mipmap.ic_handshake,
            "Meet",
            "Meet your local Tutortek community!")
        )
        list.add(OnboardingItem(
            R.mipmap.ic_money,
            "Earn",
            "Earn money while sharing knowledge!")
        )

        adapter = OnboardingAdapter(list)
    }
}
