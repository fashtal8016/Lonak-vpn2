package com.example.core.model

enum class ProtocolType(val displayName: String, val scheme: String, val badgeColorHex: Long) {
    VLESS("VLESS", "vless", 0xFF19C8C8),
    VMESS("VMess", "vmess", 0xFF9B59B6),
    TROJAN("Trojan", "trojan", 0xFFE67E22),
    SHADOWSOCKS("Shadowsocks", "ss", 0xFF3498DB),
    HYSTERIA2("Hysteria 2", "hysteria2", 0xFFE74C3C),
    TUIC("TUIC", "tuic", 0xFF1ABC9C),
    WIREGUARD("WireGuard", "wireguard", 0xFF8E44AD),
    SOCKS5("SOCKS5", "socks5", 0xFFF39C12),
    HTTP("HTTP", "http", 0xFF95A5A6);

    companion object {
        fun fromScheme(scheme: String): ProtocolType {
            return when (scheme.lowercase().removeSuffix("://")) {
                "vless" -> VLESS
                "vmess" -> VMESS
                "trojan" -> TROJAN
                "ss" -> SHADOWSOCKS
                "hysteria2", "hy2" -> HYSTERIA2
                "tuic" -> TUIC
                "wireguard", "wg" -> WIREGUARD
                "socks5", "socks" -> SOCKS5
                "http", "https" -> HTTP
                else -> VLESS
            }
        }
    }
}
