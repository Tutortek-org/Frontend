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
import com.tutortekorg.tutortek.data.LearningMaterial
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.data.Topic

class LearningMaterialAdapter(private val learningMaterials: List<LearningMaterial>,
                              private val navController: NavController,
                              private val dialog: BottomSheetDialog,
                              private val topic: Topic,
                              private val meeting: Meeting)
    : RecyclerView.Adapter<LearningMaterialAdapter.LearningMaterialViewHolder>() {

    class LearningMaterialViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = view.findViewById(R.id.txt_learning_material_name)

        fun setLearningMaterialData(learningMaterial: LearningMaterial) {
            textTitle.text = learningMaterial.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearningMaterialViewHolder =
        LearningMaterialViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_learning_material, parent, false)
        )

    override fun onBindViewHolder(holder: LearningMaterialViewHolder, position: Int) {
        holder.view.setOnClickListener {
            dialog.dismiss()
            val bundle = bundleOf("learningMaterial" to learningMaterials[position], "topic" to topic, "meeting" to meeting)
            navController.navigate(R.id.action_meetingDetailsFragment_to_learningMaterialsDetailsFragment, bundle)
        }
        holder.setLearningMaterialData(learningMaterials[position])
    }

    override fun getItemCount(): Int = learningMaterials.size
}
