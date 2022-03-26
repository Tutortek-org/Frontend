package com.tutortekorg.tutortek.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic

class PersonalMeetingAdapter(private val meetings: List<Meeting>,
                             private val navController: NavController,
                             private val topics: List<Topic>
) : RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    class MeetingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = view.findViewById(R.id.txt_meeting_name)
        private val textDate: TextView = view.findViewById(R.id.txt_meeting_date)

        fun setMeetingData(meeting: Meeting) {
            textTitle.text = meeting.name
            textDate.text = meeting.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingAdapter.MeetingViewHolder =
        MeetingAdapter.MeetingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_meeting, parent, false)
        )

    override fun onBindViewHolder(holder: MeetingAdapter.MeetingViewHolder, position: Int) {
        holder.view.setOnClickListener {
            val bundle = bundleOf("meeting" to meetings[position], "topic" to topics[position])
            navController.navigate(R.id.action_topicDetailsFragment_to_meetingDetailsFragment, bundle)
        }
        holder.setMeetingData(meetings[position])
    }

    override fun getItemCount(): Int = meetings.size
}
