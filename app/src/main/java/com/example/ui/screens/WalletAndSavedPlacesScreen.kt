package com.example.ui.screens

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
import com.example.data.SavedPlaceEntity
import com.example.ui.theme.*

@Composable
fun WalletAndSavedPlacesScreen(
    walletBalance: Double,
    savedPlaces: List<SavedPlaceEntity>,
    onAddFunds: (Double) -> Unit,
    onAddSavedPlace: (String, String, String) -> Unit,
    onDeleteSavedPlace: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("HOME") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RapidoBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Wallet & Saved Addresses",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RapidoDark
            )
        }

        // Rapido Wallet Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RapidoDark),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RapidoYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Wallet",
                                    tint = RapidoDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "RAPIDO WALLET",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RapidoYellow
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF333333)
                        ) {
                            Text(
                                text = "Auto-Deduct ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Available Balance",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Text(
                        text = "₹${walletBalance.toInt()}.00",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "QUICK ADD MONEY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(100.0, 200.0, 500.0).forEach { amount ->
                            Button(
                                onClick = { onAddFunds(amount) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RapidoYellow,
                                    contentColor = RapidoDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "+₹${amount.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Saved Places Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAVED ADDRESSES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RapidoTextSecondary
                )

                Button(
                    onClick = { showAddPlaceDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RapidoDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Address",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Saved Places List
        items(savedPlaces) { place ->
            val icon = when (place.iconType) {
                "HOME" -> Icons.Default.Home
                "WORK" -> Icons.Default.Work
                else -> Icons.Default.Star
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saved_address_item_${place.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RapidoYellowContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = place.label,
                            tint = RapidoDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = place.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = RapidoDark
                        )
                        Text(
                            text = place.address,
                            fontSize = 12.sp,
                            color = RapidoTextSecondary
                        )
                    }

                    IconButton(onClick = { onDeleteSavedPlace(place.id) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = RapidoRed
                        )
                    }
                }
            }
        }
    }

    // Add Saved Place Dialog
    if (showAddPlaceDialog) {
        AlertDialog(
            onDismissRequest = { showAddPlaceDialog = false },
            title = {
                Text(
                    text = "Add Saved Address",
                    fontWeight = FontWeight.Bold,
                    color = RapidoDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        label = { Text("Label (e.g. Gym, Parents Home)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAddress,
                        onValueChange = { newAddress = it },
                        label = { Text("Full Address") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Icon Category:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("HOME", "WORK", "FAVORITE").forEach { cat ->
                            val isSel = newType == cat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) RapidoYellow else RapidoBg,
                                modifier = Modifier.clickable { newType = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RapidoDark,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLabel.isNotBlank() && newAddress.isNotBlank()) {
                            onAddSavedPlace(newLabel, newAddress, newType)
                            newLabel = ""
                            newAddress = ""
                            showAddPlaceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RapidoYellow, contentColor = RapidoDark)
                ) {
                    Text("SAVE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPlaceDialog = false }) {
                    Text("CANCEL", color = RapidoDark)
                }
            }
        )
    }
}
