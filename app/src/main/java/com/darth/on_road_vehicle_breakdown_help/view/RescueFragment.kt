package com.darth.on_road_vehicle_breakdown_help.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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




        return binding.root
    }

    fun cancelRequest(view: View) {}
    fun updateRequest(view: View) {}

}