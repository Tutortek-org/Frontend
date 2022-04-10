package com.tutortekorg.tutortek.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.UserProfile

class UserProfilesAdapter(private val userProfiles: List<UserProfile>,
                          private val context: Context,
                          private val navController: NavController,
                          private val dialog: BottomSheetDialog
) : RecyclerView.Adapter<UserProfilesAdapter.AttendantsViewHolder>() {

    class AttendantsViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.txt_attendant_name)

        fun setAttendantData(userProfile: UserProfile) {
            textName.text = context.getString(R.string.profile_full_name, userProfile.firstName, userProfile.lastName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendantsViewHolder =
        AttendantsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_attendant, parent, false),
            context
        )

    override fun onBindViewHolder(holder: AttendantsViewHolder, position: Int) {
        holder.view.setOnClickListener {
            dialog.dismiss()
            val bundle = bundleOf("userProfile" to userProfiles[position])
            navController.navigate(R.id.action_meetingDetailsFragment_to_foreignProfileFragment, bundle)
        }
        holder.setAttendantData(userProfiles[position])
    }

    override fun getItemCount() = userProfiles.size
}
