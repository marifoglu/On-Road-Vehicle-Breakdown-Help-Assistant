package com.darth.on_road_vehicle_breakdown_help.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityRegistrationBinding
import com.darth.on_road_vehicle_breakdown_help.view.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

    }

    fun register(view: View) {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val password2 = binding.password2.text.toString()

        val homeAddress = binding.homeAddress.text.toString()
        val phoneNumber = binding.phoneNumber.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty() && password2.isNotEmpty() && homeAddress.isNotEmpty() && phoneNumber.isNotEmpty()){
            if (password == password2){

                auth.createUserWithEmailAndPassword(email,password)
                    .addOnSuccessListener {

                        val userRegistration = hashMapOf<String, Any>()
                        userRegistration.put("email", auth.currentUser!!.email!!)
                        userRegistration.put("homeAddress", homeAddress)
                        userRegistration.put("phoneNumber", phoneNumber)
                        userRegistration.put("vehicleRegDate", Timestamp.now())

                        firestore.collection("UserInformation").add(userRegistration)
                            .addOnSuccessListener {
                                Toast.makeText(this@RegistrationActivity,"You successfully registered.", Toast.LENGTH_LONG).show()
                            }.addOnFailureListener {
                                Toast.makeText(this@RegistrationActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
                            }

                        val intent = Intent(this@RegistrationActivity, LandingPage::class.java)
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