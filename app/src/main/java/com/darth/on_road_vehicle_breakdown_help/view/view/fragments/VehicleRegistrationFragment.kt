package com.darth.on_road_vehicle_breakdown_help.view.view.fragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import androidx.fragment.app.DialogFragment
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentVehicleRegistrationBinding
import com.darth.on_road_vehicle_breakdown_help.view.model.CarList
import com.darth.on_road_vehicle_breakdown_help.view.util.getJsonData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


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

        val jsonFileString = getJsonData(requireContext(), "carlist.json")
        if (jsonFileString != null) {
            Log.i("data", jsonFileString)
        } else {
            Log.e("data", "jsonFileString is null")
        }

        val gson = Gson()
        val listCarManufacturer = object : TypeToken<List<CarList>>() {}.type
        val vehicles: List<CarList>? = gson.fromJson(jsonFileString, listCarManufacturer)

        if (vehicles != null) {
            Log.i("data", "Deserialized vehicles: $vehicles")
            val vehicleManufacturerList : MutableList<String> = ArrayList()
            vehicleManufacturerList.add("Choose your vehicle:")
            for (vehicle in vehicles) {
                vehicleManufacturerList.add(vehicle.brand)
            }

            val vehicleManufacturerAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),
                R.layout.support_simple_spinner_dropdown_item, vehicleManufacturerList)

            val vehicleManufacturerSpinner = binding.vehicleManufacturer
            vehicleManufacturerSpinner.adapter = vehicleManufacturerAdapter
            vehicleManufacturerSpinner.setSelection(0)

            vehicleManufacturerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // give an error later!
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedManufacturer : String = vehicleManufacturerList[position]
                    val defaultItem: String = vehicleManufacturerList[0]

                    if (selectedManufacturer != defaultItem) {
                        Toast.makeText(requireContext(), "$selectedManufacturer selected!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Log.e("data", "Failed to deserialize vehicles")
        }

        // Vehicle Models List comes from JSON -----------------------------------------------
        val carListType = object : TypeToken<List<CarList>>() {}.type
        val carListAdapterList: List<CarList>? = gson.fromJson(jsonFileString, carListType)

        if (carListAdapterList != null) {
            // Log.i("data", "Deserialized carListAdapterList: $carListAdapterList")
            val carManufacturerList: MutableList<String> = mutableListOf()
            val defaultCarManufacturer = "Choose your vehicle manufacturer"
            carManufacturerList.add(defaultCarManufacturer)

            for (carListAdapter in carListAdapterList) {
                carManufacturerList.add(carListAdapter.brand)
            }

            val carManufacturerAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                carManufacturerList
            )
            val carManufacturerSpinner = binding.vehicleManufacturer
            carManufacturerSpinner.adapter = carManufacturerAdapter
            carManufacturerSpinner.setSelection(0)

            carManufacturerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Give an error message if needed
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        val selectedCarListAdapter = carListAdapterList[position - 1]
                        val carModelsList = selectedCarListAdapter.models
                        val carModelsAdapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            carModelsList
                        )
                        val carModelsSpinner = binding.vehicleModel
                        carModelsSpinner.adapter = carModelsAdapter
                        carModelsSpinner.setSelection(0)

                        carModelsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // Give an error message if needed...........
                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                if (position != 0) {
                                    val selectedCarModel = carModelsList[position - 1]
                                    Toast.makeText(
                                        requireContext(),
                                        "$selectedCarModel selected!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Log.e("data", "Failed to deserialize carListAdapterList")
        }

        // Vehicle Year list comes generated -------------------------------------------------------
        val vehicleYearList : MutableList<String> = ArrayList()
        vehicleYearList.add("Choose your vehicle year:")
        for (year in 2025 downTo 1950) {
            vehicleYearList.add(year.toString())
        }

        val vehicleYearAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),
            R.layout.support_simple_spinner_dropdown_item, vehicleYearList)

        val vehicleYearSpinner = binding.vehicleYear // year
        vehicleYearSpinner.adapter = vehicleYearAdapter
        vehicleYearSpinner.setSelection(0)

        vehicleYearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // give an error later!
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val selectedYear : String = vehicleYearList[position]
                val defaultItem: String = vehicleYearList[0]

                if (selectedYear != defaultItem) {
                    Toast.makeText(requireContext(), "$selectedYear selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //add --------------------------------------------------------------------------------------
        binding.vehicleRegisterButton.setOnClickListener {
            val id = UUID.randomUUID().toString()

            if (auth.currentUser != null) {
                val selectedManufacturer = binding.vehicleManufacturer.selectedItem.toString()
                val selectedModel = binding.vehicleModel.selectedItem.toString()
                val selectedYear = binding.vehicleYear.selectedItem.toString()

                val registerVehicle = hashMapOf<String, Any>()
                registerVehicle.put("id", id)
                registerVehicle.put("vehicleUser", auth.currentUser!!.email!!)
                registerVehicle.put("vehicleManufacturer", selectedManufacturer)
                registerVehicle.put("vehicleModel", selectedModel)
                registerVehicle.put("vehicleYear", selectedYear)
                registerVehicle.put("vehicleRegDate", Timestamp.now())

                firestore.collection("Vehicles").add(registerVehicle)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Your vehicle has been successfully added.",
                            Toast.LENGTH_LONG
                        ).show()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "An error occurred while adding your vehicle. Please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
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