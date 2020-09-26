package com.darth.on_road_vehicle_breakdown_help.view

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentHomeBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.Place
import com.darth.on_road_vehicle_breakdown_help.view.model.Rescue
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(){

//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding ?: throw IllegalStateException("Attempt to access the binding when it's null")

    private lateinit var binding: FragmentHomeBinding


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val userCollectionRef = Firebase.firestore.collection("UserInformation")
    private val rescueCollectionRef = Firebase.firestore.collection("Rescue")
    private val vehicleCollectionRef = Firebase.firestore.collection("Vehicle")

    private var dataID: String? = null
    private var datadocumentId: String? = null
    private var dataLatitude: String? = null
    private var dataLongitude: String? = null
    private var dataNewDirection: String? = null
    private var dataNewVehicle: String? = null
    private var dataProblem: String? = null
    var googleMap: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.deleteRescueRequest.setOnClickListener {
           // deleteRescue(deleteRescueDataGet())
        }

        lifecycleScope.launch {
            delay(1500)
            getRescueData()
            //sendRescueData()
        }
        getUserInformation()
        onClickButtons()

    }
    private fun getBinding(): FragmentHomeBinding {
        return binding
    }
    private suspend fun getRescueData() {
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
                                        val rescueFBId = document.id as? String
                                        val rescueFBRescueRequest = document.get("rescueRequest") as? String
                                        val rescueFBMap = document.get("rescueMap") as? Map<*, *>
                                        val rescueFBMapLatitude = rescueFBMap?.get("latitude") as? Double
                                        val rescueFBMapLongitude = rescueFBMap?.get("longitude") as? Double
                                        val rescueFBMapDirection = document.get("rescueDirection") as? String
                                        val rescueFBVehicle = document.get("rescueVehicle") as? String
                                        val rescueFBVehicleUser = document.get("rescueVehicleUser") as? String
                                        val rescueFBDescribeProblem = document.get("rescueDescribeProblem") as? String

                                        // If user has a rescue request-----------------------------------------
                                        if (rescueFBRescueRequest == "1"){

                                            binding.addARescueRequest.visibility = View.GONE

                                            binding.currentRescueRequest.visibility = View.VISIBLE

                                            val message = "You have a currently road assistance request."
                                            val startIndex = message.indexOf("currently")
                                            val endIndex = startIndex + "currently".length
                                            val spannable = SpannableString(message)
                                            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                            binding.currentRescueRequest.text = spannable

                                            binding.deleteRescueRequest.visibility = View.VISIBLE

                                            binding.linearLayoutId.setOnClickListener {

                                                val fragment = RescueFragment()
                                                val bundle = Bundle()
                                                bundle.putString("data", "show")
                                                bundle.putString("dataID", rescueFBId)
                                                bundle.putString("dataRescueRequest", rescueFBRescueRequest)
                                                if (rescueFBMapLatitude != null) {
                                                    bundle.putString("dataMapLatitude", rescueFBMapLatitude.toDouble().toString()
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
                                                transaction.replace(R.id.frameLayoutID, fragment).commit()

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else {

                    // Collection is empty
                    val message = "You do not have a currently road assistance request."
                    val startIndex = message.indexOf("do not have")
                    val endIndex = startIndex + "do not have".length

                    val spannable = SpannableString(message)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    binding.currentRescueRequest.text = spannable
                    binding.addARescueRequest.visibility = View.VISIBLE
                    binding.deleteRescueRequest.visibility = View.GONE

                    binding.addARescueRequest.setOnClickListener {
                        val fragment = RescueFragment()
                        val bundle = Bundle()
                        bundle.putString("data", "create")
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
    private fun onClickButtons() {
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

                                if (isAdded) { // Check if the fragment is added to the activity
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
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}