package com.darth.on_road_vehicle_breakdown_help.view.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentNotificationBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.ChatAdapter
import com.darth.on_road_vehicle_breakdown_help.view.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import java.util.UUID

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var mChats: MutableList<ChatMessage> = mutableListOf()
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mId: String

    private var userName: String = ""
    private lateinit var userEmail: String


    private lateinit var recyclerViewAdapter: ChatAdapter

    private val chatMessages: MutableList<ChatMessage> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewAdapter = ChatAdapter(chatMessages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = recyclerViewAdapter

        getData()

        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }
        /*
        binding.btnAgencyMessage.setOnClickListener {
            sendMessageAgency()
        }
         */
    }

    private fun sendMessage() {
        val messageToSend = binding.sendMessage.text.toString()

        val uuid = UUID.randomUUID()
        val uuidString = uuid.toString()

        val user: FirebaseUser? = auth.currentUser
        val userEmail = user?.email.toString()

        val senderType = "customer" // Set the sender type as "customer"

        val data = hashMapOf(
            "usermessage" to messageToSend,
            "useremail" to userEmail,
            "receiveremail" to "agency@raw.com", // define contact email here for the agency
           "usermessagetime" to FieldValue.serverTimestamp(),
           "senderType" to senderType
        )

        db.collection("Messages").document(uuidString).set(data)
            .addOnSuccessListener {
                binding.sendMessage.setText("")
                Toast.makeText(requireContext(), "Message sent successfully", Toast.LENGTH_SHORT).show()
                getData() // Retrieve updated data after sending the message
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Agency Test Section
    private fun sendMessageAgency() {
        val messageToSend = binding.sendMessage.text.toString()

        val uuid = UUID.randomUUID()
        val uuidString = uuid.toString()

        val user: FirebaseUser? = auth.currentUser
        val userEmail = user?.email.toString()

        val senderType = "agency" // Set the sender type as "customer"

        val data = hashMapOf(
            "usermessage" to messageToSend,
            "useremail" to userEmail,
            "receiveremail" to "raw@raw.com", // Add the receiver's email
            "usermessagetime" to FieldValue.serverTimestamp(),
            "senderType" to senderType // Add the senderType field
        )
        db.collection("Messages").document(uuidString).set(data)
            .addOnSuccessListener {
                binding.sendMessage.setText("")
                Toast.makeText(requireContext(), "Message sent successfully", Toast.LENGTH_SHORT).show()
                getData() // Retrieve updated data after sending the message
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getData() {
        val user: FirebaseUser? = auth.currentUser
        val userEmail = user?.email.toString()

        val newReference = db.collection("Messages")
        val query: Query = newReference
            .orderBy("usermessagetime", Query.Direction.ASCENDING)
            .whereEqualTo("useremail", userEmail)
            .whereIn("receiveremail", listOf(userEmail, "agency@raw.com"))
        query.addSnapshotListener { value, error ->
            chatMessages.clear()
            if (value != null) {
                for (document in value) {
                    val senderType = document.getString("senderType")
                    val useremail = document.getString("useremail")
                    val receiveremail = document.getString("receiveremail")
                    val usermessage = document.getString("usermessage")
                    val usermessagetime = document.getDate("usermessagetime")

                    chatMessages.add(ChatMessage(useremail!!, receiveremail!!, usermessage, usermessagetime, senderType!!))
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}