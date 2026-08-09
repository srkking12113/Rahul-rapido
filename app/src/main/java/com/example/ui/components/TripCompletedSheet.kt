package com.example.ui.components

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
import com.example.data.RideEntity
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TripCompletedSheet(
    ride: RideEntity?,
    ratingGiven: Float,
    feedbackSubmitted: Boolean,
    onRatingSelected: (Float) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTip by remember { mutableStateOf(0) }
    val feedbackTags = listOf("On Time", "Clean Helmet", "Safe Driving", "Polite Captain", "Smooth Route")
    val selectedTags = remember { mutableStateListOf<String>() }

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

            Spacer(modifier = Modifier.height(16.dp))

            // Success Checkmark
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(RapidoGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ride Completed",
                    tint = RapidoGreen,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You Reached Safely!",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RapidoDark
            )

            Text(
                text = "Thank you for riding with Rapido",
                fontSize = 13.sp,
                color = RapidoTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fare Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = RapidoBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Paid (${ride?.paymentMethod ?: "Wallet"})",
                                fontSize = 12.sp,
                                color = RapidoTextSecondary
                            )
                            Text(
                                text = "₹${ride?.fare?.toInt() ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RapidoDark
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RapidoYellow
                        ) {
                            Text(
                                text = ride?.vehicleType ?: "Bike Taxi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RapidoDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = RapidoBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${ride?.pickupLocation} → ${ride?.dropLocation}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoDark,
                        maxLines = 1
                    )
                    Text(
                        text = "Captain: ${ride?.captainName ?: "Captain"} (${ride?.vehicleNumber})",
                        fontSize = 12.sp,
                        color = RapidoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Stars
            Text(
                text = "Rate Captain ${ride?.captainName ?: ""}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { starIndex ->
                    Icon(
                        imageVector = if (starIndex <= ratingGiven) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star $starIndex",
                        tint = if (starIndex <= ratingGiven) Color(0xFFFFA000) else Color.LightGray,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onRatingSelected(starIndex.toFloat()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feedback Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                feedbackTags.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) RapidoYellowContainer else RapidoBg,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = if (isSelected) RapidoYellowDark else RapidoBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            }
                    ) {
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = RapidoDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tip Captain Option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Tip for Captain:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RapidoDark
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 10, 20, 50).forEach { tip ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedTip == tip) RapidoYellow else RapidoBg,
                            modifier = Modifier.clickable { selectedTip = tip }
                        ) {
                            Text(
                                text = if (tip == 0) "None" else "₹$tip",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RapidoDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDoneClick,
                colors = ButtonDefaults.buttonColors(containerColor = RapidoYellow, contentColor = RapidoDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("done_trip_button")
            ) {
                Text(
                    text = "DONE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
