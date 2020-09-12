package com.darth.on_road_vehicle_breakdown_help.view

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRescueData()
        getUserInformation()
        onClickButtons()
    }


    private fun getRescueData() {

        // If collection has a document?
        val collectionRef = db.collection("Rescue")
        collectionRef.get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Collection has an one document

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

                                        binding.deleteRescueRequest.visibility = View.VISIBLE

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
                                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                            transaction.replace(R.id.frameLayoutID, fragment)?.commit()

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } else {

                // Collection is empty

                val message = "You have a currently road assistance request."
                val startIndex = message.indexOf("currently")
                val endIndex = startIndex + "currently".length

                val spannable = SpannableString(message)
                spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                binding.currentRescueRequest.text = spannable

                binding.addARescueRequest.visibility = View.VISIBLE
                binding.updateRescueRequest.visibility = View.GONE
                binding.deleteRescueRequest.visibility = View.GONE

                binding.addARescueRequest.setOnClickListener {
                    val fragment = RescueFragment()
                    val bundle = Bundle()
                    bundle.putString("data", "new")
                    fragment.arguments = bundle
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frameLayoutID, fragment)?.commit()
                }
            }
        }
        .addOnFailureListener { e ->
            // any errors?
            e.localizedMessage
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
    private fun onClickButtons(){

        binding.deleteRescueRequest.setOnClickListener {

            db.collection("Rescue").addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            for (document in documents) {
                                val documentId = document.id

                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle("Delete")
                                builder.setMessage("Are you sure you want to delete the road assistance request?")
                                builder.setPositiveButton("Yes") { _, _ ->
                                    // Delete document...
                                    db.collection("Rescue").document(documentId).delete()
                                        .addOnSuccessListener {
                                            // Document deleted successfully
                                            Log.d(TAG, "Document deleted successfully")
                                            val fragment = HomeFragment()
                                            val transaction = fragmentManager?.beginTransaction()
                                            transaction?.replace(
                                                com.darth.on_road_vehicle_breakdown_help.R.id.frameLayoutID,
                                                fragment
                                            )?.commit()
                                        }
                                        .addOnFailureListener { e ->
                                            // Error occurred while deleting the document
                                            Log.w(TAG, "Error deleting document", e)
                                        }
                                }
                                builder.setNegativeButton("No") { _, _ ->
                                    // empty...
                                }
                                builder.create().show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}