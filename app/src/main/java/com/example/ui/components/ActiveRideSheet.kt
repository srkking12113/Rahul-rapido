package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.Captain
import com.example.model.LocationPoint
import com.example.model.RideStep
import com.example.model.VehicleType
import com.example.ui.theme.*

@Composable
fun SearchingCaptainSheet(
    vehicleType: VehicleType,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Searching Radar Ring
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(RapidoYellowContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = RapidoYellowDark,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(60.dp)
                )
                Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = "Searching",
                    tint = RapidoDark,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connecting to Nearby Captain...",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RapidoDark
            )

            Text(
                text = "Assigning best ${vehicleType.title} captain in 1.5 km radius",
                fontSize = 13.sp,
                color = RapidoTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Safety Helmet Assurance Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(RapidoGreenContainer)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Safety Pledge",
                    tint = RapidoGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "100% Sanitized Helmet Provided",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoGreen
                    )
                    Text(
                        text = "Safety cap provided for every ride",
                        fontSize = 11.sp,
                        color = RapidoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onCancelClick,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, RapidoRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("cancel_ride_button")
            ) {
                Text(
                    text = "CANCEL RIDE REQUEST",
                    color = RapidoRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActiveRideSheet(
    rideStep: RideStep,
    captain: Captain,
    otp: String,
    vehicleType: VehicleType,
    pickup: LocationPoint,
    drop: LocationPoint,
    tripProgress: Float,
    fare: Double,
    onCancelRide: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (rideStep == RideStep.IN_TRIP) RapidoGreenContainer else RapidoYellowContainer
                ) {
                    Text(
                        text = if (rideStep == RideStep.IN_TRIP) "TRIP IN PROGRESS" else "CAPTAIN ARRIVING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (rideStep == RideStep.IN_TRIP) RapidoGreen else RapidoDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // OTP Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RapidoYellow,
                    modifier = Modifier.border(1.dp, RapidoYellowDark, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OTP: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = RapidoDark
                        )
                        Text(
                            text = otp,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RapidoDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Captain Details Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RapidoBg)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Captain Avatar
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(RapidoYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Captain",
                        tint = RapidoDark,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = captain.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${captain.rating} • ${captain.totalRides} rides",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = RapidoTextSecondary
                        )
                    }
                    Text(
                        text = "${captain.vehicleName} • ${captain.vehicleNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RapidoDark
                    )
                }

                // Call & Message Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = RapidoGreen,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Captain",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(20.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = RapidoDark,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "Message Captain",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trip Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Destination: ${drop.name}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark
                    )
                    Text(
                        text = "${(tripProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoGreen
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = tripProgress,
                    color = RapidoGreen,
                    trackColor = RapidoBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety & Emergency SOS Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RapidoBg,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Ride",
                            tint = RapidoDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Trip",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RapidoDark
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RapidoRedContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Emergency SOS",
                            tint = RapidoRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Emergency 112",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RapidoRed
                        )
                    }
                }
            }

            if (rideStep == RideStep.CAPTAIN_ASSIGNED) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onCancelRide,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel Ride", color = RapidoRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
