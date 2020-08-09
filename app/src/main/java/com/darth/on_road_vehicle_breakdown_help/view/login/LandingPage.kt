package com.darth.on_road_vehicle_breakdown_help.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityLandingPageBinding
import com.google.firebase.auth.FirebaseAuth


class LandingPage : AppCompatActivity() {

    private lateinit var binding : ActivityLandingPageBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()



    }

    fun forgotPassword(view: View) {}
    fun signIn(view: View) {
        
    }
    fun createAccount(view: View) {
        val intent = Intent(this@LandingPage, RegistrationActivity::class.java)
        startActivity(intent)
    }
}