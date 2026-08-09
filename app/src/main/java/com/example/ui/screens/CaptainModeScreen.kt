package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*

@Composable
fun CaptainModeScreen(
    driverOnline: Boolean,
    earningsToday: Double,
    completedRides: Int,
    onToggleOnline: () -> Unit,
    modifier: Modifier = Modifier
) {
    var incomingOrderAccepted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RapidoDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rapido Captain Portal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Honda Shine • KA 05 EQ 8821",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (driverOnline) RapidoGreenContainer else Color(0xFF333333),
                    modifier = Modifier.clickable { onToggleOnline() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (driverOnline) RapidoGreen else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (driverOnline) "ONLINE" else "OFFLINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (driverOnline) RapidoGreen else Color.White
                        )
                    }
                }
            }
        }

        // Today's Earnings Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TODAY'S EARNINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoYellow
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "₹${earningsToday.toInt()}.00",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Trips Completed", fontSize = 11.sp, color = Color.LightGray)
                            Text("$completedRides Rides", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Column {
                            Text("Acceptance Rate", fontSize = 11.sp, color = Color.LightGray)
                            Text("98%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RapidoGreen)
                        }

                        Column {
                            Text("Rating", fontSize = 11.sp, color = Color.LightGray)
                            Text("4.92 ★", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RapidoYellow)
                        }
                    }
                }
            }
        }

        // Daily Incentive Bonus Goal
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RapidoYellowContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Bonus",
                        tint = RapidoDark,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Captain Bonus Target!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RapidoDark
                        )
                        Text(
                            text = "Complete 2 more rides today to unlock ₹150 cash bonus",
                            fontSize = 11.sp,
                            color = RapidoDark
                        )
                    }
                }
            }
        }

        // Incoming Trip Request Simulation
        item {
            Text(
                text = "INCOMING TRIP ORDERS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
        }

        item {
            if (!driverOnline) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You are Offline. Go Online to receive nearby ride requests!",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (incomingOrderAccepted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A29)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Accepted", tint = RapidoGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TRIP ACCEPTED!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Head to Pickup Location: HSR Layout Sector 4", color = Color.LightGray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { incomingOrderAccepted = false },
                            colors = ButtonDefaults.buttonColors(containerColor = RapidoYellow, contentColor = RapidoDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("COMPLETE TRIP & COLLECT ₹65", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, RapidoYellow, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(8.dp), color = RapidoYellow) {
                                Text(
                                    text = "BIKE TAXI REQUEST",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RapidoDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "₹65.00",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RapidoGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Pickup: Koramangala 5th Block (1.2 km away)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Drop: Indiranagar 100 Feet Rd (4.5 km)",
                            fontSize = 13.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("DECLINE", color = Color.LightGray)
                            }

                            Button(
                                onClick = { incomingOrderAccepted = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RapidoYellow, contentColor = RapidoDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ACCEPT RIDE", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
