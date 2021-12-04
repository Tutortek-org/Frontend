package com.tutortekorg.tutortek.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Meeting

class MeetingAdapter(private val meetings: List<Meeting>)
    : RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    class MeetingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = view.findViewById(R.id.txt_meeting_name)
        private val textDate: TextView = view.findViewById(R.id.txt_meeting_date)

        fun setTopicData(meeting: Meeting) {
            textTitle.text = meeting.name
            textDate.text = meeting.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder =
        MeetingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_meeting, parent, false)
        )

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.setTopicData(meetings[position])
    }

    override fun getItemCount(): Int = meetings.size
}
