package com.darth.on_road_vehicle_breakdown_help.view.model

data class Vehicle(
    var id: String,
    val vehicleManufacturer: String,
    val vehicleModel: String,
    val vehicleYear: String
) {
    constructor() : this("", "", "", "")
}