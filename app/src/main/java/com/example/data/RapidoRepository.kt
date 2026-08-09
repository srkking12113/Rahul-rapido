package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RapidoRepository(private val rideDao: RideDao) {

    val allRides: Flow<List<RideEntity>> = rideDao.getAllRides()
    val savedPlaces: Flow<List<SavedPlaceEntity>> = rideDao.getSavedPlaces()

    suspend fun insertRide(ride: RideEntity): Long = rideDao.insertRide(ride)

    suspend fun deleteRide(id: Long) = rideDao.deleteRide(id)

    suspend fun clearHistory() = rideDao.clearAllHistory()

    suspend fun insertSavedPlace(place: SavedPlaceEntity): Long = rideDao.insertSavedPlace(place)

    suspend fun deleteSavedPlace(id: Long) = rideDao.deleteSavedPlace(id)

    suspend fun seedDefaultPlacesIfEmpty() {
        val currentPlaces = savedPlaces.first()
        if (currentPlaces.isEmpty()) {
            rideDao.insertSavedPlace(
                SavedPlaceEntity(
                    label = "Home",
                    address = "Sector 4, HSR Layout, Bengaluru",
                    iconType = "HOME"
                )
            )
            rideDao.insertSavedPlace(
                SavedPlaceEntity(
                    label = "Office / Tech Park",
                    address = "RMZ Ecoworld, Bellandur, Bengaluru",
                    iconType = "WORK"
                )
            )
            rideDao.insertSavedPlace(
                SavedPlaceEntity(
                    label = "Metro Station",
                    address = "Indiranagar Metro Station, 100 Feet Rd",
                    iconType = "FAVORITE"
                )
            )

            // Seed 2 sample initial ride history records
            rideDao.insertRide(
                RideEntity(
                    pickupLocation = "Forum Mall, Koramangala",
                    dropLocation = "Indiranagar Metro Station",
                    vehicleType = "Bike Taxi",
                    fare = 42.0,
                    distanceKm = 4.2,
                    durationMins = 12,
                    timestamp = System.currentTimeMillis() - 86400000L,
                    status = "COMPLETED",
                    captainName = "Rajesh Kumar",
                    captainRating = 4.9f,
                    vehicleNumber = "KA 01 EK 4920",
                    paymentMethod = "Rapido Wallet"
                )
            )
            rideDao.insertRide(
                RideEntity(
                    pickupLocation = "HSR Layout Sector 1",
                    dropLocation = "RMZ Ecoworld, Bellandur",
                    vehicleType = "Auto",
                    fare = 78.0,
                    distanceKm = 5.8,
                    durationMins = 18,
                    timestamp = System.currentTimeMillis() - 172800000L,
                    status = "COMPLETED",
                    captainName = "Suresh Gowda",
                    captainRating = 4.8f,
                    vehicleNumber = "KA 05 M 9821",
                    paymentMethod = "UPI / GPay"
                )
            )
        }
    }
}
