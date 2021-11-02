package com.tutortekorg.tutortek

import android.content.Intent
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

        binding.btnOnboardingAction.setOnClickListener {
            if(binding.viewPagerOnboarding.currentItem + 1 < adapter.itemCount)
                binding.viewPagerOnboarding.currentItem = binding.viewPagerOnboarding.currentItem + 1
            else {
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupOnboardingAdapter() {
        val list = mutableListOf<OnboardingItem>()

        list.add(OnboardingItem(
            R.mipmap.ic_books,
            resources.getString(R.string.title_learn),
            resources.getString(R.string.description_learn))
        )
        list.add(OnboardingItem(
            R.mipmap.ic_handshake,
            resources.getString(R.string.title_meet),
            resources.getString(R.string.description_meet))
        )
        list.add(OnboardingItem(
            R.mipmap.ic_money,
            resources.getString(R.string.title_earn),
            resources.getString(R.string.description_earn))
        )

        adapter = OnboardingAdapter(list)
    }

    private fun setupOnboardingIndicators() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
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

        if(index == adapter.itemCount - 1) binding.btnOnboardingAction.text =  resources.getString(R.string.button_get_started)
        else binding.btnOnboardingAction.text =  resources.getString(R.string.button_next)
    }
}
