package com.darth.on_road_vehicle_breakdown_help.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentHomeBinding
import com.darth.on_road_vehicle_breakdown_help.view.login.LandingPage
import com.darth.on_road_vehicle_breakdown_help.view.model.User
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
        getUserInformation()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)



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
                            val rescueFBDescribeProblem = document.get("describeTheProblem") as String

                            // If user has a rescue request-----------------------------------------
                            if (rescueFBRescueRequest.equals("1")){

                                binding.addARescueRequest.visibility = View.GONE

                                binding.currentRescueRequest.visibility = View.VISIBLE
                                binding.currentRescueRequest.text = "You have a currently road assistance request."

                                binding.updateRescueRequest.setOnClickListener {

                                    val fragment = RescueFragment()
                                    val bundle = Bundle()
                                    bundle.putString("data", "update")
                                    bundle.putString("dataID", rescueFBId)
                                    bundle.putString("dataRescueRequest", rescueFBRescueRequest)
                                    if (rescueFBMapLatitude != null) {
                                        bundle.putString("dataMapLatitude",
                                            rescueFBMapLatitude.toDouble().toString()
                                        )
                                    }
                                    if (rescueFBMapLongitude != null) {
                                        bundle.putString("dataMapLongitude", rescueFBMapLongitude.toDouble().toString())
                                    }
                                    bundle.putString("dataMapDirection", rescueFBMapDirection)
                                    bundle.putString("dataVehicle", rescueFBVehicle)
                                    bundle.putString("dataVehicleUser", rescueFBVehicleUser)
                                    bundle.putString("dataDescribeProblem", rescueFBDescribeProblem)

                                    fragment.arguments = bundle
                                    val transaction = fragmentManager?.beginTransaction()
                                    transaction?.replace(R.id.frameLayoutID, fragment)?.commit()

                                }


                                // Update the map with the new latitude and longitude values
                                if (rescueFBMapLatitude != null && rescueFBMapLongitude != null) {
                                    selectedLatLng = LatLng(rescueFBMapLatitude, rescueFBMapLongitude)
                                    updateMap()
                                }
                            }
                        }
                    }else{
                        ifDontHaveRequest()
                    }
                }
            }
        }
    }

    private fun ifDontHaveRequest(){

        binding.currentRescueRequest.text = "You don't have a currently road assistance request."

        binding.addARescueRequest.visibility = View.VISIBLE
        binding.updateRescueRequest.visibility = View.GONE

        binding.addARescueRequest.setOnClickListener {
            val fragment = RescueFragment()
            val bundle = Bundle()
            bundle.putString("data", "new")
            fragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayoutID, fragment)?.commit()
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
                            binding.rescueFBVehicleUser.text = userNameAndSurname
                        }
                    }
                }
            }
        }
    }

    private fun updateMap() {
        if (::mMap.isInitialized && selectedLatLng != null) {
            mMap.addMarker(MarkerOptions().position(selectedLatLng!!))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, DEFAULT_ZOOM))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in the selected location and move the camera
        if (selectedLatLng != null) {
            mMap.addMarker(MarkerOptions().position(selectedLatLng!!).title("Selected Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, DEFAULT_ZOOM))
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}