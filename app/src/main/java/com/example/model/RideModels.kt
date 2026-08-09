package com.example.model

enum class VehicleType(
    val title: String,
    val subtitle: String,
    val capacity: String,
    val baseFare: Double,
    val perKmRate: Double,
    val speedFactor: Float // For ETA calculation
) {
    BIKE_TAXI(
        title = "Bike Taxi",
        subtitle = "Fastest for single rider, Beat the traffic!",
        capacity = "1 Person",
        baseFare = 25.0,
        perKmRate = 8.0,
        speedFactor = 1.2f
    ),
    AUTO(
        title = "Auto",
        subtitle = "Metered convenience, Up to 3 passengers",
        capacity = "3 Persons",
        baseFare = 40.0,
        perKmRate = 12.0,
        speedFactor = 1.0f
    ),
    CAB_ECONOMY(
        title = "Cab Economy",
        subtitle = "Comfortable hatchback with AC",
        capacity = "4 Persons",
        baseFare = 80.0,
        perKmRate = 18.0,
        speedFactor = 0.9f
    ),
    PARCEL_EXPRESS(
        title = "Parcel Express",
        subtitle = "Instant item pickup & door delivery",
        capacity = "Max 15 kg",
        baseFare = 35.0,
        perKmRate = 9.5,
        speedFactor = 1.1f
    )
}

data class LocationPoint(
    val name: String,
    val address: String,
    val x: Float, // Map normalized coordinate 0.0f..1.0f
    val y: Float
)

data class Captain(
    val name: String,
    val vehicleName: String,
    val vehicleNumber: String,
    val rating: Float,
    val totalRides: Int,
    val phone: String,
    val helmetVerified: Boolean = true
)

data class PromoCode(
    val code: String,
    val description: String,
    val discountPercent: Int,
    val maxDiscount: Double
)

enum class RideStep {
    LOCATION_SELECT,
    VEHICLE_SELECT,
    SEARCHING_CAPTAIN,
    CAPTAIN_ASSIGNED,
    IN_TRIP,
    TRIP_COMPLETED,
    HISTORY,
    SAVED_PLACES,
    CAPTAIN_MODE
}

data class PaymentMethodOption(
    val id: String,
    val name: String,
    val iconName: String,
    val isDefault: Boolean = false
)
