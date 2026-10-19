package com.example.core.model

enum class DnsProvider(val displayName: String, val primaryIp: String, val dohUrl: String) {
    CLOUDFLARE("Cloudflare (1.1.1.1)", "1.1.1.1", "https://cloudflare-dns.com/dns-query"),
    GOOGLE("Google DNS (8.8.8.8)", "8.8.8.8", "https://dns.google/dns-query"),
    QUAD9("Quad9 (9.9.9.9)", "9.9.9.9", "https://dns.quad9.net/dns-query"),
    ADGUARD("AdGuard Ad-Blocking", "94.140.14.14", "https://dns.adguard-dns.com/dns-query"),
    SHECAN_IRAN("Shecan Anti-Sanction", "178.22.122.100", "https://shecan.ir");
}

data class VpnSettings(
    val dnsProvider: DnsProvider = DnsProvider.CLOUDFLARE,
    val customDns: String = "",
    val adBlockEnabled: Boolean = true,
    val killSwitchEnabled: Boolean = false,
    val autoReconnect: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val excludedAppPackages: Set<String> = emptySet(),
    val preferredCore: String = "Xray-core (Reality)",
    val appLanguage: String = "fa",
    val isDarkMode: Boolean = true
)
