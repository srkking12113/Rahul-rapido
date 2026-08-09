package com.example.ui.components

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
import com.example.data.SavedPlaceEntity
import com.example.model.LocationPoint
import com.example.ui.theme.*

@Composable
fun LocationSelectorSheet(
    pickup: LocationPoint,
    popularLocations: List<LocationPoint>,
    savedPlaces: List<SavedPlaceEntity>,
    onSelectDropLocation: (LocationPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredLocations = remember(searchQuery, popularLocations) {
        if (searchQuery.isBlank()) {
            popularLocations
        } else {
            popularLocations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.address.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Drag Indicator Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Pickup Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RapidoBg)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(RapidoGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Pickup Location",
                        tint = RapidoGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PICKUP LOCATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoTextSecondary
                    )
                    Text(
                        text = pickup.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RapidoTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.border(1.dp, RapidoBorder, RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "Change",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark,
                        modifier = Modifier
                            .clickable { }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Destination Field ("Where are you going?")
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Where are you going?",
                        fontWeight = FontWeight.Medium,
                        color = RapidoTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = RapidoYellowDark
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RapidoYellowDark,
                    unfocusedBorderColor = RapidoBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("destination_search_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Saved Places Chips Shortcuts
            Text(
                text = "SAVED PLACES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(savedPlaces) { place ->
                    val placeIcon = when (place.iconType) {
                        "HOME" -> Icons.Default.Home
                        "WORK" -> Icons.Default.Work
                        else -> Icons.Default.Star
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = RapidoYellowContainer,
                        modifier = Modifier
                            .testTag("saved_place_chip_${place.label}")
                            .clickable {
                                onSelectDropLocation(
                                    LocationPoint(
                                        name = place.label,
                                        address = place.address,
                                        x = 0.70f,
                                        y = 0.35f
                                    )
                                )
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = placeIcon,
                                contentDescription = place.label,
                                tint = RapidoDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = place.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RapidoDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Popular Destinations List
            Text(
                text = "POPULAR DESTINATIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLocations) { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectDropLocation(location) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(RapidoBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Location",
                                tint = RapidoRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = location.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RapidoTextPrimary
                            )
                            Text(
                                text = location.address,
                                fontSize = 12.sp,
                                color = RapidoTextSecondary,
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Select",
                            tint = RapidoTextSecondary
                        )
                    }
                    Divider(color = RapidoBorder.copy(alpha = 0.5f))
                }
            }
        }
    }
}
