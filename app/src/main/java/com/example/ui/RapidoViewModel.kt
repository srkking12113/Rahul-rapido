package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RapidoRepository
import com.example.data.RideEntity
import com.example.data.SavedPlaceEntity
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class RapidoUiState(
    val currentStep: RideStep = RideStep.LOCATION_SELECT,
    val pickup: LocationPoint = LocationPoint("Current Location (HSR Layout)", "17th Main Rd, Sector 4, Bengaluru", 0.25f, 0.65f),
    val drop: LocationPoint? = null,
    val selectedVehicleType: VehicleType = VehicleType.BIKE_TAXI,
    val appliedPromo: PromoCode? = null,
    val selectedPayment: PaymentMethodOption = PaymentMethodOption("WALLET", "Rapido Wallet", "account_balance_wallet", isDefault = true),
    val walletBalance: Double = 185.0,
    val assignedCaptain: Captain? = null,
    val otp: String = "4821",
    val tripProgress: Float = 0.0f, // 0.0f to 1.0f
    val isSearching: Boolean = false,
    val activeTab: String = "BOOK", // BOOK, HISTORY, SAVED, CAPTAIN
    val driverMode: Boolean = false,
    val driverOnline: Boolean = true,
    val driverEarningsToday: Double = 640.0,
    val driverCompletedRides: Int = 8,
    val lastCompletedRide: RideEntity? = null,
    val ratingGiven: Float = 5.0f,
    val feedbackSubmitted: Boolean = false
)

class RapidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RapidoRepository
    val rideHistory: StateFlow<List<RideEntity>>
    val savedPlaces: StateFlow<List<SavedPlaceEntity>>

    private val _uiState = MutableStateFlow(RapidoUiState())
    val uiState: StateFlow<RapidoUiState> = _uiState.asStateFlow()

    private var tripSimulationJob: Job? = null

    val popularLocations = listOf(
        LocationPoint("Indiranagar Metro Station", "100 Feet Rd, Indiranagar, Bengaluru", 0.70f, 0.30f),
        LocationPoint("Koramangala Forum Mall", "Hosur Rd, Koramangala, Bengaluru", 0.45f, 0.55f),
        LocationPoint("RMZ Ecoworld", "Outer Ring Rd, Bellandur, Bengaluru", 0.85f, 0.75f),
        LocationPoint("Kempegowda Int'l Airport (KIA)", "Devanahalli, Bengaluru", 0.80f, 0.15f),
        LocationPoint("Bengaluru City Railway Station", "Majestic, Bengaluru", 0.20f, 0.25f),
        LocationPoint("Phoenix Marketcity", "Whitefield Main Rd, Bengaluru", 0.90f, 0.40f),
        LocationPoint("MG Road Boulevard", "Church Street / MG Road, Bengaluru", 0.50f, 0.35f)
    )

    val availablePromos = listOf(
        PromoCode("RAPIDO50", "50% OFF on Bike Taxis (Up to ₹25)", 50, 25.0),
        PromoCode("WELCOME20", "Flat ₹20 OFF on any ride", 20, 20.0),
        PromoCode("PASS2026", "Save ₹15 per ride with Rapido Pass", 15, 15.0)
    )

    val paymentOptions = listOf(
        PaymentMethodOption("WALLET", "Rapido Wallet", "account_balance_wallet", isDefault = true),
        PaymentMethodOption("UPI", "UPI (GPay / PhonePe / Paytm)", "qr_code"),
        PaymentMethodOption("CASH", "Cash to Captain", "payments"),
        PaymentMethodOption("CARD", "Credit / Debit Card", "credit_card")
    )

    val captainPool = listOf(
        Captain("Ramesh Verma", "Honda Shine 125", "KA 05 EQ 8821", 4.92f, 1840, "+91 98765 11223"),
        Captain("Vikram Singh", "Bajaj RE Auto", "KA 01 MG 4210", 4.88f, 3210, "+91 98123 44556"),
        Captain("Anand Kumar", "Maruti WagonR AC", "KA 51 Z 9081", 4.95f, 950, "+91 99001 88776"),
        Captain("Deepak Sharma", "TVS Jupiter", "KA 03 EV 3319", 4.85f, 1420, "+91 97411 22334")
    )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = RapidoRepository(database.rideDao())
        rideHistory = repository.allRides.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        savedPlaces = repository.savedPlaces.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.seedDefaultPlacesIfEmpty()
        }
    }

    fun setDropLocation(location: LocationPoint) {
        _uiState.update {
            it.copy(
                drop = location,
                currentStep = RideStep.VEHICLE_SELECT
            )
        }
    }

    fun setPickupLocation(location: LocationPoint) {
        _uiState.update { it.copy(pickup = location) }
    }

    fun selectVehicleType(type: VehicleType) {
        _uiState.update { it.copy(selectedVehicleType = type) }
    }

    fun applyPromo(promo: PromoCode?) {
        _uiState.update { it.copy(appliedPromo = promo) }
    }

    fun selectPaymentMethod(option: PaymentMethodOption) {
        _uiState.update { it.copy(selectedPayment = option) }
    }

    fun addWalletFunds(amount: Double) {
        _uiState.update { it.copy(walletBalance = it.walletBalance + amount) }
    }

    fun setStep(step: RideStep) {
        _uiState.update { it.copy(currentStep = step) }
    }

    fun setActiveTab(tab: String) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun toggleDriverMode() {
        _uiState.update { it.copy(driverMode = !it.driverMode) }
    }

    fun toggleDriverOnline() {
        _uiState.update { it.copy(driverOnline = !it.driverOnline) }
    }

    fun calculateDistanceKm(): Double {
        val state = _uiState.value
        val drop = state.drop ?: return 3.5
        val dx = (drop.x - state.pickup.x) * 12.0
        val dy = (drop.y - state.pickup.y) * 12.0
        val dist = kotlin.math.sqrt((dx * dx + dy * dy).toDouble())
        return kotlin.math.max(1.2, (dist * 10).roundToInt() / 10.0)
    }

    fun calculateFare(vehicleType: VehicleType): Double {
        val dist = calculateDistanceKm()
        val rawFare = vehicleType.baseFare + (dist * vehicleType.perKmRate)
        val promo = _uiState.value.appliedPromo
        var finalFare = rawFare
        if (promo != null) {
            val discount = (rawFare * promo.discountPercent / 100.0).coerceAtMost(promo.maxDiscount)
            finalFare -= discount
        }
        return kotlin.math.max(20.0, (finalFare).roundToInt().toDouble())
    }

    fun calculateEtaMins(vehicleType: VehicleType): Int {
        val dist = calculateDistanceKm()
        val mins = (dist * 3.0 / vehicleType.speedFactor).roundToInt()
        return kotlin.math.max(3, mins)
    }

    fun startBookingRide() {
        val state = _uiState.value
        val drop = state.drop ?: return

        tripSimulationJob?.cancel()

        _uiState.update {
            it.copy(
                currentStep = RideStep.SEARCHING_CAPTAIN,
                isSearching = true,
                tripProgress = 0.0f,
                feedbackSubmitted = false
            )
        }

        tripSimulationJob = viewModelScope.launch {
            // Step 1: Searching for 2.5 seconds
            delay(2500)

            // Select captain based on vehicle
            val captain = when (state.selectedVehicleType) {
                VehicleType.BIKE_TAXI -> captainPool[0]
                VehicleType.AUTO -> captainPool[1]
                VehicleType.CAB_ECONOMY -> captainPool[2]
                VehicleType.PARCEL_EXPRESS -> captainPool[3]
            }

            val generatedOtp = (1000..9999).random().toString()

            _uiState.update {
                it.copy(
                    currentStep = RideStep.CAPTAIN_ASSIGNED,
                    isSearching = false,
                    assignedCaptain = captain,
                    otp = generatedOtp
                )
            }

            // Step 2: Captain assigned for 2 seconds before trip starts
            delay(2000)

            _uiState.update {
                it.copy(currentStep = RideStep.IN_TRIP)
            }

            // Step 3: Animate ride progress from 0.0 to 1.0 over ~10 seconds
            val totalSteps = 50
            for (i in 1..totalSteps) {
                delay(200)
                val progress = i.toFloat() / totalSteps
                _uiState.update { it.copy(tripProgress = progress) }
            }

            // Step 4: Trip Completed
            val finalFare = calculateFare(state.selectedVehicleType)
            val distance = calculateDistanceKm()
            val eta = calculateEtaMins(state.selectedVehicleType)

            val completedEntity = RideEntity(
                pickupLocation = state.pickup.name,
                dropLocation = drop.name,
                vehicleType = state.selectedVehicleType.title,
                fare = finalFare,
                distanceKm = distance,
                durationMins = eta,
                timestamp = System.currentTimeMillis(),
                status = "COMPLETED",
                captainName = captain.name,
                captainRating = captain.rating,
                vehicleNumber = captain.vehicleNumber,
                paymentMethod = state.selectedPayment.name
            )

            // Deduct from wallet if wallet selected
            if (state.selectedPayment.id == "WALLET") {
                _uiState.update { it.copy(walletBalance = kotlin.math.max(0.0, it.walletBalance - finalFare)) }
            }

            // Insert into Room DB
            repository.insertRide(completedEntity)

            _uiState.update {
                it.copy(
                    currentStep = RideStep.TRIP_COMPLETED,
                    lastCompletedRide = completedEntity
                )
            }
        }
    }

    fun cancelRide() {
        tripSimulationJob?.cancel()
        _uiState.update {
            it.copy(
                currentStep = RideStep.VEHICLE_SELECT,
                isSearching = false,
                tripProgress = 0.0f
            )
        }
    }

    fun submitRating(rating: Float) {
        _uiState.update {
            it.copy(
                ratingGiven = rating,
                feedbackSubmitted = true
            )
        }
    }

    fun finishTripAndReset() {
        _uiState.update {
            it.copy(
                currentStep = RideStep.LOCATION_SELECT,
                drop = null,
                assignedCaptain = null,
                appliedPromo = null,
                tripProgress = 0.0f,
                feedbackSubmitted = false
            )
        }
    }

    fun addSavedPlace(label: String, address: String, iconType: String) {
        viewModelScope.launch {
            repository.insertSavedPlace(
                SavedPlaceEntity(
                    label = label,
                    address = address,
                    iconType = iconType
                )
            )
        }
    }

    fun deleteSavedPlace(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedPlace(id)
        }
    }

    fun clearRideHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
