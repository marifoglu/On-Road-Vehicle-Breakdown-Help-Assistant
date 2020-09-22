package com.darth.on_road_vehicle_breakdown_help.view

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentFunctionsBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.Place
import com.darth.on_road_vehicle_breakdown_help.view.model.Rescue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class FunctionsFragment : Fragment() {

    private var _binding: FragmentFunctionsBinding? = null
    private val binding get() = _binding!!

    private val userCollectionRef = Firebase.firestore.collection("UserInformation")
    private val rescueCollectionRef = Firebase.firestore.collection("Rescue")
    private val vehicleCollectionRef = Firebase.firestore.collection("Vehicle")

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var dataID: String? = null
    private var dataDocumentId: String? = null
    private var dataRescueId: String? = null
    private var dataRescueRequest: String? = null
    private var dataLatitude: String? = null
    private var dataLongitude: String? = null
    private var dataNewDirection: String? = null
    private var dataNewVehicle: String? = null
    private var dataProblem: String? = null
    private var dataVehicleUser: String? = null
    private var googleMap: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFunctionsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        bundleGets()
        dataCheck()

        binding.updateData.setOnClickListener {
            val newDataMap = getNewDataMap()
            updateRescue(newDataMap)
        }
    }

    private fun bundleGets() {

        arguments?.let { // null check
            dataID = it.getString("data") // "create" "update" "delete"
            dataDocumentId = it.getString("documentID")
            dataRescueRequest = it.getString("dataRescueRequest")
            dataLatitude = it.getString("dataMapLatitude")
            dataLongitude = it.getString("dataMapLongitude")
            dataNewDirection = it.getString("dataMapDirection")
            dataNewVehicle = it.getString("dataVehicle")
            dataProblem = it.getString("dataDescribeProblem")
            dataVehicleUser = it.getString("dataVehicleUser")

            binding.textRescueId.text = "DocumentID : $dataDocumentId"
            binding.textRescueDescribeProblem.text = "Problem : $dataProblem"
            binding.textRescueRequest.text = "Rescue Request : $dataRescueRequest"
            binding.textRescueDirection.text = "Directions : $dataNewDirection"
            binding.textRescueMapLatitude.text = "Latitude : $dataLatitude"
            binding.textRescueMapLongitude.text = "Longitude : $dataLongitude"
            binding.textRescueVehicle.text = "Vehicle : $dataNewVehicle"
            binding.textRescueVehicleUser.text = "User : $dataVehicleUser"
        }
    }
    private fun dataCheck(){
        if (dataID.equals("create")){
            Log.d("DATA", "Data id is $dataID")

        }else if (dataID.equals("update")){
            Log.d("DATA", "Data id is $dataID")


        }else if (dataID.equals("delete")){
            deleteRescue(deleteRescueDataGet())
            Log.d("DATA", "Data id is $dataID")

        }else{
            Log.d("DATA", "Data id is $dataID")
        }
    }

    private fun getNewDataMap(): Map<String, Any> {
        val googleMap = hashMapOf(
            "latitude" to dataLatitude!!.toDouble(),
            "longitude" to dataLongitude!!.toDouble()
        )

        val map = mutableMapOf<String, Any>()
        map["rescueDescribeProblem"] = dataProblem!!
        map["rescueDirection"] = dataNewDirection!!
        map["rescueMap"] = googleMap
        map["rescueRequest"] = dataRescueRequest!!
        map["rescueVehicle"] = dataNewVehicle!!
        map["rescueVehicleUser"] = dataVehicleUser!!

        return map
    }


    private fun updateRescue(newRescueMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
//            val rescueQuery = rescueCollectionRef
            val rescueQuery = rescueCollectionRef.get().await()
//                .whereEqualTo("rescueRequest", dataRescueRequest!!)
//                .whereEqualTo("rescueDirection", dataNewDirection!!)
//                .whereEqualTo("rescueDescribeProblem", dataProblem!!)
//                .whereEqualTo("rescueMap.latitude", dataLatitude!!.toDouble())
//                .whereEqualTo("rescueMap.longitude", dataLongitude!!.toDouble())
//                .whereEqualTo("rescueVehicle", dataNewVehicle!!)
//                .whereEqualTo("rescueVehicleUser", dataVehicleUser!!)
//                .get()
//                .await()
            if (rescueQuery.documents.isNotEmpty()) {
                for (document in rescueQuery) {
                    Log.d("MyApp", "Document found: ${document.id}")
                    Log.d("MyApp", "Document data: ${document.data}")
                    try {
                        rescueCollectionRef.document(document.id).set(
                            newRescueMap,
                            SetOptions.merge()
                        ).await()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "No road assistance request matched the query.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    private fun deleteRescueDataGet(): Rescue {

        val rescueRequest = "1"
        val vehicleUser = "auth.currentUser!!.email!!"
        val rescueDirection = ""
        val rescueDescribeProblem = ""
        val googleMap = Place(0.0, 0.0)
        val vehicleItem = ""

        return Rescue(
            rescueRequest = rescueRequest,
            rescueDescribeProblem = rescueDescribeProblem,
            rescueDirection = rescueDirection,
            rescueMap = googleMap,
            rescueVehicle = vehicleItem,
            rescueVehicleUser = vehicleUser,
        )
    }
    private fun deleteRescue(rescue: Rescue) = CoroutineScope(Dispatchers.IO).launch {
        val rescueQuery = rescueCollectionRef
            .get()
            .await()
        if(rescueQuery.documents.isNotEmpty()) {
            for(document in rescueQuery) {
                try {
                    rescueCollectionRef.document(document.id).delete().await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Road assistance request successfully removed.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No rescue request matched the query.", Toast.LENGTH_LONG).show()
            }
        }
    }



}