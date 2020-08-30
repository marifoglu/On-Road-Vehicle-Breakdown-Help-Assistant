package com.darth.on_road_vehicle_breakdown_help.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val DEFAULT_ZOOM = 15f

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var trackBoolean: Boolean? = null

    private var selectedLatLng: LatLng? = null
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        trackBoolean = false

        getRescueData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(com.darth.on_road_vehicle_breakdown_help.R.id.map_container) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        return binding.root
    }

    private fun getRescueData() {
        db.collection("Rescue").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (document in documents) {
                            val rescueFBId = document.get("id") as String
                            val rescueFBRescueRequest = document.get("rescueRequest") as String
                            val rescueFBMap = document.get("rescueMap") as Map<*, *>
                            val rescueFBMapLatitude = rescueFBMap?.get("latitude") as Double?
                            val rescueFBMapLongitude = rescueFBMap?.get("longitude") as Double?
                            val rescueFBMapDirection = document.get("rescueDirection") as String
                            val rescueFBVehicle = document.get("rescueVehicle") as String
                            val rescueFBVehicleUser = document.get("vehicleUser") as String
                            val rescueFBDescribeProblem =
                                document.get("describeTheProblem") as String

                            //binding.rescueFBMapLatitude.text = rescueFBMapLatitude.toString()
                            //binding.rescueFBMapLongitude.text = rescueFBMapLongitude.toString()
                            binding.rescueFBMapDirection.text = rescueFBMapDirection
                            binding.rescueFBVehicle.text = rescueFBVehicle
                            binding.rescueFBVehicleUser.text = rescueFBVehicleUser
                            binding.rescueFBDescribeProblem.text = rescueFBDescribeProblem

                            // Update the map with the new latitude and longitude values
                            if (rescueFBMapLatitude != null && rescueFBMapLongitude != null) {
                                selectedLatLng = LatLng(rescueFBMapLatitude, rescueFBMapLongitude)
                                updateMap()
                            }
                        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in the selected location and move the camera
        mMap.addMarker(MarkerOptions().position(selectedLatLng!!).title("Selected Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, 15f))
    }

}