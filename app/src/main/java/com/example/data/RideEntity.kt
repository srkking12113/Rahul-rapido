package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_history")
data class RideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pickupLocation: String,
    val dropLocation: String,
    val vehicleType: String, // Bike Taxi, Auto, Cab Economy, Parcel Express
    val fare: Double,
    val distanceKm: Double,
    val durationMins: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // COMPLETED, CANCELLED
    val captainName: String,
    val captainRating: Float,
    val vehicleNumber: String,
    val paymentMethod: String
)

@Entity(tableName = "saved_places")
data class SavedPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String, // Home, Office, Gym, Favorite
    val address: String,
    val iconType: String // HOME, WORK, FAVORITE
)
