package com.darth.on_road_vehicle_breakdown_help.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityRegistrationBinding
import com.darth.on_road_vehicle_breakdown_help.view.MainActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistrationBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


    }

    fun register(view: View) {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val password2 = binding.password2.text.toString()

        val homeAddress = binding.homeAddress.text.toString()
        val phoneNumber = binding.phoneNumber.text.toString()

        val vehicleManufecturer = binding.vehicleManufecturer.text.toString()
        val vehicleModel = binding.vehicleModel.text.toString()
        val vehicleYear = binding.vehicleYear.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && password2.isNotEmpty()/* && homeAddress.isNotEmpty() && phoneNumber.isNotEmpty() && vehicleManufecturer.isNotEmpty() && vehicleModel.isNotEmpty() && vehicleYear.isNotEmpty()*/){
            if (password == password2){

                auth.createUserWithEmailAndPassword(email,password)
                    .addOnSuccessListener {
                        val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this,"Registration completed! You can login now.", Toast.LENGTH_LONG).show()

                }.addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }else {

                Toast.makeText(this,"You have to fill all forms for the registration!", Toast.LENGTH_LONG).show()
            }


        }
    }
}