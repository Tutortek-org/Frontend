package com.tutortekorg.tutortek.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.LearningMaterial

class LearningMaterialAdapter(private val learningMaterials: List<LearningMaterial>)
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
        holder.setLearningMaterialData(learningMaterials[position])
    }

    override fun getItemCount(): Int = learningMaterials.size
}
