package com.darth.on_road_vehicle_breakdown_help.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darth.on_road_vehicle_breakdown_help.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val messageCollectionRef = Firebase.firestore.collection("Messages")

    private var mChats: MutableList<ChatMessage> = mutableListOf()
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mId: String

    private var userName: String = ""
    private lateinit var userEmail: String



    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: ChatAdapter
    private lateinit var messageText: EditText

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
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.btnAgencyMessage.setOnClickListener {
            sendMessageAgency()
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
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

    private fun sendMessageAgency() {
        val messageToSend = binding.sendMessage.text.toString()

        val uuid = UUID.randomUUID()
        val uuidString = uuid.toString()

        val user: FirebaseUser? = auth.currentUser
        val userEmail = user?.email.toString()

        val senderType = "agency" // Set the sender type as "customer"

        val data = hashMapOf(
            "usermessage" to messageToSend,
            "useremail" to "agency@agency.com",
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
        val agencyTag = "agency" // Modify with the agency tag/nickname

        val newReference = db.collection("Messages")
        val query: Query = newReference
            .orderBy("usermessagetime", Query.Direction.ASCENDING)
        query.addSnapshotListener { value, error ->
            chatMessages.clear()
            if (value != null) {
                for (document in value) {
                    val senderType = document.getString("senderType")
                    val useremail = document.getString("useremail")
                    val usermessage = document.getString("usermessage")
                    val usermessagetime = document.getDate("usermessagetime")

                    chatMessages.add(ChatMessage(useremail!!, usermessage, usermessagetime, senderType!!))

                }
                recyclerViewAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Error getting data: ${error?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}