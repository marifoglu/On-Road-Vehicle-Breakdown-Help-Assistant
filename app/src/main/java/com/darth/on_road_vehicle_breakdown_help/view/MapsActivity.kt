package com.darth.on_road_vehicle_breakdown_help.view

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darth.on_road_vehicle_breakdown_help.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityMapsBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.Place
import com.darth.on_road_vehicle_breakdown_help.view.adapter.VehicleAdapter
import com.darth.on_road_vehicle_breakdown_help.view.model.Vehicle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

private const val DEFAULT_ZOOM = 15f

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var binding: ActivityMapsBinding

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var vehicleArrayList : ArrayList<Vehicle>

    private var trackBoolean : Boolean? = null

    private var selectedLatLng: LatLng? = null
    var selectedLatitude: Double? = null
    var selectedLongitude: Double? = null

    private var vehicleItem: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Obtain the SupportMapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        vehicleArrayList = ArrayList()

        getVehicles()
        registerLauncher()
        getProblem()

        sharedPreferences = this.getSharedPreferences("com.darth.on_road_vehicle_breakdown_help", MODE_PRIVATE)
        trackBoolean = false

        selectedLatitude = 0.0
        selectedLongitude = 0.0



    }



    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setOnMapLongClickListener(this)


        val intentData = intent.getStringExtra("data") // gets "new" or "update"

        if (intentData.equals("new")) {

            binding.saveRescueButton.visibility = View.VISIBLE
            binding.editRescueButton.visibility = View.GONE

            // Casting
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = LocationListener { location ->
                trackBoolean = sharedPreferences.getBoolean("tracking", false)

                if (!trackBoolean!!){
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16f))
                    sharedPreferences.edit().putBoolean("tracking",true).apply()
                }
            }


            if (ContextCompat.checkSelfPermission(this@MapsActivity,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Permission needed for location", Snackbar.LENGTH_SHORT).setAction("Give Permission"){
                        // Request permission
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                } else {
                    // Request permission
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                // Permission granted
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null){
                    val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,16f))
                }
                mMap.isMyLocationEnabled = true
            }

        }else if (intentData.equals("update")) { //-----------------------------------------------------------------------------------

            binding.saveRescueButton.visibility = View.GONE
            binding.editRescueButton.visibility = View.VISIBLE

            val rescueFBId = intent.getStringExtra("dataFB_ID")
            val rescueFBRescueRequest = intent.getStringExtra("dataFB_RescueRequest")
            val rescueFBMapLatitude = intent.getStringExtra("dataFB_MapLatitude")
            val rescueFBMapLongitude = intent.getStringExtra("dataFB_MapLongitude")
            var rescueFBMapDirection = intent.getStringExtra("dataFB_MapDirection")
            val rescueFBVehicle = intent.getStringExtra("dataFB_Vehicle")
            val rescueFBVehicleUser = intent.getStringExtra("dataFB_VehicleUser")
            val rescueFBDescribeProblem = intent.getStringExtra("dataFB_DescribeProblem")

            // Update the map with the new latitude and longitude values
            if (rescueFBMapLatitude != null && rescueFBMapLongitude != null) {
                selectedLatLng = LatLng(rescueFBMapLatitude.toDouble(), rescueFBMapLongitude.toDouble())
                updateMap()
            }

            binding.rescueDirectionText.setText(rescueFBMapDirection)

            val spinnerUpdateAdapter = binding.problemSpinner.adapter as ArrayAdapter<String>
            val spinnerUpdatePosition = spinnerUpdateAdapter.getPosition(rescueFBDescribeProblem)
            binding.problemSpinner.setSelection(spinnerUpdatePosition)

            println(rescueFBVehicleUser)
            val spinnerVehicleAdapter = binding.currentVehicleSpinner.adapter as ArrayAdapter<String>
            val spinnerVehicleUpdatePosition = spinnerVehicleAdapter.getPosition(rescueFBVehicle)
            binding.currentVehicleSpinner.setSelection(spinnerVehicleUpdatePosition)


            // Casting
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = LocationListener { location ->
                trackBoolean = sharedPreferences.getBoolean("tracking", false)

                if (!trackBoolean!!){
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16f))
                    sharedPreferences.edit().putBoolean("tracking",true).apply()
                }
            }


            if (ContextCompat.checkSelfPermission(this@MapsActivity,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Permission needed for location", Snackbar.LENGTH_SHORT).setAction("Give Permission"){
                        // Request permission
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                } else {
                    // Request permission
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                // Permission granted
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null){
                    val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,16f))
                }
                mMap.isMyLocationEnabled = true
            }

        }

    }

    private fun registerLauncher(){

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result){
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    // Permission granted
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null){
                        val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,16f))
                    }
                    mMap.isMyLocationEnabled = true
                }

            }else{
                // Permission denied
                Toast.makeText(this@MapsActivity, "Permission needed!", Toast.LENGTH_SHORT).show()

            }
        }

    // Save Button ---------------------------------------------------------------------------------
    binding.saveRescueButton.setOnClickListener {

        val rescueId = UUID.randomUUID().toString()

        if (auth.currentUser != null) {

            val rescueRequest = "1"

            val rescueDirection = binding.rescueDirectionText.text.toString()

            val rescueSpinner = binding.problemSpinner.selectedItem.toString()

            val rescueDescribeProblem = binding.describeProblem.text.toString()

            val googleMap = Place(selectedLatitude!!,selectedLongitude!!)

            val rescue = hashMapOf<String, Any>()
            rescue.put("id", rescueId)
            rescue.put("vehicleUser", auth.currentUser!!.email!!)
            rescue.put("rescueMap", googleMap)
            rescue.put("rescueDirection", rescueDirection)
            rescue.put("rescueVehicle", vehicleItem)
            rescue.put("rescueRequest", rescueRequest)


            if (rescueSpinner != "Other") {
                rescue.put("describeTheProblem", rescueSpinner)
            }else{
                rescue.put("describeTheProblem", rescueDescribeProblem)
            }

            db.collection("Rescue").add(rescue)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Rescue requested has been successfully added.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this,HomeFragment::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "An error occurred while adding your rescue request. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // I gonna fix it up!
        binding.editRescueButton.setOnClickListener {


            if (auth.currentUser != null) {


                val rescueDirection = binding.rescueDirectionText.text.toString()
                val rescueSpinner = binding.problemSpinner.selectedItem.toString()
                val rescueDescribeProblem = binding.describeProblem.text.toString()
                val googleMap = Place(selectedLatitude!!,selectedLongitude!!)

                }
            }

        binding.goBackRescueButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getProblem(){
        val problemList : MutableList<String> = ArrayList()
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

        val adapter : ArrayAdapter<String> = ArrayAdapter(this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, problemList)

        val problemSpinner = binding.problemSpinner
        problemSpinner.adapter = adapter
        problemSpinner.setSelection(0)

        problemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // give an error later!
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val item : String = problemList[position]
                val defaultItem: String = problemList[0]
                val otherItem: String = problemList[1]

                if (item == otherItem){
                    binding.describeProblem.visibility = View.VISIBLE
                }else{
                    binding.describeProblem.visibility = View.GONE
                }

                if (item != defaultItem) {
                    Toast.makeText(this@MapsActivity, "$item selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getVehicles() {

        // Data comes from Firebase to here!
        val vehicleList : MutableList<String> = ArrayList()
        vehicleList.add("Choose your vehicle")


        val vehicleAdapter : ArrayAdapter<String> = ArrayAdapter(this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, vehicleList)

        val vehicleSpinner = binding.currentVehicleSpinner
        vehicleSpinner.adapter = vehicleAdapter
        vehicleSpinner.setSelection(0)

        vehicleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                val vehicleItem : String = vehicleList[position]
                val defaultItem: String = vehicleList[0]

                if (vehicleItem != defaultItem) {
                    Toast.makeText(this@MapsActivity, "$vehicleItem selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }


        db.collection("Vehicles").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        for (document in documents) {
                            val vehicleId = document.get("id") as String
                            val vehicleManufacturerFB = document.get("vehicleManufacturer") as String
                            val vehicleModelFB = document.get("vehicleModel") as String
                            val vehicleYearFB = document.get("vehicleYear") as String

                            // Add each vehicle as a separate item to the vehicleList
                            val vehicleString = "$vehicleManufacturerFB $vehicleModelFB $vehicleYearFB"
                            vehicleList.add(vehicleString)
                        }
                        vehicleAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    private fun updateMap() {
        if (::mMap.isInitialized && selectedLatLng != null) {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(selectedLatLng!!))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, DEFAULT_ZOOM))
        }
    }


    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
    }
}