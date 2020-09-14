package com.darth.on_road_vehicle_breakdown_help.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityMainBinding
import com.darth.on_road_vehicle_breakdown_help.view.login.LandingPage
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        auth = FirebaseAuth.getInstance()

        val homeFragment = HomeFragment()
        val rescueFragment = RescueFragment()
        val notificationFragment = NotificationFragment()
        val settingsFragment = SettingsFragment()

        binding.bottomNavigationView.setOnItemSelectedListener  {
            when(it.itemId){
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

                when(fragment) {
                    is HomeFragment -> {
                        // Something for HomeFragment?
                    }
                    is RescueFragment -> {
                        // Set data to the bundle
                        bundle.putString("data", "navbar")
                    }
                    is NotificationFragment -> {
                        // Do something for NotificationFragment
                    }
                    is SettingsFragment -> {
                        // Do something for SettingsFragment
                    }
                }

                // Add the bundle to the fragment instance
                fragment.arguments = bundle

                replace(R.id.frameLayoutID, fragment)
                commit()
            }
        }
    }


    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LandingPage::class.java))
        finish()
    }

}