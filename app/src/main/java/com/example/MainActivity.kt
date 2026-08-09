package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.RideStep
import com.example.ui.RapidoViewModel
import com.example.ui.components.*
import com.example.ui.screens.CaptainModeScreen
import com.example.ui.screens.RideHistoryScreen
import com.example.ui.screens.WalletAndSavedPlacesScreen
import com.example.ui.theme.RapidoDark
import com.example.ui.theme.RapidoGreen
import com.example.ui.theme.RapidoTheme
import com.example.ui.theme.RapidoYellow

class MainActivity : ComponentActivity() {

    private val viewModel: RapidoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RapidoTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val rideHistory by viewModel.rideHistory.collectAsStateWithLifecycle()
                val savedPlaces by viewModel.savedPlaces.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        RapidoTopAppBar(
                            walletBalance = uiState.walletBalance,
                            activeTab = uiState.activeTab,
                            onWalletClick = { viewModel.setActiveTab("SAVED") }
                        )
                    },
                    bottomBar = {
                        RapidoBottomNavigation(
                            activeTab = uiState.activeTab,
                            onTabSelected = { viewModel.setActiveTab(it) }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.activeTab) {
                            "BOOK" -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // 1. Interactive Map in Background
                                    RapidoMapCanvas(
                                        pickup = uiState.pickup,
                                        drop = uiState.drop,
                                        rideStep = uiState.currentStep,
                                        selectedVehicleType = uiState.selectedVehicleType,
                                        tripProgress = uiState.tripProgress,
                                        isSearching = uiState.isSearching,
                                        onRecenterClick = { }
                                    )

                                    // 2. Dynamic Ride Booking Bottom Sheet
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                    ) {
                                        when (uiState.currentStep) {
                                            RideStep.LOCATION_SELECT -> {
                                                LocationSelectorSheet(
                                                    pickup = uiState.pickup,
                                                    popularLocations = viewModel.popularLocations,
                                                    savedPlaces = savedPlaces,
                                                    onSelectDropLocation = { viewModel.setDropLocation(it) }
                                                )
                                            }

                                            RideStep.VEHICLE_SELECT -> {
                                                val drop = uiState.drop ?: viewModel.popularLocations[0]
                                                VehicleSelectionSheet(
                                                    pickup = uiState.pickup,
                                                    drop = drop,
                                                    selectedVehicleType = uiState.selectedVehicleType,
                                                    appliedPromo = uiState.appliedPromo,
                                                    selectedPayment = uiState.selectedPayment,
                                                    walletBalance = uiState.walletBalance,
                                                    distanceKm = viewModel.calculateDistanceKm(),
                                                    availablePromos = viewModel.availablePromos,
                                                    onSelectVehicle = { viewModel.selectVehicleType(it) },
                                                    onApplyPromo = { viewModel.applyPromo(it) },
                                                    onSelectPayment = { viewModel.selectPaymentMethod(it) },
                                                    onBookClick = { viewModel.startBookingRide() },
                                                    onChangeLocationClick = { viewModel.setStep(RideStep.LOCATION_SELECT) },
                                                    calculateFare = { viewModel.calculateFare(it) },
                                                    calculateEta = { viewModel.calculateEtaMins(it) }
                                                )
                                            }

                                            RideStep.SEARCHING_CAPTAIN -> {
                                                SearchingCaptainSheet(
                                                    vehicleType = uiState.selectedVehicleType,
                                                    onCancelClick = { viewModel.cancelRide() }
                                                )
                                            }

                                            RideStep.CAPTAIN_ASSIGNED, RideStep.IN_TRIP -> {
                                                val captain = uiState.assignedCaptain ?: viewModel.captainPool[0]
                                                val drop = uiState.drop ?: viewModel.popularLocations[0]

                                                ActiveRideSheet(
                                                    rideStep = uiState.currentStep,
                                                    captain = captain,
                                                    otp = uiState.otp,
                                                    vehicleType = uiState.selectedVehicleType,
                                                    pickup = uiState.pickup,
                                                    drop = drop,
                                                    tripProgress = uiState.tripProgress,
                                                    fare = viewModel.calculateFare(uiState.selectedVehicleType),
                                                    onCancelRide = { viewModel.cancelRide() }
                                                )
                                            }

                                            RideStep.TRIP_COMPLETED -> {
                                                TripCompletedSheet(
                                                    ride = uiState.lastCompletedRide,
                                                    ratingGiven = uiState.ratingGiven,
                                                    feedbackSubmitted = uiState.feedbackSubmitted,
                                                    onRatingSelected = { viewModel.submitRating(it) },
                                                    onDoneClick = { viewModel.finishTripAndReset() }
                                                )
                                            }

                                            else -> {
                                                LocationSelectorSheet(
                                                    pickup = uiState.pickup,
                                                    popularLocations = viewModel.popularLocations,
                                                    savedPlaces = savedPlaces,
                                                    onSelectDropLocation = { viewModel.setDropLocation(it) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            "HISTORY" -> {
                                RideHistoryScreen(
                                    rides = rideHistory,
                                    onRebookRide = { ride ->
                                        val dropLoc = viewModel.popularLocations.firstOrNull { it.name == ride.dropLocation }
                                            ?: viewModel.popularLocations[0]
                                        viewModel.setDropLocation(dropLoc)
                                        viewModel.setActiveTab("BOOK")
                                    },
                                    onClearHistory = { viewModel.clearRideHistory() }
                                )
                            }

                            "SAVED" -> {
                                WalletAndSavedPlacesScreen(
                                    walletBalance = uiState.walletBalance,
                                    savedPlaces = savedPlaces,
                                    onAddFunds = { viewModel.addWalletFunds(it) },
                                    onAddSavedPlace = { label, addr, icon -> viewModel.addSavedPlace(label, addr, icon) },
                                    onDeleteSavedPlace = { viewModel.deleteSavedPlace(it) }
                                )
                            }

                            "CAPTAIN" -> {
                                CaptainModeScreen(
                                    driverOnline = uiState.driverOnline,
                                    earningsToday = uiState.driverEarningsToday,
                                    completedRides = uiState.driverCompletedRides,
                                    onToggleOnline = { viewModel.toggleDriverOnline() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RapidoTopAppBar(
    walletBalance: Double,
    activeTab: String,
    onWalletClick: () -> Unit
) {
    Surface(
        color = RapidoDark,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Branded Rapido Icon Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RapidoYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = "Rapido",
                        tint = RapidoDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "rapido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = RapidoYellow,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "BIKE TAXI & AUTO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Wallet Balance Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF2A2A2A),
                modifier = Modifier
                    .testTag("top_app_bar_wallet")
                    .clickable { onWalletClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Balance",
                        tint = RapidoYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹${walletBalance.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun RapidoBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = RapidoDark,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            Triple("BOOK", "Ride", Icons.Default.DirectionsBike),
            Triple("HISTORY", "My Trips", Icons.Default.History),
            Triple("SAVED", "Wallet", Icons.Default.AccountBalanceWallet),
            Triple("CAPTAIN", "Captain Mode", Icons.Default.SportsMotorsports)
        )

        items.forEach { (tabId, label, icon) ->
            val isSelected = activeTab == tabId
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tabId) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RapidoDark,
                    selectedTextColor = RapidoYellow,
                    indicatorColor = RapidoYellow,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_tab_$tabId")
            )
        }
    }
}
