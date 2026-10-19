package com.example.ui.servers

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.ProtocolType
import com.example.ui.components.ServerItemCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianLapisPrimary
import com.example.ui.theme.PersianTurquoise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersScreen(
    viewModel: ServersViewModel,
    onServerSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val servers by viewModel.filteredServers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedProtocol by viewModel.selectedProtocol.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val selectedServerId by viewModel.selectedServerId.collectAsStateWithLifecycle()
    val testingServerId by viewModel.testingServerId.collectAsStateWithLifecycle()
    val isTestingAll by viewModel.isTestingAll.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var inputConfigText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header: Title + Test All Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "سرورهای Lonak",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${servers.size} سرور آماده اتصال",
                        style = MaterialTheme.typography.labelSmall,
                        color = PersianTurquoise
                    )
                }

                Button(
                    onClick = { viewModel.testAllPings() },
                    enabled = !isTestingAll,
                    colors = ButtonDefaults.buttonColors(containerColor = PersianLapisPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("test_all_pings_button")
                ) {
                    if (isTestingAll) {
                        CircularProgressIndicator(
                            color = PersianGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.NetworkPing,
                            contentDescription = "Test All",
                            tint = PersianGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (isTestingAll) "در حال تست…" else "تست پینگ",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "جستجوی سرور، کشور، یا پروتکل…",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PersianTurquoise
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = PersianTurquoise,
                    unfocusedBorderColor = DarkSurfaceVariant,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_servers_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Protocol Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedProtocol == null,
                    onClick = { viewModel.selectProtocol(null) },
                    label = { Text("همه پروتکل‌ها", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PersianTurquoise,
                        selectedLabelColor = DarkBackground,
                        containerColor = DarkSurface,
                        labelColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                for (proto in ProtocolType.entries) {
                    FilterChip(
                        selected = selectedProtocol == proto,
                        onClick = { viewModel.selectProtocol(proto) },
                        label = { Text(proto.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PersianTurquoise,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurface,
                            labelColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sort Options Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (sort in ServerSortOption.entries) {
                    FilterChip(
                        selected = sortOption == sort,
                        onClick = { viewModel.setSortOption(sort) },
                        label = { Text(sort.titlePersian, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PersianGold,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurface,
                            labelColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Server Card List
            if (servers.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سروری یافت نشد",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "از دکمه + برای افزودن کانفیگ یا از برگه اسکنر استفاده کنید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(servers, key = { it.id }) { server ->
                        ServerItemCard(
                            server = server,
                            isSelected = server.id == selectedServerId,
                            isTesting = server.id == testingServerId,
                            onConnectClick = {
                                viewModel.selectServer(server)
                                onServerSelected()
                            },
                            onTestPingClick = {
                                viewModel.testServerPing(server)
                            },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(server)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Button to Add / Import Server
        FloatingActionButton(
            onClick = {
                val clipText = clipboardManager.getText()?.text.orEmpty()
                if (clipText.contains("://")) {
                    inputConfigText = clipText
                }
                showAddDialog = true
            },
            containerColor = PersianTurquoise,
            contentColor = DarkBackground,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_server_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Config")
        }

        // Add Server Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "افزودن کانفیگ یا لینک اشتراک",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "لینک VLESS, VMess, Trojan, SS, Hysteria2, TUIC یا لینک سابسکریپشن را وارد کنید:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputConfigText,
                            onValueChange = { inputConfigText = it },
                            placeholder = { Text("vless://... یا https://...", color = Color.Gray, fontSize = 12.sp) },
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PersianTurquoise
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    val text = clipboardManager.getText()?.text.orEmpty()
                                    if (text.isNotEmpty()) {
                                        inputConfigText = text
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = PersianGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("جای‌گذاری کلیپ‌بورد", color = PersianGold, fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputConfigText.isNotBlank()) {
                                viewModel.importConfig(inputConfigText) { count ->
                                    Toast.makeText(
                                        context,
                                        if (count > 0) "$count کانفیگ با موفقیت اضافه شد" else "کانفیگ نامعتبر بود",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showAddDialog = false
                                    inputConfigText = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PersianTurquoise)
                    ) {
                        Text("ذخیره", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("انصراف", color = Color.Gray)
                    }
                }
            )
        }
    }
}
