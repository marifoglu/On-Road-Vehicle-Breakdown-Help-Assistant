package com.darth.on_road_vehicle_breakdown_help.view.adapter

data class BrandListAdapter(val brand: String, val models: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BrandListAdapter

        if (brand != other.brand) return false
        if (!models.contentEquals(other.models)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = brand.hashCode()
        result = 31 * result + models.contentHashCode()
        return result
    }
}
