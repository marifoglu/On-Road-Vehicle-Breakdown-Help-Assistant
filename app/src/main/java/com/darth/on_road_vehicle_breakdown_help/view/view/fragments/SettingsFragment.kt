package com.darth.on_road_vehicle_breakdown_help.view.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentSettingsBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.VehicleAdapter
import com.darth.on_road_vehicle_breakdown_help.view.view.auth.LandingPage
import com.darth.on_road_vehicle_breakdown_help.view.model.User
import com.darth.on_road_vehicle_breakdown_help.view.model.Vehicle
import com.darth.on_road_vehicle_breakdown_help.view.util.SwipeToDelete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var vehicleArrayList: ArrayList<Vehicle>
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var userInformationList: ArrayList<User>

    private lateinit var xmlUserEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        userInformationList = ArrayList<User>()
        xmlUserEmail = binding.userEmailText

        vehicleArrayList = ArrayList()
        vehicleAdapter = VehicleAdapter(vehicleArrayList)
        binding.vehicleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.vehicleRecyclerView.adapter = vehicleAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserInformation()
        getVehicles()

        // Swipe to remove -------------------------------------------------------------------------
        val swipeToDeleteCallBack = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val vehicle = vehicleArrayList[position]

                vehicle.isLocallyDeleted = true

                // Delete the selected vehicle from Firebase
                db.collection("Vehicles").document(vehicle.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Vehicle deleted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        vehicleArrayList.removeAt(position)
                        vehicleAdapter.notifyItemRemoved(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete vehicle!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Reset the flag if deletion from Firebase fails
                        vehicle.isLocallyDeleted = false
                        vehicleAdapter.notifyItemChanged(position)
                    }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(binding.vehicleRecyclerView)

        // Add Vehicle -----------------------------------------------------------------------------
        binding.addVehicleText.setOnClickListener {
            val dialog = VehicleRegistrationFragment()
            dialog.show(childFragmentManager, "Add Vehicle")
        }


        // Logout ----------------------------------------------------------------------------------
        val logoutButton = binding.root.findViewById<Button>(R.id.button3)
        logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                logout(binding.root)
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                // Do nothing---
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun getVehicles() {
        val currentUserEmail = auth.currentUser?.email

        currentUserEmail?.let { email ->
            db.collection("Vehicles")
                .whereEqualTo("vehicleUser", email)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        if (value != null) {
                            if (!value.isEmpty) {
                                val documents = value.documents

                                // Update existing vehicles and remove deleted vehicle
                                for (document in documents) {
                                    val vehicle = document.toObject(Vehicle::class.java)
                                    vehicle?.let {
                                        it.id = document.id

                                        // Check if the vehicle is locally deleted
                                        val existingVehicleIndex = vehicleArrayList.indexOfFirst { existingVehicle ->
                                            existingVehicle.id == vehicle.id
                                        }

                                        if (existingVehicleIndex != -1) {
                                            val existingVehicle = vehicleArrayList[existingVehicleIndex]
                                            if (existingVehicle.isLocallyDeleted) {
                                                // Remove the locally deleted vehicle
                                                vehicleArrayList.removeAt(existingVehicleIndex)
                                            } else {
                                                // Update the existing vehicle with the updated data
                                                vehicleArrayList[existingVehicleIndex] = vehicle
                                            }
                                        } else {
                                            // Add the new vehicle to the list
                                            vehicleArrayList.add(vehicle)
                                        }
                                    }
                                }

                                vehicleAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
        }
    }

    private fun getUserInformation() {
        val currentUserEmail = auth.currentUser?.email

        currentUserEmail?.let { email ->
            db.collection("UserInformation")
                .whereEqualTo("email", email)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        if (value != null) {
                            if (!value.isEmpty) {
                                val documents = value.documents

                                for (document in documents) {
                                    val userId = document.getString("id")
                                    val userEmail = document.getString("email")
                                    val userHomeAddress = document.getString("homeAddress")
                                    val userNameAndSurname = document.getString("nameAndSurname")
                                    val userPhoneNumber = document.getString("phoneNumber")

                                    // Set the user information values
                                    binding.userEmailText.text = userEmail ?: ""
                                    binding.userHomeAddressText.text = userHomeAddress ?: ""
                                    binding.userNameText.text = userNameAndSurname ?: ""
                                    binding.userPhoneText.text = userPhoneNumber ?: ""

                                    val user = User(userId!!, userEmail!!, userHomeAddress!!, userNameAndSurname!!, userPhoneNumber!!)
                                    userInformationList.add(user)
                                }
                                vehicleAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
        }
    }


    private fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(requireContext(), LandingPage::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
