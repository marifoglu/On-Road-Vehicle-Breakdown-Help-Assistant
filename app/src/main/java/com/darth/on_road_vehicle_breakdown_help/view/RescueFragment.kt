package com.darth.on_road_vehicle_breakdown_help.view

import android.content.ContentValues.TAG
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentRescueBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.Place
import com.darth.on_road_vehicle_breakdown_help.view.model.Vehicle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

private const val DEFAULT_ZOOM = 16f
class RescueFragment : Fragment(), OnMapReadyCallback ,GoogleMap.OnMapLongClickListener {

    private var _binding: FragmentRescueBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    private lateinit var mMap: GoogleMap
    var selectedLatitude: Double? = null
    var selectedLongitude: Double? = null
    private var trackBoolean: Boolean? = null
    private var selectedLatLng: LatLng? = null

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var vehicleArrayList: ArrayList<Vehicle>

    private var vehicleItem: String = ""
    private var vehicleItemUpdate: String = ""
    private var problemItemUpdate: String = ""
    private var newProblemSpinner: String = ""


    private var data: String? = null
    private var dataID: String? = null
    private var dataRescueRequest: String? = null
    private var rescueMapLatitude: String? = null
    private var rescueMapLongitude: String? = null
    private var dataMapDirection: String? = null
    private var dataVehicle: String? = null
    private var dataVehicleUser: String? = null
    private var dataDescribeProblem: String? = null

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
        _binding = FragmentRescueBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(com.darth.on_road_vehicle_breakdown_help.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkForData()
        registerLauncher()
        getProblem()
        getVehicles()
        onClickButtons()

        sharedPreferences = requireActivity().getSharedPreferences(
            "com.darth.on_road_vehicle_breakdown_help",
            AppCompatActivity.MODE_PRIVATE
        )
        trackBoolean = false

        selectedLatitude = 0.0
        selectedLongitude = 0.0

        vehicleArrayList = ArrayList()
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location ->
            trackBoolean = sharedPreferences.getBoolean("tracking", false)

            if (!trackBoolean!!) {
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM))
                sharedPreferences.edit().putBoolean("tracking", true).apply()
            }
        }
        permissionLauncher()
        // Add a marker in the selected location and move the camera
        if (selectedLatLng != null) {
            mMap.addMarker(MarkerOptions().position(selectedLatLng!!).title("Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, DEFAULT_ZOOM))
        }

    }

    private fun checkForData() {
        arguments?.let {
            data = it.getString("data") // "new" "update" "navbar"
            dataID = it.getString("dataID")
            dataRescueRequest = it.getString("dataRescueRequest")
            rescueMapLatitude = it.getString("dataMapLatitude")
            rescueMapLongitude = it.getString("dataMapLongitude")

            dataMapDirection = it.getString("dataMapDirection")
            dataVehicle = it.getString("dataVehicle")
            dataVehicleUser = it.getString("dataVehicleUser")
            dataDescribeProblem = it.getString("dataDescribeProblem")

        }


        db.collection("Rescue").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        Log.d("Data", "Collection is not empty")

                        if (data.equals("new")) {
                            createRescueFromHome()
                        } else if(data.equals("update")) {
                            updateRescue()
                        }else if (data.equals("navbar")){
                            navbarCheck()
                        }
                    }

                    else {
                        Log.d("Firebase", "Collection empty")
                        if (data.equals("new")) {
                            createRescueFromHome()
                        } else if(data.equals("update")) {
                            updateRescue()
                        }else if (data.equals("navbar")){
                            navbarCheck()

                        }
                    }
                } else {
                    Log.d("Firebase", "Value is null")
                    // create new?
                    println("Firebase Value is null")

                }
            }
        }
    }

    private fun updateRescue() {
        binding.createRescueRequest.visibility = View.GONE
        binding.rescueInformationText.visibility = View.GONE

        binding.map.visibility = View.VISIBLE
        binding.rescueDirectionLabel.visibility = View.VISIBLE
        binding.rescueDirectionText.visibility = View.VISIBLE
        binding.vehicleLabel.visibility = View.VISIBLE
        binding.currentVehicleSpinner.visibility = View.VISIBLE
        binding.problemDescription.visibility = View.VISIBLE
        binding.problemSpinner.visibility = View.VISIBLE
        binding.describeProblem.visibility = View.VISIBLE
        binding.saveRescueButton.visibility = View.VISIBLE
        binding.goBackRescueButton.visibility = View.VISIBLE
        binding.editRescueButton.visibility = View.VISIBLE


        binding.rescueDirectionText.setText(dataMapDirection)


        // Update the map with the new latitude and longitude values--------------------------------
        if (rescueMapLatitude != null && rescueMapLongitude != null) {
            selectedLatLng = LatLng(rescueMapLatitude!!.toDouble(), rescueMapLongitude!!.toDouble())
            updateMap(rescueMapLatitude, rescueMapLongitude)
        }

        //  Problem Data ---------------------------------------------------------------------------
        val problemListUpdate: MutableList<String> = ArrayList()
        problemListUpdate.add("Choose the problem:")
        problemListUpdate.add("Other")
        problemListUpdate.add("A flat or faulty battery")
        problemListUpdate.add("Alternator faults")
        problemListUpdate.add("Damaged tyres or wheel")
        problemListUpdate.add("Electrical problem")
        problemListUpdate.add("Keys and alarms")
        problemListUpdate.add("Misfuelling")
        problemListUpdate.add("Clutch cables on manual vehicles")
        problemListUpdate.add("Diesel Particulate Filter (DPF)")
        problemListUpdate.add("Starter motor")
        problemListUpdate.add("Overheating")
        problemListUpdate.add("Accident")

        val problemAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, problemListUpdate
        )

        val problemSpinnerUpdate = binding.problemSpinner
        problemSpinnerUpdate.adapter = problemAdapter

        val myDataIndexUpdate = problemListUpdate.indexOf(dataDescribeProblem)

        if (myDataIndexUpdate != -1) {
            problemSpinnerUpdate.setSelection(myDataIndexUpdate)

            problemSpinnerUpdate.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // give an error later!
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        problemItemUpdate = parent!!.getItemAtPosition(position) as String

                        val problemItemUpdate: String = problemListUpdate[position]
                        val defaultItemUpdate: String = problemListUpdate[0]
                        val defaultOtherItemUpdate: String = problemListUpdate[1]

                        if (problemItemUpdate != defaultItemUpdate) {
                            Toast.makeText(
                                requireContext(),
                                "$problemItemUpdate selected!",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (problemItemUpdate != defaultOtherItemUpdate) {
                                newProblemSpinner = problemItemUpdate
                                binding.describeProblem.visibility = View.GONE
                            } else {
                                binding.describeProblem.visibility = View.VISIBLE
                                binding.describeProblem.setText(dataDescribeProblem)
                            }
                        }
                    }
                }
        }


        //  Vehicle Data ---------------------------------------------------------------------------
        val vehicleListUpdate: MutableList<String> = ArrayList()

        val vehicleAdapterUpdate: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, vehicleListUpdate
        )

        val vehicleSpinnerUpdate = binding.currentVehicleSpinner
        vehicleSpinnerUpdate.adapter = vehicleAdapterUpdate

        vehicleSpinnerUpdate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // give an error later!
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (parent != null) {
                    vehicleItemUpdate = parent.getItemAtPosition(position) as String
                }
                val vehicleItemUpdate: String = vehicleListUpdate[position]
                val defaultItemUpdate: String = vehicleListUpdate[0]

                if (vehicleItemUpdate != defaultItemUpdate) {
                    Toast.makeText(
                        requireContext(),
                        "$vehicleItemUpdate selected!",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    // Get the index of myData in Firebase
                    val index = vehicleListUpdate.indexOf(dataVehicle)
                    if (index != -1) {
                        Log.d("Firebase", "The index of myData in Firebase is $index")
                    } else {
                        Log.d("Firebase", "myData not found in Firebase")
                    }
                }
            }
        }

        db.collection("Vehicles").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        for (document in documents) {
                            val vehicleManufacturer =
                                document.get("vehicleManufacturer") as String
                            val vehicleModel = document.get("vehicleModel") as String
                            val vehicleYear = document.get("vehicleYear") as String

                            // Add each vehicle as a separate item to the vehicleList
                            val vehicleString =
                                "$vehicleManufacturer $vehicleModel $vehicleYear"
                            vehicleListUpdate.add(vehicleString)

                            //------editRescueButton--------------------------------------------------------------------
                            binding.editRescueButton.setOnClickListener() {

                                db.collection("Rescue").addSnapshotListener { value, error ->
                                    if (error != null) {
                                        Toast.makeText(
                                            requireContext(),
                                            error.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else {
                                        if (value != null) {
                                            if (!value.isEmpty) {
                                                val documents2 = value.documents
                                                for (documentQuery in documents2) {
                                                    val documentId = documentQuery.id


                                                    val fragment = HomeFragment()
                                                    val bundle = Bundle()

                                                    // Data sent as a "save" ----------------------------------------------------------
                                                    bundle.putString("data", "save")

                                                    // Document ID --------------------------------------------------------------------
                                                    bundle.putString("documentID", documentId)

                                                    // LatLong ------------------------------------------------------------------------
                                                    if ((selectedLatitude != 0.0) && (selectedLongitude != 0.0)) {
                                                        bundle.putString(
                                                            "dataMapLatitude",
                                                            selectedLatitude!!.toDouble().toString()
                                                        )
                                                        bundle.putString(
                                                            "dataMapLongitude",
                                                            selectedLongitude!!.toDouble()
                                                                .toString()
                                                        )

                                                    } else {
                                                        bundle.putString(
                                                            "dataMapLatitude",
                                                            rescueMapLatitude!!.toDouble()
                                                                .toString()
                                                        )
                                                        bundle.putString(
                                                            "dataMapLongitude",
                                                            rescueMapLongitude!!.toDouble()
                                                                .toString()
                                                        )
                                                    }

                                                    // Direction -----------------------------------------------------------------------
                                                    val newDirectionValue =
                                                        binding.rescueDirectionText.text.toString()
                                                    bundle.putString(
                                                        "dataMapDirection",
                                                        newDirectionValue
                                                    )

                                                    // Vehicle -------------------------------------------------------------------------
                                                    bundle.putString(
                                                        "dataVehicle",
                                                        vehicleItemUpdate
                                                    )

                                                    // Describe Problem -- if Other selected--------------------------------------------
                                                    if (problemItemUpdate == "Other") {
                                                        bundle.putString(
                                                            "dataDescribeProblem",
                                                            binding.describeProblem.text.toString()
                                                        )
                                                    } else {
                                                        bundle.putString(
                                                            "dataDescribeProblem",
                                                            problemItemUpdate
                                                        )
                                                    }

                                                    fragment.arguments = bundle
                                                    val transaction =
                                                        requireActivity().supportFragmentManager.beginTransaction()
                                                    transaction.replace(
                                                        com.darth.on_road_vehicle_breakdown_help.R.id.frameLayoutID,
                                                        fragment
                                                    ).commit()

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    vehicleAdapterUpdate.notifyDataSetChanged()
                }
            }
        }
    }

    private fun navbarCheck(){
        db.collection("Rescue").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        for (document in documents) {
                            val rescueFBRescueRequest = document.get("rescueRequest") as String

                            if (rescueFBRescueRequest == "1") {
                                updateRescue()
                                println("update works")

                            } else {
                                createRescueFromHome()
                                println("create works")

                            }
                        }
                    }
                }
            }
        }
    }
    private fun createRescueFromHome(){
        binding.createRescueRequest.visibility = View.GONE
        binding.rescueInformationText.visibility = View.GONE
        binding.editRescueButton.visibility = View.GONE

        binding.map.visibility = View.VISIBLE
        binding.rescueDirectionLabel.visibility = View.VISIBLE
        binding.rescueDirectionText.visibility = View.VISIBLE
        binding.vehicleLabel.visibility = View.VISIBLE
        binding.currentVehicleSpinner.visibility = View.VISIBLE
        binding.problemDescription.visibility = View.VISIBLE
        binding.problemSpinner.visibility = View.VISIBLE
        binding.describeProblem.visibility = View.VISIBLE
        binding.saveRescueButton.visibility = View.VISIBLE
        binding.goBackRescueButton.visibility = View.VISIBLE

    }

    private fun onClickButtons() {
        //------goBackRescueButton------------------------------------------------------------------
        binding.goBackRescueButton.setOnClickListener {
            val fragment = HomeFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(
                com.darth.on_road_vehicle_breakdown_help.R.id.frameLayoutID,
                fragment
            )?.commit()
        }

        //------saveRescueButton--------------------------------------------------------------------
        binding.saveRescueButton.setOnClickListener {

            val rescueRequest = "1"

            val rescueId = UUID.randomUUID().toString()
            val rescueDirection = binding.rescueDirectionText.text.toString()
            val rescueSpinner = binding.problemSpinner.selectedItem.toString()
            val rescueDescribeProblem = binding.describeProblem.text.toString()
            val googleMap = Place(selectedLatitude!!, selectedLongitude!!)

            val rescue = hashMapOf<String, Any>()
            rescue["id"] = rescueId
            rescue["vehicleUser"] = auth.currentUser!!.email!!
            rescue["rescueMap"] = googleMap
            rescue["rescueDirection"] = rescueDirection
            rescue["rescueVehicle"] = vehicleItem
            rescue["rescueRequest"] = rescueRequest


            if (rescueSpinner != "Other") {
                rescue["describeTheProblem"] = rescueSpinner
            } else {
                rescue["describeTheProblem"] = rescueDescribeProblem
            }

            db.collection("Rescue").add(rescue)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Rescue requested has been successfully added.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val fragment = HomeFragment()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(
                        com.darth.on_road_vehicle_breakdown_help.R.id.frameLayoutID,
                        fragment
                    )?.commit()

                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "An error occurred while adding your rescue request. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        //------createRescueRequest-----------------------------------------------------------------
        binding.createRescueRequest.setOnClickListener {

            // Hide and Show layout
            binding.createRescueRequest.visibility = View.GONE
            binding.rescueInformationText.visibility = View.GONE
            binding.editRescueButton.visibility = View.GONE
            binding.describeProblem.visibility = View.GONE

            binding.map.visibility = View.VISIBLE
            binding.rescueDirectionLabel.visibility = View.VISIBLE
            binding.rescueDirectionText.visibility = View.VISIBLE
            binding.vehicleLabel.visibility = View.VISIBLE
            binding.currentVehicleSpinner.visibility = View.VISIBLE
            binding.problemDescription.visibility = View.VISIBLE
            binding.problemSpinner.visibility = View.VISIBLE
            binding.saveRescueButton.visibility = View.VISIBLE
            binding.goBackRescueButton.visibility = View.VISIBLE

        }
    }

    private fun permissionLauncher() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Snackbar.make(binding.root, "Permission needed for location", Snackbar.LENGTH_SHORT)
                    .setAction("Give Permission") {
                        // Request permission
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
            } else {
                // Request permission
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            // Permission granted
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 16f))
            }
            mMap.isMyLocationEnabled = true
        }
    }

    private fun registerLauncher() {

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Permission granted
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0f,
                            locationListener
                        )
                        val lastLocation =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation =
                                LatLng(lastLocation.latitude, lastLocation.longitude)
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    lastUserLocation,
                                    16f
                                )
                            )
                        }
                        mMap.isMyLocationEnabled = true
                    }

                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Permission needed!", Toast.LENGTH_SHORT)
                        .show()

                }
            }
    }

    private fun getVehicles() {

        // Data comes from Firebase to here!
        val vehicleList: MutableList<String> = ArrayList()
        vehicleList.add("Choose your vehicle")


        val vehicleAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, vehicleList
        )

        val vehicleSpinner = binding.currentVehicleSpinner
        vehicleSpinner.adapter = vehicleAdapter
        vehicleSpinner.setSelection(0)

        vehicleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // give an error later!
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (parent != null) {
                    vehicleItem = parent.getItemAtPosition(position) as String
                }
                val vehicleItem: String = vehicleList[position]
                val defaultItem: String = vehicleList[0]

                if (vehicleItem != defaultItem) {
                    Toast.makeText(requireContext(), "$vehicleItem selected!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        db.collection("Vehicles").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        for (document in documents) {
                            val vehicleManufacturerFB =
                                document.get("vehicleManufacturer") as String
                            val vehicleModelFB = document.get("vehicleModel") as String
                            val vehicleYearFB = document.get("vehicleYear") as String

                            // Add each vehicle as a separate item to the vehicleList
                            val vehicleString =
                                "$vehicleManufacturerFB $vehicleModelFB $vehicleYearFB"
                            vehicleList.add(vehicleString)
                        }
                        vehicleAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getProblem() {
        val problemList: MutableList<String> = ArrayList()
        problemList.add("Choose the problem:")
        problemList.add("Other")
        problemList.add("A flat or faulty battery")
        problemList.add("Alternator faults")
        problemList.add("Damaged tyres or wheel")
        problemList.add("Electrical problem")
        problemList.add("Keys and alarms")
        problemList.add("Misfuelling")
        problemList.add("Clutch cables on manual vehicles")
        problemList.add("Diesel Particulate Filter (DPF)")
        problemList.add("Starter motor")
        problemList.add("Overheating")
        problemList.add("Accident")

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, problemList
        )

        val problemSpinner = binding.problemSpinner
        problemSpinner.adapter = adapter
        problemSpinner.setSelection(0)

        problemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // give an error later!
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val item: String = problemList[position]
                val defaultItem: String = problemList[0]
                val otherItem: String = problemList[1]

                if (item == otherItem) {
                    binding.describeProblem.visibility = View.VISIBLE
                } else {
                    binding.describeProblem.visibility = View.GONE
                }

                if (item != defaultItem) {
                    Toast.makeText(requireContext(), "$item selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateMap(latitude: String?, longitude: String?) {
        if (latitude.isNullOrEmpty() || longitude.isNullOrEmpty()) {
            return
        }

        try {
            val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
            if (::mMap.isInitialized) {
                mMap.addMarker(MarkerOptions().position(latLng))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




