package com.example.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.DnsProvider
import com.example.ui.components.SimurghHeaderBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var showDnsDialog by remember { mutableStateOf(false) }
    var showCoreDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "تنظیمات Lonak VPN",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "شخصی‌سازی دی‌ان‌اس، امنیت، هسته و فیلترینگ داخلی",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Network & DNS
        SettingsSectionHeader(title = "شبکه و امنیت")

        SettingsTile(
            title = "ارائه‌دهنده DNS",
            subtitle = settings.dnsProvider.displayName,
            icon = Icons.Default.Dns,
            onClick = { showDnsDialog = true }
        )

        SettingsSwitchTile(
            title = "حذف تبلیغات و ردیاب‌ها",
            subtitle = "مسدودسازی تبلیغات در لایه TUN با لیست AdAway و StevenBlack",
            icon = Icons.Default.Block,
            checked = settings.adBlockEnabled,
            onCheckedChange = { viewModel.setAdBlockEnabled(it) }
        )

        SettingsSwitchTile(
            title = "کلید قطع اضطراری (Kill Switch)",
            subtitle = "مسدودسازی اینترنت هنگام قطع ناگهانی تونل VPN",
            icon = Icons.Default.Security,
            checked = settings.killSwitchEnabled,
            onCheckedChange = { viewModel.setKillSwitchEnabled(it) }
        )

        SettingsSwitchTile(
            title = "اتصال مجدد خودکار",
            subtitle = "تلاش مجدد با الگوریتم بک‌آف نمایی در صورت افت سوکت",
            icon = Icons.Default.Cached,
            checked = settings.autoReconnect,
            onCheckedChange = { viewModel.setAutoReconnect(it) }
        )

        SettingsSwitchTile(
            title = "تونل اختصاصی (Split Tunneling)",
            subtitle = "معاف‌سازی اپ‌های بانکی و پیام‌رسان‌های داخلی",
            icon = Icons.Default.CallSplit,
            checked = settings.splitTunnelingEnabled,
            onCheckedChange = { viewModel.setSplitTunnelingEnabled(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Core Engine
        SettingsSectionHeader(title = "هسته تونل و اتصال")

        SettingsTile(
            title = "هسته اجرایی پیش‌فرض",
            subtitle = settings.preferredCore,
            icon = Icons.Default.Memory,
            onClick = { showCoreDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: General & About
        SettingsSectionHeader(title = "عمومی و حریم خصوصی")

        SettingsTile(
            title = "درباره لونک VPN",
            subtitle = "«پرواز آزاد در اینترنت» • بدون لاگ و تبلیغات",
            icon = Icons.Default.Info,
            onClick = { showAboutDialog = true }
        )

        SettingsTile(
            title = "بازنشانی سرورها و داده‌ها",
            subtitle = "حذف و بازتولید گره‌های پیش‌فرض",
            icon = Icons.Default.PowerSettingsNew,
            onClick = {
                viewModel.resetAllData()
                Toast.makeText(context, "سرورها با موفقیت بازنشانی شدند", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }

    // DNS Selection Dialog
    if (showDnsDialog) {
        AlertDialog(
            onDismissRequest = { showDnsDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = "انتخاب ارائه‌دهنده DNS", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    for (provider in DnsProvider.entries) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateDnsProvider(provider)
                                    showDnsDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = settings.dnsProvider == provider,
                                onClick = {
                                    viewModel.updateDnsProvider(provider)
                                    showDnsDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PersianTurquoise)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = provider.displayName, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDnsDialog = false }) {
                    Text("بستن", color = PersianTurquoise)
                }
            }
        )
    }

    // Core Engine Dialog
    if (showCoreDialog) {
        val cores = listOf(
            "Xray-core (Reality + XTLS Vision)",
            "sing-box (Shadowsocks + Hysteria2)",
            "WireGuard-go (Native Kernel TUN)",
            "SSH-JNI (Encrypted Shell Tunnel)"
        )
        AlertDialog(
            onDismissRequest = { showCoreDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(text = "انتخاب هسته پروتکل", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    for (core in cores) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setPreferredCore(core)
                                    showCoreDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = settings.preferredCore.startsWith(core.take(8)),
                                onClick = {
                                    viewModel.setPreferredCore(core)
                                    showCoreDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PersianGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = core, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCoreDialog = false }) {
                    Text("بستن", color = PersianGold)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SimurghHeaderBadge()
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "لونک VPN", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "نسخه ۱.۰.۰ — سیمرغ آزادی", color = PersianGold, fontSize = 12.sp)
                    }
                }
            },
            text = {
                Column {
                    Text(
                        text = "«لونک VPN — پرواز آزاد در اینترنت»\n\n" +
                                "طراحی‌شده با معماری تمیز اندروید (Kotlin + Compose + Room + VpnService)، مجهز به اسکنر زنده گره‌های ضد فیلترینگ، تست پینگ خودکار، مسدودساز تبلیغات در لایه شبکه و بدون هیچ‌گونه ردیاب یا تبلیغات درون‌برنامه‌ای.",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PersianTurquoise)
                ) {
                    Text("تأیید", color = DarkBackground)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = PersianGold,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun SettingsTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PersianTurquoise.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PersianTurquoise,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SettingsSwitchTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PersianGold.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PersianGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(text = subtitle, color = Color.Gray, fontSize = 11.sp)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PersianTurquoise,
                    uncheckedTrackColor = DarkSurfaceVariant
                )
            )
        }
    }
}
