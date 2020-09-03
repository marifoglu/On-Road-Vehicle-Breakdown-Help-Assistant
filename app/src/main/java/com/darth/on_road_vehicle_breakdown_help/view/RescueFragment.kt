package com.darth.on_road_vehicle_breakdown_help.view

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentRescueBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val DEFAULT_ZOOM = 15f
class RescueFragment : Fragment(), OnMapReadyCallback {

    private var _binding : FragmentRescueBinding? = null
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
        _binding = FragmentRescueBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(com.darth.on_road_vehicle_breakdown_help.R.id.rescueMapContainer) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

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
                            val documentId = document.id
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
                            if (rescueFBRescueRequest == "1"){

                                binding.rescueInformationText.visibility = View.GONE
                                binding.createRescueRequest.visibility = View.GONE

                                binding.vehicleLocation.text = rescueFBMapDirection
                                binding.vehicleProblem.text = rescueFBDescribeProblem
                                binding.vehicleSelected.text = rescueFBVehicle

                                binding.createRescueRequest.setOnClickListener {
                                    val intent = Intent(requireContext(), MapsActivity::class.java)
                                    intent.putExtra("data", "update")
                                    intent.putExtra("dataFB_ID", rescueFBId)
                                    intent.putExtra("dataFB_RescueRequest", rescueFBRescueRequest)
                                    if (rescueFBMapLatitude != null) {
                                        intent.putExtra("dataFB_MapLatitude", rescueFBMapLatitude.toDouble())
                                    }
                                    if (rescueFBMapLongitude != null) {
                                        intent.putExtra("dataFB_MapLongitude", rescueFBMapLongitude.toDouble())
                                    }
                                    intent.putExtra("dataFB_MapDirection", rescueFBMapDirection)
                                    intent.putExtra("dataFB_Vehicle", rescueFBVehicle)
                                    intent.putExtra("dataFB_VehicleUser", rescueFBVehicleUser)
                                    intent.putExtra("dataFB_DescribeProblem", rescueFBDescribeProblem)
                                    startActivity(intent)
                                }

                                // Update the map with the new latitude and longitude values
                                if (rescueFBMapLatitude != null && rescueFBMapLongitude != null) {
                                    selectedLatLng = LatLng(rescueFBMapLatitude, rescueFBMapLongitude)
                                    updateMap()
                                }
                            }

                            binding.buttonUpdate.setOnClickListener {
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle("Update")
                                builder.setMessage("Are you sure you want to update the road assistance request?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    val intent = Intent(requireContext(), MapsActivity::class.java)
                                    intent.putExtra("data", "update")
                                    intent.putExtra("dataFB_ID", rescueFBId)
                                    intent.putExtra("dataFB_RescueRequest", rescueFBRescueRequest)
                                    if (rescueFBMapLatitude != null) {
                                        intent.putExtra("dataFB_MapLatitude", rescueFBMapLatitude.toDouble())
                                    }
                                    if (rescueFBMapLongitude != null) {
                                        intent.putExtra("dataFB_MapLongitude", rescueFBMapLongitude.toDouble())
                                    }
                                    intent.putExtra("dataFB_MapDirection", rescueFBMapDirection)
                                    intent.putExtra("dataFB_Vehicle", rescueFBVehicle)
                                    intent.putExtra("dataFB_VehicleUser", rescueFBVehicleUser)
                                    intent.putExtra("dataFB_DescribeProblem", rescueFBDescribeProblem)
                                    startActivity(intent)
                                }
                                builder.setNegativeButton("No") { _, _ ->
                                    // empty...
                                }
                                builder.create().show()
                            }


                            binding.buttonDelete.setOnClickListener {
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle("Delete")
                                builder.setMessage("Are you sure you want to delete the road assistance request?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    // Delete document...
                                    deleteDocument(documentId)
                                    val intent = Intent(requireContext(), MapsActivity::class.java)
                                    startActivity(intent)


                                }
                                builder.setNegativeButton("No") { _, _ ->
                                    // empty...
                                }
                                builder.create().show()
                            }
                        }
                    }else{
                        runThisFuckinCode()
                    }
                }
            }
        }


    }

    private fun runThisFuckinCode(){
//
//        binding.addARescueRequest.visibility = View.VISIBLE
//
//        binding.mapContainer.visibility = View.GONE
//        binding.updateRescueRequest.visibility = View.GONE
//        binding.mapContainer.visibility = View.GONE
//        binding.rescueFBMapDirectionLabel.visibility = View.GONE
//        binding.rescueFBMapDirection.visibility = View.GONE
//        binding.rescueFBVehicle.visibility  = View.GONE
//        binding.rescueFBVehicleUser.visibility  = View.GONE
//        binding.rescueFBDescribeProblem.visibility  = View.GONE
//        binding.mapContainer.visibility = View.GONE
//
//        binding.addARescueRequest.setOnClickListener {
//            val intent = Intent(requireContext(), MapsActivity::class.java)
//            intent.putExtra("data", "new")
//            startActivity(intent)
    }


    private fun deleteDocument(documentId: String){
        db.collection("Rescue").document(documentId).delete()
            .addOnSuccessListener {
                // Document deleted successfully
                Log.d(TAG, "Document deleted successfully")
            }
            .addOnFailureListener { e ->
                // Error occurred while deleting the document
                Log.w(TAG, "Error deleting document", e)
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng!!, 15f))
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}