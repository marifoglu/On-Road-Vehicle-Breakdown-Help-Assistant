package com.darth.on_road_vehicle_breakdown_help.view.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.darth.on_road_vehicle_breakdown_help.R
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentHomeBinding
import com.darth.on_road_vehicle_breakdown_help.databinding.FragmentSSSBinding
import com.darth.on_road_vehicle_breakdown_help.view.adapter.SSSAdapter
import com.darth.on_road_vehicle_breakdown_help.view.model.SSS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SSSFragment : DialogFragment()  {

private lateinit var binding: FragmentSSSBinding
private var sssList = ArrayList<SSS>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_s_s_s, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSSSBinding.bind(view)

        initData()
        setRecyclerView()

        binding.sssConstraintLayout.setOnClickListener {
            dismiss()
        }
    }

    private fun setRecyclerView(){
        val adapter = SSSAdapter(sssList)
        binding.recyclerViewSSS.adapter = adapter

        binding.recyclerViewSSS.setHasFixedSize(true)
    }

    private fun initData(){

        sssList.add(
            SSS(
            "What do I do if my car has broken down?",
            "If your car has broken down, the most important thing to do is to stay safe. Pull your car off of the road and turn on your hazard warning lights. If you can, call your breakdown provider and organise a mechanic or recovery vehicle\n" +
                    "\n" +
                    "You’ll also need to figure out how to prevent a similar breakdown in the future. Ask your mechanic for advice on what maintenance you should be doing, or consider getting a roadside assistance plan if you do not have one.",
                false
            )
        )

        sssList.add(
            SSS(
                "Can you leave your car if broken down?",
                "If you have broken down on a public road or highway, you may be required to call for roadside assistance or towing services to move your car, as leaving it unattended is not allowed. In this case, you may also be subject to a fine or other penalties, depending on your location.\n" +
                        "\n" +
                        "If you have broken down on private property, such as in a parking lot or on your own driveway, you may be able to leave your car while you look into getting it repaired or replaced. However, you should contact the property owner first to ask for permission.",
                false
            )
        )

        sssList.add(
            SSS(
                "Do you have to stay with a broken down car?",
                "If you are able to assess the situation and determine that it is safe to stay with the car, then you can wait for roadside assistance to arrive. However, if you feel uncomfortable or unsafe, it is best to call for help and leave the vehicle to be repaired or towed away. You cannot leave a vehicle on the motorway or A road.",
                false
            )
        )

        sssList.add(
            SSS(
                "What can I do with a broke down car?",
                "Park in a safe space and call your breakdown provider. Give them all your details and what you may think is wrong with the vehicle. Then either remain in the vehcile, or stand on the other side of the motorway barrier until help comes to fix your vehicle.",
                false
            )
        )
    }
}