package com.darth.on_road_vehicle_breakdown_help.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentSettingsBinding
import com.darth.on_road_vehicle_breakdown_help.view.login.LandingPage
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Logout
        val logoutButton = binding.root.findViewById<Button>(R.id.button3)

        logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                logout(binding.root)
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                // Do nothing
            }
            val dialog = builder.create()
            dialog.show()
        }

        return binding.root
    }
    private fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(requireContext(), LandingPage::class.java))
        requireActivity().finish()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
