package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.RideEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RideHistoryScreen(
    rides: List<RideEntity>,
    onRebookRide: (RideEntity) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredRides = remember(rides, selectedFilter) {
        if (selectedFilter == "ALL") rides
        else rides.filter { it.vehicleType.uppercase().contains(selectedFilter) }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RapidoBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Trips & History",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RapidoDark
            )

            if (rides.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Text("Clear All", color = RapidoRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf("ALL" to "All Rides", "BIKE" to "Bike Taxi", "AUTO" to "Auto", "CAB" to "Cab")
            items(filters) { (key, label) ->
                val isSelected = selectedFilter == key
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) RapidoDark else Color.White,
                    modifier = Modifier
                        .border(1.dp, if (isSelected) RapidoDark else RapidoBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else RapidoDark,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredRides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBike,
                        contentDescription = "No Rides",
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No ride history found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoTextSecondary
                    )
                    Text(
                        text = "Book a Rapido bike or auto to see your trips here!",
                        fontSize = 12.sp,
                        color = RapidoTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredRides) { ride ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ride_history_item_${ride.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = RapidoYellow
                                    ) {
                                        Text(
                                            text = ride.vehicleType,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = RapidoDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = RapidoGreenContainer
                                    ) {
                                        Text(
                                            text = ride.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RapidoGreen,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "₹${ride.fare.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RapidoDark
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pickup -> Drop
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(RapidoGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ride.pickupLocation,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RapidoDark,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(RapidoRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ride.dropLocation,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RapidoDark,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = RapidoBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Captain: ${ride.captainName} (${ride.vehicleNumber})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = RapidoTextSecondary
                                    )
                                    Text(
                                        text = dateFormatter.format(Date(ride.timestamp)),
                                        fontSize = 11.sp,
                                        color = RapidoTextSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = RapidoBg,
                                    modifier = Modifier
                                        .border(1.dp, RapidoBorder, RoundedCornerShape(12.dp))
                                        .clickable { onRebookRide(ride) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay,
                                            contentDescription = "Rebook",
                                            tint = RapidoDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Rebook",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RapidoDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
