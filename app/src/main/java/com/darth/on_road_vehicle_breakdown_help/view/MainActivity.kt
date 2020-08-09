package com.darth.on_road_vehicle_breakdown_help.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val rescueFragment = RescueFragment()
        val notificationFragment = NotificationFragment()
        val settingsFragment = SettingsFragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener  {
            when(it.itemId){
                R.id.navHome ->setCurrentFragment(homeFragment)
                R.id.navRescue ->setCurrentFragment(rescueFragment)
                R.id.navNotifications ->setCurrentFragment(notificationFragment)
                R.id.navSettings ->setCurrentFragment(settingsFragment)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutID,fragment)
            commit()
        }

}