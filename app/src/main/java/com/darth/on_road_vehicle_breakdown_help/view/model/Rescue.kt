package com.darth.on_road_vehicle_breakdown_help.view.model

data class Rescue(
    //var rescueId: String = "",
    var rescueRequest: String = "",
    var rescueDescribeProblem: String = "",
    var rescueDirection: String = "",
    var rescueMap: Place,
    var rescueVehicle: String = "",
    var rescueVehicleUser: String = ""
)