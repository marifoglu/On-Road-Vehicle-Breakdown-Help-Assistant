package com.darth.on_road_vehicle_breakdown_help.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentNotificationBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.ChatAdapter
import com.darth.on_road_vehicle_breakdown_help.view.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.provider.Settings
import android.util.Log

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    @SuppressLint("HardwareIds")
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

        userEmail = auth.currentUser?.email ?: ""

        mChats = mutableListOf()

        mId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        mAdapter = ChatAdapter(mChats, mId)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = mAdapter

        binding.btnSendMessage.setOnClickListener {
            val message = binding.sendMessage.text.toString()

            if (message.isNotEmpty()) {
                sendMessage(userEmail, "agency@raw.com", message)
            }

            binding.sendMessage.setText("")
        }

        messageCollectionRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "Error fetching chats: ${error.message}")
                return@addSnapshotListener
            }

            value?.let { snapshot ->
                val newChats = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)
                }.filter { chat ->
                    chat.sender == userEmail || chat.receiver == userEmail || chat.receiver == "agency@raw.com"
                }
                mChats.clear()
                mChats.addAll(newChats)
                mAdapter.notifyDataSetChanged()
                binding.chatRecyclerView.scrollToPosition(mChats.size - 1)
            }
        }

        getUserInformation()
    }

    private fun sendMessage(sender: String, receiver: String, content: String) {
        val messageMap = hashMapOf(
            "sender" to sender,
            "receiver" to receiver,
            "message" to content,
        )

        messageCollectionRef
            .add(messageMap)
            .addOnSuccessListener {
                // Message sent successfully
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error sending message: ${exception.message}")
                // Handle error
            }
    }

    private fun getUserInformation() {
        db.collection("UserInformation").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents

                        for (document in documents) {
                            val userNameAndSurname = document.get("nameAndSurname") as String
                            // Set the user name to TextView
                            userName = document.get("nameAndSurname") as String
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}