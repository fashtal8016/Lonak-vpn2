package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.data.local.entity.ServerEntity
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LonakError
import com.example.ui.theme.LonakSuccess
import com.example.ui.theme.LonakWarning
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianLapisPrimary
import com.example.ui.theme.PersianTurquoise

@Composable
fun ActiveServerBanner(
    server: ServerEntity?,
    onSelectClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.5.dp, if (server != null) PersianTurquoise.copy(alpha = 0.4f) else DarkSurfaceVariant, RoundedCornerShape(20.dp))
            .clickable(onClick = onSelectClicked)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Flag or placeholder
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PersianLapisPrimary.copy(alpha = 0.4f))
            ) {
                Text(
                    text = server?.getFlagEmoji() ?: "🌐",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server?.name ?: "سروری انتخاب نشده است",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (server != null) {
                        ProtocolBadge(protocolName = server.protocol.displayName, colorHex = server.protocol.badgeColorHex)
                        PingBadge(pingMs = server.pingMs)
                        if (server.isCleanIp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Clean IP",
                                    tint = PersianTurquoise,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "IP تمیز",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PersianTurquoise,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "برای انتخاب کلیک کنید",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select Server",
                tint = PersianGold,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ServerItemCard(
    server: ServerEntity,
    isSelected: Boolean,
    isTesting: Boolean,
    onConnectClick: () -> Unit,
    onTestPingClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) PersianTurquoise else DarkSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onConnectClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Flag Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
            ) {
                Text(
                    text = server.getFlagEmoji(),
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProtocolBadge(protocolName = server.protocol.displayName, colorHex = server.protocol.badgeColorHex)
                    PingBadge(pingMs = server.pingMs)

                    if (server.tag.isNotEmpty() && server.tag != "NEW") {
                        TagBadge(tag = server.tag)
                    }
                }
            }

            // Ping test button
            IconButton(
                onClick = onTestPingClick,
                modifier = Modifier.size(36.dp)
            ) {
                if (isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = PersianTurquoise,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.NetworkPing,
                        contentDescription = "Test Ping",
                        tint = PersianTurquoise,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Favorite button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Favorite",
                    tint = if (server.isFavorite) PersianGold else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ProtocolBadge(protocolName: String, colorHex: Long) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(colorHex).copy(alpha = 0.18f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = protocolName,
            style = MaterialTheme.typography.labelSmall,
            color = Color(colorHex),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PingBadge(pingMs: Int) {
    val (color, text) = when {
        pingMs in 1..120 -> LonakSuccess to "${pingMs}ms"
        pingMs in 121..250 -> LonakWarning to "${pingMs}ms"
        pingMs > 250 -> LonakError to "${pingMs}ms"
        else -> Color.Gray to "-- ms"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TagBadge(tag: String) {
    val label = when (tag) {
        "FAST" -> "⚡ سریع"
        "STABLE" -> "🔒 پایدار"
        "TESTED" -> "🧪 تست‌شده"
        else -> tag
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(PersianGold.copy(alpha = 0.18f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PersianGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
