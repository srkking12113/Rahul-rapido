package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun VehicleSelectionSheet(
    pickup: LocationPoint,
    drop: LocationPoint,
    selectedVehicleType: VehicleType,
    appliedPromo: PromoCode?,
    selectedPayment: PaymentMethodOption,
    walletBalance: Double,
    distanceKm: Double,
    availablePromos: List<PromoCode>,
    onSelectVehicle: (VehicleType) -> Unit,
    onApplyPromo: (PromoCode?) -> Unit,
    onSelectPayment: (PaymentMethodOption) -> Unit,
    onBookClick: () -> Unit,
    onChangeLocationClick: () -> Unit,
    calculateFare: (VehicleType) -> Double,
    calculateEta: (VehicleType) -> Int,
    modifier: Modifier = Modifier
) {
    var showPromoDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Route Summary Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(RapidoBg)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBike,
                    contentDescription = "Ride Route",
                    tint = RapidoYellowDark,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${pickup.name} → ${drop.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "$distanceKm km • Direct Route",
                        fontSize = 12.sp,
                        color = RapidoTextSecondary
                    )
                }

                TextButton(onClick = onChangeLocationClick) {
                    Text(
                        text = "Edit",
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CHOOSE RIDE TYPE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Vehicle Type Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VehicleType.values().forEach { vehicle ->
                    val isSelected = vehicle == selectedVehicleType
                    val fare = calculateFare(vehicle)
                    val eta = calculateEta(vehicle)

                    val vehicleIcon = when (vehicle) {
                        VehicleType.BIKE_TAXI -> Icons.Default.TwoWheeler
                        VehicleType.AUTO -> Icons.Default.ElectricRickshaw
                        VehicleType.CAB_ECONOMY -> Icons.Default.DirectionsCar
                        VehicleType.PARCEL_EXPRESS -> Icons.Default.LocalShipping
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) RapidoYellowContainer else Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) RapidoYellowDark else RapidoBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("vehicle_card_${vehicle.name}")
                            .clickable { onSelectVehicle(vehicle) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) RapidoYellow else RapidoBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vehicleIcon,
                                    contentDescription = vehicle.title,
                                    tint = RapidoDark,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = vehicle.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RapidoDark
                                    )
                                    if (vehicle == VehicleType.BIKE_TAXI) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = RapidoDark
                                        ) {
                                            Text(
                                                text = "FASTEST",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RapidoYellow,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = vehicle.subtitle,
                                    fontSize = 11.sp,
                                    color = RapidoTextSecondary,
                                    maxLines = 1
                                )

                                Text(
                                    text = "$eta mins away • ${vehicle.capacity}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = RapidoGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${fare.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RapidoDark
                                )
                                if (appliedPromo != null) {
                                    Text(
                                        text = "Offer applied",
                                        fontSize = 10.sp,
                                        color = RapidoGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Coupon / Offers Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RapidoBg)
                    .clickable { showPromoDialog = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = "Promo Code",
                    tint = RapidoYellowDark,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (appliedPromo != null) "Coupon: ${appliedPromo.code} applied" else "Apply Coupon / Promo Code",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (appliedPromo != null) RapidoGreen else RapidoDark,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (appliedPromo != null) "CHANGE" else "VIEW OFFERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RapidoDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Option Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, RapidoBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Payment Method",
                    tint = RapidoDark,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedPayment.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark
                    )
                    if (selectedPayment.id == "WALLET") {
                        Text(
                            text = "Balance: ₹${walletBalance.toInt()}",
                            fontSize = 11.sp,
                            color = RapidoTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Select Payment",
                    tint = RapidoTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Booking Button
            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RapidoYellow,
                    contentColor = RapidoDark
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("book_ride_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "BOOK ${selectedVehicleType.title.uppercase()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ₹${calculateFare(selectedVehicleType).toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    // Promo Code Selection Dialog
    if (showPromoDialog) {
        AlertDialog(
            onDismissRequest = { showPromoDialog = false },
            title = {
                Text(
                    text = "Apply Coupon Code",
                    fontWeight = FontWeight.Bold,
                    color = RapidoDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    availablePromos.forEach { promo ->
                        val isApplied = appliedPromo?.code == promo.code
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isApplied) RapidoGreenContainer else RapidoBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onApplyPromo(if (isApplied) null else promo)
                                    showPromoDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = promo.code,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = RapidoDark
                                    )
                                    Text(
                                        text = promo.description,
                                        fontSize = 12.sp,
                                        color = RapidoTextSecondary
                                    )
                                }
                                Text(
                                    text = if (isApplied) "APPLIED" else "APPLY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isApplied) RapidoGreen else RapidoDark
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPromoDialog = false }) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = RapidoDark)
                }
            }
        )
    }
}
