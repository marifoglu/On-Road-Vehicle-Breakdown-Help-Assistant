package com.darth.on_road_vehicle_breakdown_help.view.view.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentHomeBinding
import com.darth.on_road_vehicle_breakdown_help.view.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val userCollectionRef = Firebase.firestore.collection("UserInformation")
    private val rescueCollectionRef = Firebase.firestore.collection("Rescue")
    private val vehicleCollectionRef = Firebase.firestore.collection("Vehicle")

    private var dataID: String? = null
    private var data: String? = null
    private var datadocumentId: String? = null
    private var dataLatitude: String? = null
    private var dataLongitude: String? = null
    private var dataNewDirection: String? = null
    private var dataNewVehicle: String? = null
    private var dataProblem: String? = null
    var googleMap: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val userEmail = currentUser?.email


        binding.sssId.setOnClickListener {
            val dialog = SSSFragment()
            dialog.show(childFragmentManager, "FAQ's")
        }

        lifecycleScope.launch {
            delay(300)
            getRescueData()
        }
        getUserInformation()
        deleteRequest()

    }

    private fun getBinding(): FragmentHomeBinding {
        return binding
    }

    private fun bundles() {
        arguments?.let {
            data = it.getString("data") // "recreate"
        }
    }

    private suspend fun getRescueData() {
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email

        val query = db.collection("Rescue")
            .whereEqualTo("rescueVehicleUser", currentUserEmail)
            .whereEqualTo("rescueRequest", "1")

        query.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            querySnapshot?.let { snapshot ->
                if (!snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val rescueFBId = document.id
                        val rescueFBRescueRequest = document.getString("rescueRequest")
                        val rescueFBMap = document.get("rescueMap") as? Map<*, *>
                        val rescueFBMapLatitude = rescueFBMap?.get("latitude") as? Double
                        val rescueFBMapLongitude = rescueFBMap?.get("longitude") as? Double
                        val rescueFBMapDirection = document.getString("rescueDirection")
                        val rescueFBVehicle = document.getString("rescueVehicle")
                        val rescueFBVehicleUser = document.getString("rescueVehicleUser")
                        val rescueFBDescribeProblem = document.getString("rescueDescribeProblem")

                        binding.addARescueRequest.visibility = View.GONE
                        binding.currentRescueRequest.visibility = View.VISIBLE

                        val message = "You have a currently road assistance request."
                        val startIndex = message.indexOf("currently")
                        val endIndex = startIndex + "currently".length
                        val spannable = SpannableString(message)
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            startIndex,
                            endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        binding.currentRescueRequest.text = spannable

                        binding.deleteRescueRequest.visibility = View.VISIBLE

                        binding.linearLayoutId.setOnClickListener {
                            val fragment = RescueFragment()
                            val bundle = Bundle()
                            bundle.putString("data", "show")
                            bundle.putString("dataID", rescueFBId)
                            bundle.putString("dataRescueRequest", rescueFBRescueRequest)
                            bundle.putString("dataMapLatitude", rescueFBMapLatitude?.toString())
                            bundle.putString("dataMapLongitude", rescueFBMapLongitude?.toString())
                            bundle.putString("dataMapDirection", rescueFBMapDirection)
                            bundle.putString("dataVehicle", rescueFBVehicle)
                            bundle.putString("dataVehicleUser", rescueFBVehicleUser)
                            bundle.putString("dataDescribeProblem", rescueFBDescribeProblem)
                            fragment.arguments = bundle
                            val transaction =
                                requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frameLayoutID, fragment).commit()
                        }
                    }
                } else {
                    val message = "You do not have a currently road assistance request."
                    val startIndex = message.indexOf("do not have")
                    val endIndex = startIndex + "do not have".length

                    val spannable = SpannableString(message)
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    binding.currentRescueRequest.text = spannable
                    binding.addARescueRequest.visibility = View.VISIBLE
                    binding.deleteRescueRequest.visibility = View.GONE

                    binding.addARescueRequest.setOnClickListener {
                        val fragment = RescueFragment()
                        val bundle = Bundle()
                        bundle.putString("data", "create")
                        fragment.arguments = bundle
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.frameLayoutID, fragment)?.commit()
                    }
                }
            }
        }
    }

    private fun getUserInformation() {
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email

        val query = db.collection("UserInformation")
            .whereEqualTo("email", currentUserEmail)

        query.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            querySnapshot?.let { snapshot ->
                if (!snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val userNameAndSurname = document.getString("nameAndSurname")
                        binding.rescueFBVehicleUser.text = userNameAndSurname
                    }
                }
            }
        }
    }


    private fun deleteRequest() {
        binding.deleteRescueRequest.setOnClickListener {
            val currentUser = auth.currentUser
            val userEmail = currentUser?.email

            if (userEmail != null) {
                db.collection("Rescue")
                    .whereEqualTo("rescueVehicleUser", userEmail)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            if (value != null) {
                                if (!value.isEmpty) {
                                    val documents = value.documents
                                    for (document in documents) {
                                        val documentId = document.id

                                        if (isAdded) {
                                            val builder = AlertDialog.Builder(requireContext())
                                            builder.setTitle("Delete")
                                            builder.setMessage("Are you sure you want to delete the road assistance request?")
                                            builder.setPositiveButton("Yes") { _, _ ->
                                                db.collection("Rescue")
                                                    .document(documentId)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        Log.d(TAG, "Document deleted successfully")
                                                        val fragment = HomeFragment()
                                                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                                        transaction.replace(R.id.frameLayoutID, fragment)
                                                            .commit()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(TAG, "Error deleting document", e)
                                                    }
                                            }
                                            builder.setNegativeButton("No") { _, _ ->
                                                // Do nothing
                                            }
                                            builder.create().show()
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }
}