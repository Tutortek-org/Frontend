package com.tutortekorg.tutortek.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Topic

class TopicAdapter(private val topics: List<Topic>)
    : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    class TopicViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = view.findViewById(R.id.txt_topic_name)

        fun setTopicData(topic: Topic) {
            textTitle.text = topic.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder =
        TopicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_topic, parent, false)
        )

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.view.setOnClickListener {
            val bundle = bundleOf("topic" to topics[position])
            it.findNavController().navigate(R.id.action_topicListFragment_to_topicDetailsFragment, bundle)
        }
        holder.setTopicData(topics[position])
    }

    override fun getItemCount(): Int = topics.size
}
