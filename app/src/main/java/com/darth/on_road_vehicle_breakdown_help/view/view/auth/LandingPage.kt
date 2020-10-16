package com.darth.on_road_vehicle_breakdown_help.view.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityLandingPageBinding
import com.darth.on_road_vehicle_breakdown_help.view.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LandingPage : AppCompatActivity() {

    private lateinit var binding : ActivityLandingPageBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // User is already authenticated, start the MainActivity and finish the LoginActivity
                startActivity(Intent(this@LandingPage, MainActivity::class.java))
                finish()
            }
        }

    }

    fun forgotPassword(view: View) {

        val intent = Intent(this@LandingPage, ForgotAccount::class.java)
        startActivity(intent)
        finish()
    }

    fun loginSignIn(view: View) {

        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {

                    val intent = Intent(this@LandingPage, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }else {

            Toast.makeText(this,"You have to fill all forms for the registration!", Toast.LENGTH_LONG).show()
        }


    }
    fun createAccount(view: View) {
        val intent = Intent(this@LandingPage, RegistrationActivity::class.java)
        startActivity(intent)
    }
}