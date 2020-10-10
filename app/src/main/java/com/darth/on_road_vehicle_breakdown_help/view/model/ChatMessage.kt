package com.darth.on_road_vehicle_breakdown_help.view.model

import java.util.Date
data class ChatMessage(
    val useremail: String,
    val receiveremail: String,
    val usermessage: String?,
    val usermessagetime: Date?,
    val senderType: String
)