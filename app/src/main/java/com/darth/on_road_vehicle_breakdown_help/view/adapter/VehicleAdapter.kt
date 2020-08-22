package com.darth.on_road_vehicle_breakdown_help.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.CarRowBinding
import com.darth.on_road_vehicle_breakdown_help.view.model.Vehicle

class VehicleAdapter(private val carList : ArrayList<Vehicle>) : RecyclerView.Adapter<VehicleAdapter.CarHolder>() {
    class CarHolder(val binding: CarRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val binding = CarRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarHolder(binding)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {
        holder.binding.rowVehicleManufacturer.text = carList.get(position).vehicleManufacturer
        holder.binding.rowVehicleModel.text = carList.get(position).vehicleModel
        holder.binding.rowVehicleYear.text = carList.get(position).vehicleYear

        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.light_grey)
        } else {
            holder.itemView.setBackgroundResource(R.color.medium_grey)
        }
    }
}