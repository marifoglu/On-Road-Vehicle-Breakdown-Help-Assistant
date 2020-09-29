package com.darth.on_road_vehicle_breakdown_help.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darth.on_road_vehicle_breakdown_help.databinding.SssRowBinding
import com.darth.on_road_vehicle_breakdown_help.view.model.SSS

class SSSAdapter (private val sssList: List<SSS>) : RecyclerView.Adapter<SSSAdapter.SSSHolder>(){

    class SSSHolder(val binding: SssRowBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SSSAdapter.SSSHolder {
        val binding = SssRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SSSHolder(binding)
    }

    override fun getItemCount(): Int {
        return sssList.size
    }

    override fun onBindViewHolder(holder: SSSAdapter.SSSHolder, position: Int) {
        val expendables : SSS = sssList[position]
        holder.binding.ssName.text = expendables.sssName
        holder.binding.ssDescription.text = expendables.sssDescription

        val isExpendables : Boolean = sssList[position].expendable
        holder.binding.expandableLayout.visibility = if (isExpendables) View.VISIBLE else View.GONE

        holder.binding.ssRowLinearLayoutId.setOnClickListener{
            val data = sssList[position]
            data.expendable = !data.expendable

            notifyItemChanged(position)
        }
    }
}

