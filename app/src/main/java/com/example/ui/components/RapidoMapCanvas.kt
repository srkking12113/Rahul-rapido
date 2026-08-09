package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocationPoint
import com.example.model.RideStep
import com.example.model.VehicleType
import com.example.ui.theme.*

@Composable
fun RapidoMapCanvas(
    pickup: LocationPoint,
    drop: LocationPoint?,
    rideStep: RideStep,
    selectedVehicleType: VehicleType,
    tripProgress: Float,
    isSearching: Boolean,
    modifier: Modifier = Modifier,
    onRecenterClick: () -> Unit = {}
) {
    // Pulse animation for pickup marker & searching radar
    val infiniteTransition = rememberInfiniteTransition(label = "map_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // TextMeasurer for map labels
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw Map Base Canvas (Parks, Lakes, Blocks)
            drawRect(color = Color(0xFFE8ECEF), size = size)

            // Parks / Greenery
            drawRoundRect(
                color = Color(0xFFD4E1D4),
                topLeft = Offset(width * 0.05f, height * 0.10f),
                size = Size(width * 0.28f, height * 0.22f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f)
            )
            drawRoundRect(
                color = Color(0xFFD4E1D4),
                topLeft = Offset(width * 0.65f, height * 0.60f),
                size = Size(width * 0.30f, height * 0.25f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f)
            )

            // Lake / Water body
            val lakePath = Path().apply {
                moveTo(width * 0.70f, height * 0.15f)
                cubicTo(width * 0.85f, height * 0.12f, width * 0.95f, height * 0.25f, width * 0.80f, height * 0.35f)
                cubicTo(width * 0.65f, height * 0.40f, width * 0.60f, height * 0.20f, width * 0.70f, height * 0.15f)
                close()
            }
            drawPath(path = lakePath, color = Color(0xFFB3E5FC))

            // 2. Draw Road Grid Network
            val roadColorMain = Color(0xFFFFFFFF)
            val roadOutlineMain = Color(0xFFCFD8DC)
            val highwayColor = Color(0xFFFFECB3) // Yellow tint highway

            // Main East-West Highways
            val roadY1 = height * 0.30f
            val roadY2 = height * 0.65f
            drawLine(highwayColor, Offset(0f, roadY1), Offset(width, roadY1), strokeWidth = 28f)
            drawLine(roadOutlineMain, Offset(0f, roadY1 - 15f), Offset(width, roadY1 - 15f), strokeWidth = 2f)
            drawLine(roadOutlineMain, Offset(0f, roadY1 + 15f), Offset(width, roadY1 + 15f), strokeWidth = 2f)

            drawLine(roadColorMain, Offset(0f, roadY2), Offset(width, roadY2), strokeWidth = 22f)
            drawLine(roadOutlineMain, Offset(0f, roadY2 - 12f), Offset(width, roadY2 - 12f), strokeWidth = 2f)
            drawLine(roadOutlineMain, Offset(0f, roadY2 + 12f), Offset(width, roadY2 + 12f), strokeWidth = 2f)

            // North-South Arterial Roads
            val roadX1 = width * 0.30f
            val roadX2 = width * 0.75f
            drawLine(roadColorMain, Offset(roadX1, 0f), Offset(roadX1, height), strokeWidth = 22f)
            drawLine(roadOutlineMain, Offset(roadX1 - 12f, 0f), Offset(roadX1 - 12f, height), strokeWidth = 2f)
            drawLine(roadOutlineMain, Offset(roadX1 + 12f, 0f), Offset(roadX1 + 12f, height), strokeWidth = 2f)

            drawLine(highwayColor, Offset(roadX2, 0f), Offset(roadX2, height), strokeWidth = 28f)
            drawLine(roadOutlineMain, Offset(roadX2 - 15f, 0f), Offset(roadX2 - 15f, height), strokeWidth = 2f)
            drawLine(roadOutlineMain, Offset(roadX2 + 15f, 0f), Offset(roadX2 + 15f, height), strokeWidth = 2f)

            // Secondary Diagonal Connection Road
            drawLine(roadColorMain, Offset(0f, height * 0.85f), Offset(width * 0.85f, 0f), strokeWidth = 16f)

            // 3. Pickup & Drop Off Coordinates
            val pickupPx = Offset(width * pickup.x, height * pickup.y)
            val dropPx = drop?.let { Offset(width * it.x, height * it.y) }
                ?: Offset(width * 0.75f, height * 0.30f)

            // 4. Draw Route Line if Drop Location set
            if (drop != null || rideStep != RideStep.LOCATION_SELECT) {
                val routePath = Path().apply {
                    moveTo(pickupPx.x, pickupPx.y)
                    // Road bending curve
                    val midX = (pickupPx.x + dropPx.x) / 2
                    val midY = (pickupPx.y + dropPx.y) / 2
                    cubicTo(
                        midX + 60f, pickupPx.y,
                        dropPx.x - 60f, midY,
                        dropPx.x, dropPx.y
                    )
                }

                // Shadow line
                drawPath(
                    path = routePath,
                    color = Color(0x33000000),
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                // Route Outer Glow / Solid Line
                drawPath(
                    path = routePath,
                    color = RapidoYellowDark,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )

                // Route Inner Dash
                drawPath(
                    path = routePath,
                    color = RapidoDark,
                    style = Stroke(
                        width = 4f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )
            }

            // 5. Draw Radar Pulse when searching for Captain
            if (isSearching) {
                drawCircle(
                    color = RapidoYellow.copy(alpha = pulseAlpha),
                    radius = pulseRadius * 2.5f,
                    center = pickupPx
                )
                drawCircle(
                    color = RapidoYellowDark,
                    radius = 8f,
                    center = pickupPx
                )
            }

            // 6. Draw Pickup Location Pin
            drawCircle(
                color = RapidoGreen.copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = pickupPx
            )
            drawCircle(
                color = RapidoGreen,
                radius = 16f,
                center = pickupPx
            )
            drawCircle(
                color = Color.White,
                radius = 7f,
                center = pickupPx
            )

            // Pickup Tag Label
            val pickupLayout = textMeasurer.measure(
                text = AnnotatedString("PICKUP"),
                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
            val labelPadding = 12f
            val labelWidth = pickupLayout.size.width + (labelPadding * 2)
            val labelHeight = pickupLayout.size.height + (labelPadding)
            drawRoundRect(
                color = RapidoDark,
                topLeft = Offset(pickupPx.x - (labelWidth / 2), pickupPx.y - 50f - labelHeight),
                size = Size(labelWidth, labelHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
            )
            drawText(
                textLayoutResult = pickupLayout,
                topLeft = Offset(pickupPx.x - (pickupLayout.size.width / 2), pickupPx.y - 50f - labelHeight + (labelPadding / 2))
            )

            // 7. Draw Drop Location Pin if Drop set
            if (drop != null) {
                drawCircle(
                    color = RapidoRed,
                    radius = 18f,
                    center = dropPx
                )
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = dropPx
                )

                val dropLayout = textMeasurer.measure(
                    text = AnnotatedString("DROP"),
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                val dropWidth = dropLayout.size.width + (labelPadding * 2)
                val dropHeight = dropLayout.size.height + (labelPadding)
                drawRoundRect(
                    color = RapidoRed,
                    topLeft = Offset(dropPx.x - (dropWidth / 2), dropPx.y - 50f - dropHeight),
                    size = Size(dropWidth, dropHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
                )
                drawText(
                    textLayoutResult = dropLayout,
                    topLeft = Offset(dropPx.x - (dropLayout.size.width / 2), dropPx.y - 50f - dropHeight + (labelPadding / 2))
                )
            }

            // 8. Draw Nearby Available Rapido Captain Vehicles on Map
            if (rideStep == RideStep.LOCATION_SELECT || rideStep == RideStep.VEHICLE_SELECT) {
                val vehicleOffsets = listOf(
                    Offset(width * 0.28f, height * 0.60f),
                    Offset(width * 0.35f, height * 0.40f),
                    Offset(width * 0.60f, height * 0.68f),
                    Offset(width * 0.22f, height * 0.32f)
                )

                vehicleOffsets.forEach { pos ->
                    drawCircle(color = RapidoDark, radius = 12f, center = pos)
                    drawCircle(color = RapidoYellow, radius = 9f, center = pos)
                }
            }

            // 9. Draw Active Captain Movement during IN_TRIP or CAPTAIN_ASSIGNED
            if (rideStep == RideStep.IN_TRIP || rideStep == RideStep.CAPTAIN_ASSIGNED) {
                val currProgress = if (rideStep == RideStep.CAPTAIN_ASSIGNED) 0.05f else tripProgress.coerceIn(0f, 1f)
                val captainX = pickupPx.x + (dropPx.x - pickupPx.x) * currProgress
                val captainY = pickupPx.y + (dropPx.y - pickupPx.y) * currProgress
                val currentCaptainPos = Offset(captainX, captainY)

                // Captain Vehicle Pulsing Halo
                drawCircle(
                    color = RapidoYellow.copy(alpha = 0.4f),
                    radius = 32f,
                    center = currentCaptainPos
                )
                drawCircle(
                    color = RapidoDark,
                    radius = 18f,
                    center = currentCaptainPos
                )
                drawCircle(
                    color = RapidoYellow,
                    radius = 12f,
                    center = currentCaptainPos
                )

                // Live Speed / Status Badge
                val speedText = if (currProgress >= 0.98f) "ARRIVED!" else "${(28 + (currProgress * 15)).toInt()} km/h"
                val speedLayout = textMeasurer.measure(
                    text = AnnotatedString(speedText),
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RapidoDark)
                )
                drawRoundRect(
                    color = RapidoYellow,
                    topLeft = Offset(currentCaptainPos.x - (speedLayout.size.width / 2) - 8f, currentCaptainPos.y + 24f),
                    size = Size(speedLayout.size.width + 16f, speedLayout.size.height + 8f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )
                drawText(
                    textLayoutResult = speedLayout,
                    topLeft = Offset(currentCaptainPos.x - (speedLayout.size.width / 2), currentCaptainPos.y + 28f)
                )
            }
        }

        // Top Overlay: High Accuracy GPS Chip
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(RapidoDark.copy(alpha = 0.85f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RapidoGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "GPS High Accuracy",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Right Floating Controls: Recenter Map & Compass
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            SmallFloatingActionButton(
                onClick = onRecenterClick,
                containerColor = Color.White,
                contentColor = RapidoDark,
                modifier = Modifier.shadow(4.dp, CircleShape)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter Map")
            }

            Spacer(modifier = Modifier.height(8.dp))

            SmallFloatingActionButton(
                onClick = { },
                containerColor = Color.White,
                contentColor = RapidoDark,
                modifier = Modifier.shadow(4.dp, CircleShape)
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Map Style")
            }
        }
    }
}
