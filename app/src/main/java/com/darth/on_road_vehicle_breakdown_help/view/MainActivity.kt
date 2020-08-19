package com.darth.on_road_vehicle_breakdown_help.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        auth = FirebaseAuth.getInstance()


        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {

                // User is already authenticated
                val homeFragment = HomeFragment()
                val rescueFragment = RescueFragment()
                val notificationFragment = NotificationFragment()
                val settingsFragment = SettingsFragment()

                binding.bottomNavigationView.setOnItemSelectedListener  {
                    when(it.itemId){
                        R.id.navHome ->setCurrentFragment(homeFragment)
                        R.id.navRescue ->setCurrentFragment(rescueFragment)
                        R.id.navNotifications ->setCurrentFragment(notificationFragment)
                        R.id.navSettings ->setCurrentFragment(settingsFragment)
                    }
                    true
                }

                setCurrentFragment(homeFragment)
            } else {
                // User is not authenticated
                startActivity(Intent(this, LandingPage::class.java))
                finish()
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutID,fragment)
            commit()
        }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LandingPage::class.java))
        finish()
    }

}