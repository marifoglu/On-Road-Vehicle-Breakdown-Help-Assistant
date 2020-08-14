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
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentNotificationBinding
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentRescueBinding
import com.google.firebase.auth.FirebaseAuth


class RescueFragment : Fragment() {

    private var _binding : FragmentRescueBinding? = null
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
        _binding = FragmentRescueBinding.inflate(inflater, container, false)

        val cancelButton = binding.root.findViewById<Button>(R.id.buttonCancel)
        val updateButton = binding.root.findViewById<Button>(R.id.buttonUpdate)

        updateButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Update")
            builder.setMessage("Are you sure you want to update help request?")
            builder.setPositiveButton("Yes") { _, _ ->
                val intent = Intent(requireContext(), MapsActivity::class.java)
                intent.putExtra("Key", "update")
                startActivity(intent)
            }
            builder.setNegativeButton("No") { _, _ ->
                // empty...
            }
            builder.create().show()
        }

        return binding.root
    }

    fun cancelRequest(view: View) {}
    fun updateRequest(view: View) {}




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

