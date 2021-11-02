package com.tutortekorg.tutortek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OnboardingAdapter(private val onboardingItems: List<OnboardingItem>)
    : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var textTitle: TextView = view.findViewById(R.id.txt_title)
        private var textDescription: TextView = view.findViewById(R.id.txt_description)
        private var imageOnboarding: ImageView = view.findViewById(R.id.img_onboarding)

        fun setOnboardingData(onboardingItem: OnboardingItem) {
            textTitle.text = onboardingItem.title
            textDescription.text = onboardingItem.description
            imageOnboarding.setImageResource(onboardingItem.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder =
        OnboardingViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_onboarding, parent, false)
        )

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.setOnboardingData(onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size
}
