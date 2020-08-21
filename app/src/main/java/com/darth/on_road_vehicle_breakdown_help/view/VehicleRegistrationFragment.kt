package com.darth.on_road_vehicle_breakdown_help.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityRegistrationBinding
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentRescueBinding
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentVehicleRegistrationBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class VehicleRegistrationFragment : DialogFragment() {

    private var _binding: FragmentVehicleRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleRegistrationBinding.inflate(inflater, container, false)

        //add

        binding.vehicleRegisterButton.setOnClickListener {
            val vehicleManufacturer = binding.vehicleManufacturer.text.toString()
            val vehicleModel = binding.vehicleModel.text.toString()
            val vehicleYear = binding.vehicleYear.text.toString()

            if (vehicleManufacturer.isNotEmpty() && vehicleModel.isNotEmpty() && vehicleYear.isNotEmpty()) {
                if (auth.currentUser != null) {







                    val registerVehicle = hashMapOf<String, Any>()

                    registerVehicle.put("vehicleUser", auth.currentUser!!.email!!)
                    registerVehicle.put("vehicleManufacturer", vehicleManufacturer)
                    registerVehicle.put("vehicleModel", vehicleModel)
                    registerVehicle.put("vehicleYear", vehicleYear)
                    registerVehicle.put("vehicleRegDate", Timestamp.now())

                    firestore.collection("Vehicles").add(registerVehicle)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Your vehicle successfully added.",
                                Toast.LENGTH_LONG
                            ).show()
                            dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }

        binding.vehicleCancelButton.setOnClickListener {
            dismiss()
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/*
        // Data comes from Firebase to here!
        val list : MutableList<String> = ArrayList()
        list.add("Choose your vehicle:")
        list.add("BMW")
        list.add("Volkswagen")
        list.add("Volvo")
        list.add("Audi")

        val adapter : ArrayAdapter<String> = ArrayAdapter(this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)

        val mySpinner = binding.vehicleSpinner
        mySpinner.adapter = adapter
        mySpinner.setSelection(0)

        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // give an error later!
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val item : String = list[position]
                val defaultItem: String = list[0]

                if (item != defaultItem) {
                    Toast.makeText(this@MapsActivity, "$item selected!", Toast.LENGTH_LONG).show()
                }
            }
        }
 */
