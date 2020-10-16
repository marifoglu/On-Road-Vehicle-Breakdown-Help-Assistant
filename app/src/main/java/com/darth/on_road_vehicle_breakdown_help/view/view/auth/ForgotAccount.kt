package com.darth.on_road_vehicle_breakdown_help.view.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.darth.on_road_vehicle_breakdown_help.databinding.ActivityForgotAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotAccount : AppCompatActivity() {

    private lateinit var binding : ActivityForgotAccountBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var alert : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
    }

    fun resetPassword(view: View) {

        val forgotEmail = binding.loginEmail2.text.toString()

        if (forgotEmail.isNotEmpty()){
            auth.sendPasswordResetEmail(forgotEmail).addOnSuccessListener {

                // Alert Dialog
                alert = AlertDialog.Builder(this)
                alert.setTitle("Password Retested")
                    .setMessage("Password reset sent to your email address. " +
                            "\nPlease reset it and try to login again.")
                    .setCancelable(false)
                    .setPositiveButton("OK"){ _, it ->
                        val intent = Intent(this@ForgotAccount, LandingPage::class.java)
                        startActivity(intent)
                        finish()
                    }.show()

            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(this,"You have to fill the email area!", Toast.LENGTH_LONG).show()
        }

    }
}