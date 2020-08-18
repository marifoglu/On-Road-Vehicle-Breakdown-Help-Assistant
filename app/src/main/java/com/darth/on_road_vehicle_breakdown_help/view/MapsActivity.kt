package com.darth.on_road_vehicle_breakdown_help.view

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
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
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var binding: ActivityMapsBinding

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences

    private var trackBoolean : Boolean? = null

    var selectedLatitude: Double? = null
    var selectedLongitude: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        sharedPreferences = this.getSharedPreferences("com.darth.on_road_vehicle_breakdown_help", MODE_PRIVATE)
        trackBoolean = false

        selectedLatitude = 0.0
        selectedLongitude = 0.0

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


    }




    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setOnMapLongClickListener(this)

        // "key" -> update and create comes!
        val intent = intent
        val info = intent.getStringExtra("key")

        if (info == "create") {

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
                    Snackbar.make(binding.root,"Permission needed for location", Snackbar.LENGTH_LONG).setAction("Give Permission"){
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

        }else{

            mMap.clear()

            binding.saveRescueButton.visibility = View.GONE
            binding.editRescueButton.visibility = View.VISIBLE

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
                    Snackbar.make(binding.root,"Permission needed for location", Snackbar.LENGTH_LONG).setAction("Give Permission"){
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
                Toast.makeText(this@MapsActivity, "Permission needed!", Toast.LENGTH_LONG).show()

            }
        }

//        binding.saveRescueButton.setOnClickListener {
//
//        }
//
//
//        binding.editRescueButton.setOnClickListener {
//
//        }


//
    }


    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude = p0.latitude
        selectedLatitude = p0.longitude
    }
}