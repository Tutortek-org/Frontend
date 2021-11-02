package com.tutortekorg.tutortek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.tutortekorg.tutortek.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: OnboardingAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingAdapter()
        binding.viewPagerOnboarding.adapter = adapter

        setupOnboardingIndicators()
        setCurrentOnboardingIndicator(0)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnboardingIndicator(position)
            }
        })
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

    private fun setupOnboardingIndicators() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 0, 0)
        for(i in 0 until adapter.itemCount) {
            val indicator = ImageView(applicationContext)
            indicator.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_inactive))
            indicator.layoutParams = layoutParams
            binding.layoutOnboardingIndicators.addView(indicator)
        }
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        val childCount = binding.layoutOnboardingIndicators.childCount
        for(i in 0 until childCount) {
            val imageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView
            if(i == index) imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_active))
            else imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_inactive))
        }
    }
}
