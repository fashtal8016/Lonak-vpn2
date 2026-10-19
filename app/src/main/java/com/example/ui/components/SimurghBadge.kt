package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.ripple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.model.VpnStatus
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LonakError
import com.example.ui.theme.LonakSuccess
import com.example.ui.theme.LonakWarning
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianLapisPrimary
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseGlow

@Composable
fun SimurghConnectButton(
    status: VpnStatus,
    durationText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == VpnStatus.CONNECTED) 1.08f else if (status == VpnStatus.CONNECTING) 1.05f else 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (status == VpnStatus.CONNECTED) 0.65f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val (haloColor, buttonGradient) = when (status) {
        VpnStatus.CONNECTED -> PersianTurquoise to listOf(PersianTurquoise, PersianLapisPrimary)
        VpnStatus.CONNECTING -> PersianGold to listOf(PersianGold, PersianLapisPrimary)
        VpnStatus.DISCONNECTING -> LonakWarning to listOf(LonakWarning, DarkSurface)
        VpnStatus.ERROR -> LonakError to listOf(LonakError, DarkSurface)
        VpnStatus.DISCONNECTED -> PersianLapisPrimary to listOf(PersianLapisPrimary, DarkSurface)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(230.dp)
        ) {
            // Outer Pulsating Glow
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(haloColor.copy(alpha = glowAlpha * 0.4f))
            )

            // Isfahan Geometric Border Ring
            Box(
                modifier = Modifier
                    .size(196.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                PersianTurquoise,
                                PersianGold,
                                PersianTurquoiseGlow,
                                PersianLapisPrimary,
                                PersianTurquoise
                            )
                        ),
                        shape = CircleShape
                    )
                    .background(DarkBackground)
            )

            // Inner Interactive Action Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .shadow(16.dp, CircleShape, spotColor = haloColor)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(buttonGradient))
                    .testTag("vpn_toggle_button")
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = PersianGold),
                        onClick = onClick
                    )
            ) {
                // Central Simurgh & Power Symbol
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (status == VpnStatus.CONNECTING) {
                        CircularProgressIndicator(
                            color = PersianGold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(46.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "VPN Power Toggle",
                            tint = if (status == VpnStatus.CONNECTED) Color.White else PersianTurquoiseGlow,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (status) {
                            VpnStatus.CONNECTED -> "قطع اتصال"
                            VpnStatus.CONNECTING -> "در حال اتصال…"
                            VpnStatus.DISCONNECTING -> "در حال قطع…"
                            VpnStatus.ERROR -> "خطا در اتصال"
                            VpnStatus.DISCONNECTED -> "اتصال"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    if (status == VpnStatus.CONNECTED && durationText.isNotEmpty()) {
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.labelSmall,
                            color = PersianGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimurghHeaderBadge(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .border(1.5.dp, PersianGold, CircleShape)
            .background(DarkSurface)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_simurgh_logo),
            contentDescription = "Lonak Simurgh Icon",
            modifier = Modifier.size(42.dp)
        )
    }
}
