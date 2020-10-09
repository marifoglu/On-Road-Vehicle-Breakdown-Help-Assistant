package com.darth.on_road_vehicle_breakdown_help.view.adapter

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.darth.on_road_vehicle_breakdown_help.R
//import com.darth.on_road_vehicle_breakdown_help.view.model.ChatMessage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.darth.on_road_vehicle_breakdown_help.view.model.ChatMessage
import com.darth.on_road_vehicle_breakdown_help.R

class ChatAdapter(private val chatMessages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        // Inflate the appropriate layout based on the view type
        val itemView: View = when (viewType) {
            VIEW_TYPE_USER -> layoutInflater.inflate(R.layout.item_right, parent, false)
            VIEW_TYPE_AGENCY -> layoutInflater.inflate(R.layout.item_left, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = chatMessages[position]

        // Determine the view type based on the senderType field of the ChatMessage
        return when (chatMessage.senderType) {
            "customer" -> VIEW_TYPE_USER
            "agency" -> VIEW_TYPE_AGENCY
            else -> throw IllegalArgumentException("Invalid senderType")
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declare your ViewHolder views here
        private val userMessageTextView: TextView = itemView.findViewById(R.id.userMessageTextView)

        fun bind(chatMessage: ChatMessage) {
            // Bind the data to the views in the ViewHolder
            userMessageTextView.text = chatMessage.usermessage
        }
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AGENCY = 2
    }
}
