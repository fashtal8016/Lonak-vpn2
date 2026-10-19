package com.example.ui.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.VpnStatus
import com.example.ui.components.ActiveServerBanner
import com.example.ui.components.LiveTrafficMeters
import com.example.ui.components.SimurghConnectButton
import com.example.ui.components.SimurghHeaderBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianLapisPrimary
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseGlow

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToServers: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onPrepareVpn: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vpnStatus by viewModel.vpnStatus.collectAsStateWithLifecycle()
    val trafficStats by viewModel.trafficStats.collectAsStateWithLifecycle()
    val activeServer by viewModel.activeServer.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Bar with Simurgh Emblem
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SimurghHeaderBadge()
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "لونک VPN",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "پرواز آزاد در اینترنت • سیمرغ آزادی",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PersianTurquoiseGlow,
                        fontSize = 12.sp
                    )
                }
            }

            // Quick Shield Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Protection",
                        tint = if (vpnStatus == VpnStatus.CONNECTED) PersianTurquoise else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (vpnStatus == VpnStatus.CONNECTED) "ایمن" else "محافظت‌نشده",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (vpnStatus == VpnStatus.CONNECTED) PersianTurquoise else Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Large Simurgh Connect Button
        SimurghConnectButton(
            status = vpnStatus,
            durationText = trafficStats.formatDuration(),
            onClick = {
                viewModel.toggleVpn(context, onPrepareVpn)
            }
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Real-Time Speed Meters
        LiveTrafficMeters(stats = trafficStats)

        Spacer(modifier = Modifier.height(18.dp))

        // Active Server Banner
        ActiveServerBanner(
            server = activeServer,
            onSelectClicked = onNavigateToServers
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    viewModel.quickConnect(context, onPrepareVpn)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PersianLapisPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quick_connect_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Quick Connect",
                    tint = PersianGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "اتصال هوشمند",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = onNavigateToScanner,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("scanner_shortcut_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Scanner",
                    tint = PersianTurquoise,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "اسکنر سرور",
                    color = PersianTurquoise,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
