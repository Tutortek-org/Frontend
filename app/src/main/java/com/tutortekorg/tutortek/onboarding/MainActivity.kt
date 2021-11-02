package com.tutortekorg.tutortek.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.tutortekorg.tutortek.LoginActivity
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: OnboardingAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindDataToUI()
        bindEventsToUI()
    }

    private fun bindEventsToUI() {
        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnboardingIndicator(position)
            }
        })
        binding.btnOnboardingAction.setOnClickListener { navigateOnboardScreen() }
    }

    private fun bindDataToUI() {
        setupOnboardingAdapter()
        binding.viewPagerOnboarding.adapter = adapter
        setupOnboardingIndicators()
        setCurrentOnboardingIndicator(0)
    }

    private fun navigateOnboardScreen() {
        if (isNotLastItem()) binding.viewPagerOnboarding.currentItem = binding.viewPagerOnboarding.currentItem + 1
        else {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun isNotLastItem() = binding.viewPagerOnboarding.currentItem + 1 < adapter.itemCount

    private fun setupOnboardingAdapter() {
        val list = mutableListOf<OnboardingItem>()

        list.add(
            OnboardingItem(
            R.mipmap.ic_books,
            resources.getString(R.string.title_learn),
            resources.getString(R.string.description_learn))
        )
        list.add(
            OnboardingItem(
            R.mipmap.ic_handshake,
            resources.getString(R.string.title_meet),
            resources.getString(R.string.description_meet))
        )
        list.add(
            OnboardingItem(
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
            setupIndicator(layoutParams)
        }
    }

    private fun setupIndicator(layoutParams: LinearLayout.LayoutParams) {
        val indicator = ImageView(applicationContext)
        indicator.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.onboarding_indicator_inactive
            )
        )
        indicator.layoutParams = layoutParams
        binding.layoutOnboardingIndicators.addView(indicator)
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        updateIndicators(index)
        updateButton(index)
    }

    private fun updateButton(index: Int) {
        if (index == adapter.itemCount - 1) binding.btnOnboardingAction.text =
            resources.getString(R.string.button_get_started)
        else binding.btnOnboardingAction.text = resources.getString(R.string.button_next)
    }

    private fun updateIndicators(index: Int) {
        val childCount = binding.layoutOnboardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView
            if (i == index) imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_active
                )
            )
            else imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_inactive
                )
            )
        }
    }
}
