package com.darth.on_road_vehicle_breakdown_help.view.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityMainBinding
import com.darth.on_road_vehicle_breakdown_help.view.view.fragments.HomeFragment
import com.darth.on_road_vehicle_breakdown_help.view.view.fragments.NotificationFragment
import com.darth.on_road_vehicle_breakdown_help.view.view.fragments.RescueFragment
import com.darth.on_road_vehicle_breakdown_help.view.view.fragments.SettingsFragment
import com.darth.on_road_vehicle_breakdown_help.view.view.auth.LandingPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        lifecycleScope.launch {
            delay(500)
            getRescueData()
        }

        val homeFragment = HomeFragment()
        val rescueFragment = RescueFragment()
        val notificationFragment = NotificationFragment()
        val settingsFragment = SettingsFragment()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navHome -> setCurrentFragment(homeFragment)
                R.id.navRescue -> setCurrentFragment(rescueFragment)
                R.id.navNotifications -> setCurrentFragment(notificationFragment)
                R.id.navSettings -> setCurrentFragment(settingsFragment)
            }
            true
        }

        if (isFragmentChangeAllowed()) {
            setCurrentFragment(homeFragment)
        }

        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // User authenticated
            } else {
                // User is not authenticated
                startActivity(Intent(this, LandingPage::class.java))
                finish()
            }
        }
    }

    private fun isFragmentChangeAllowed(): Boolean {
        // Check some condition
        return true
    }

    private fun setCurrentFragment(fragment: Fragment) {
        if (!isFinishing && isFragmentChangeAllowed()) {
            supportFragmentManager.beginTransaction().apply {
                // Create a bundle to pass data
                val bundle = Bundle()

                when (fragment) {
                    is HomeFragment -> {
                        // Something for HomeFragment?
                    }
                    is RescueFragment -> {
                        // Set data to the bundle
                        bundle.putString("data", "navbar")
                        bundle.putString("dataID", dataID)
                        bundle.putString("dataRescueRequest", dataRescueRequest)
                        bundle.putString("dataMapLatitude", rescueMapLatitude)
                        bundle.putString("dataMapLongitude", rescueMapLongitude)
                        bundle.putString("dataMapDirection", dataMapDirection)
                        bundle.putString("dataVehicle", dataVehicle)
                        bundle.putString("dataVehicleUser", dataVehicleUser)
                        bundle.putString("dataDescribeProblem", dataDescribeProblem)
                    }
                    is NotificationFragment -> {
                        // Do something for NotificationFragment
                    }
                    is SettingsFragment -> {
                        // Do something for SettingsFragment
                    }
                }
                fragment.arguments = bundle

                replace(R.id.frameLayoutID, fragment)
                commit()
            }
        }
    }

    private fun getRescueData() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            db.collection("Rescue")
                .whereEqualTo("email", currentUserEmail)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(
                            this@MainActivity,
                            error.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (value != null) {
                            if (!value.isEmpty) {
                                val documents = value.documents
                                for (document in documents) {
                                    dataID = document.id as? String
                                    dataRescueRequest = document.get("rescueRequest") as? String
                                    val rescueMap =
                                        document.get("rescueMap") as? Map<String, Double>
                                    rescueMapLatitude = rescueMap?.get("latitude").toString()
                                    rescueMapLongitude = rescueMap?.get("longitude").toString()
                                    dataMapDirection =
                                        document.get("rescueDirection") as? String
                                    dataVehicle = document.get("rescueVehicle") as? String
                                    dataVehicleUser =
                                        document.get("rescueVehicleUser") as? String
                                    dataDescribeProblem =
                                        document.get("rescueDescribeProblem") as? String

                                }
                            }
                        }
                    }
                }
        }
    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LandingPage::class.java))
        finish()
    }
}