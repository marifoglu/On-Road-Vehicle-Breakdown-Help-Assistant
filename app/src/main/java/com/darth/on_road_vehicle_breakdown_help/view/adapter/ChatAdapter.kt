package com.darth.on_road_vehicle_breakdown_help.view.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.view.model.ChatMessage

class ChatAdapter(private val dataSet: List<ChatMessage>, private val id: String) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    companion object {
        private const val CHAT_END = 1
        private const val CHAT_START = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = if (viewType == CHAT_END) {
            layoutInflater.inflate(R.layout.item_right, parent, false)
        } else {
            layoutInflater.inflate(R.layout.item_left, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].sender == "agency@raw.com") {
            CHAT_START
        } else {
            CHAT_END
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = dataSet[position]
        holder.textView.text = chat.message

        if (getItemViewType(position) == CHAT_START) {
            // Set gravity to the left for "Agency" messages
            holder.textView.gravity = Gravity.START
        } else {
            // Set gravity to the right for user messages
            holder.textView.gravity = Gravity.END
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.contentMessage)
    }
}