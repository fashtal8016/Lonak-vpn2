package com.example.core.parser

import android.util.Base64
import com.example.core.data.local.entity.ServerEntity
import com.example.core.model.ProtocolType
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object UriParser {

    fun parseLine(rawLine: String, subscriptionUrl: String? = null): ServerEntity? {
        val trimmed = rawLine.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) return null

        return try {
            when {
                trimmed.startsWith("vless://", ignoreCase = true) -> parseVless(trimmed, subscriptionUrl)
                trimmed.startsWith("vmess://", ignoreCase = true) -> parseVmess(trimmed, subscriptionUrl)
                trimmed.startsWith("trojan://", ignoreCase = true) -> parseTrojan(trimmed, subscriptionUrl)
                trimmed.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(trimmed, subscriptionUrl)
                trimmed.startsWith("hysteria2://", ignoreCase = true) || trimmed.startsWith("hy2://", ignoreCase = true) -> parseHysteria2(trimmed, subscriptionUrl)
                trimmed.startsWith("tuic://", ignoreCase = true) -> parseTuic(trimmed, subscriptionUrl)
                trimmed.startsWith("wireguard://", ignoreCase = true) || trimmed.startsWith("wg://", ignoreCase = true) -> parseWireguard(trimmed, subscriptionUrl)
                trimmed.startsWith("socks5://", ignoreCase = true) -> parseSocks(trimmed, subscriptionUrl)
                trimmed.startsWith("http://", ignoreCase = true) -> parseHttpProxy(trimmed, subscriptionUrl)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseVless(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val uri = URI(uriStr)
        val host = uri.host ?: return null
        val port = if (uri.port > 0) uri.port else 443
        val rawName = uri.fragment ?: "$host:$port"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "VLESS - $host" },
            protocol = ProtocolType.VLESS,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "NEW"
        )
    }

    private fun parseVmess(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val base64Data = uriStr.substring("vmess://".length).trim()
        val jsonStr = try {
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT or Base64.URL_SAFE)
            String(decodedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            return null
        }

        val json = JSONObject(jsonStr)
        val host = json.optString("add").ifEmpty { json.optString("host") }
        if (host.isEmpty()) return null
        val port = json.optInt("port", 443)
        val rawName = json.optString("ps", "$host:$port")
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "VMess - $host" },
            protocol = ProtocolType.VMESS,
            host = host,
            port = port,
            rawUri = uriStr,
            configJson = jsonStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "NEW"
        )
    }

    private fun parseTrojan(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val uri = URI(uriStr)
        val host = uri.host ?: return null
        val port = if (uri.port > 0) uri.port else 443
        val rawName = uri.fragment ?: "$host:$port"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "Trojan - $host" },
            protocol = ProtocolType.TROJAN,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "NEW"
        )
    }

    private fun parseShadowsocks(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val content = uriStr.removePrefix("ss://")
        val fragmentSplit = content.split("#", limit = 2)
        val mainPart = fragmentSplit[0]
        val rawName = if (fragmentSplit.size > 1) fragmentSplit[1] else ""
        val decodedName = decodeName(rawName)

        val host: String
        val port: Int

        if (mainPart.contains("@")) {
            val atSplit = mainPart.split("@", limit = 2)
            val hostPort = atSplit[1].split(":", limit = 2)
            host = hostPort[0]
            port = hostPort.getOrNull(1)?.toIntOrNull() ?: 8388
        } else {
            // Entire body might be base64
            val decoded = try {
                String(Base64.decode(mainPart, Base64.DEFAULT or Base64.URL_SAFE), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                return null
            }
            if (!decoded.contains("@")) return null
            val atSplit = decoded.split("@", limit = 2)
            val hostPort = atSplit[1].split(":", limit = 2)
            host = hostPort[0]
            port = hostPort.getOrNull(1)?.toIntOrNull() ?: 8388
        }

        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "SS - $host" },
            protocol = ProtocolType.SHADOWSOCKS,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "NEW"
        )
    }

    private fun parseHysteria2(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val cleanUri = if (uriStr.startsWith("hy2://")) uriStr.replace("hy2://", "hysteria2://") else uriStr
        val uri = URI(cleanUri)
        val host = uri.host ?: return null
        val port = if (uri.port > 0) uri.port else 443
        val rawName = uri.fragment ?: "$host:$port"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "Hysteria2 - $host" },
            protocol = ProtocolType.HYSTERIA2,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "FAST"
        )
    }

    private fun parseTuic(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val uri = URI(uriStr)
        val host = uri.host ?: return null
        val port = if (uri.port > 0) uri.port else 443
        val rawName = uri.fragment ?: "$host:$port"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "TUIC - $host" },
            protocol = ProtocolType.TUIC,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "NEW"
        )
    }

    private fun parseWireguard(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val uri = URI(uriStr)
        val host = uri.host ?: "127.0.0.1"
        val port = if (uri.port > 0) uri.port else 51820
        val rawName = uri.fragment ?: "WireGuard"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "WireGuard" },
            protocol = ProtocolType.WIREGUARD,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "STABLE"
        )
    }

    private fun parseSocks(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val uri = URI(uriStr)
        val host = uri.host ?: return null
        val port = if (uri.port > 0) uri.port else 1080
        val rawName = uri.fragment ?: "$host:$port"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "SOCKS5 - $host" },
            protocol = ProtocolType.SOCKS5,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "TESTED"
        )
    }

    private fun parseHttpProxy(uriStr: String, subscriptionUrl: String?): ServerEntity? {
        val uri = URI(uriStr)
        val host = uri.host ?: return null
        val port = if (uri.port > 0) uri.port else 8080
        val rawName = uri.fragment ?: "$host:$port"
        val decodedName = decodeName(rawName)
        val (cc, cn) = extractCountry(decodedName, host)

        return ServerEntity(
            name = decodedName.ifBlank { "HTTP - $host" },
            protocol = ProtocolType.HTTP,
            host = host,
            port = port,
            rawUri = uriStr,
            countryCode = cc,
            countryName = cn,
            subscriptionUrl = subscriptionUrl,
            tag = "TESTED"
        )
    }

    fun parseSubscriptionContent(content: String, subscriptionUrl: String? = null): List<ServerEntity> {
        val trimmed = content.trim()
        val textToProcess: String = try {
            // Attempt Base64 decode
            val decoded = String(Base64.decode(trimmed, Base64.DEFAULT or Base64.URL_SAFE), StandardCharsets.UTF_8)
            if (decoded.contains("://")) decoded else trimmed
        } catch (e: Exception) {
            trimmed
        }

        val lines = textToProcess.lines()
        val result = mutableListOf<ServerEntity>()
        val seenSignatures = mutableSetOf<String>()

        for (line in lines) {
            val parsed = parseLine(line, subscriptionUrl) ?: continue
            val signature = "${parsed.protocol.name}_${parsed.host.lowercase()}_${parsed.port}"
            if (seenSignatures.add(signature)) {
                result.add(parsed)
            }
        }
        return result
    }

    private fun decodeName(name: String): String {
        return try {
            URLDecoder.decode(name, "UTF-8")
        } catch (e: Exception) {
            name
        }
    }

    fun extractCountry(name: String, host: String): Pair<String, String> {
        val lower = "$name $host".lowercase()
        return when {
            lower.contains("germany") || lower.contains("🇩🇪") || lower.contains(" de ") || lower.contains("-de-") || lower.endsWith(".de") -> "DE" to "Germany"
            lower.contains("finland") || lower.contains("🇫🇮") || lower.contains(" fi ") || lower.contains("-fi-") || lower.endsWith(".fi") -> "FI" to "Finland"
            lower.contains("netherlands") || lower.contains("🇳🇱") || lower.contains(" nl ") || lower.contains("amsterdam") || lower.endsWith(".nl") -> "NL" to "Netherlands"
            lower.contains("turkey") || lower.contains("🇹🇷") || lower.contains(" tr ") || lower.contains("istanbul") || lower.endsWith(".tr") -> "TR" to "Turkey"
            lower.contains("france") || lower.contains("🇫🇷") || lower.contains(" fr ") || lower.contains("paris") || lower.endsWith(".fr") -> "FR" to "France"
            lower.contains("united kingdom") || lower.contains("🇬🇧") || lower.contains(" uk ") || lower.contains("london") || lower.endsWith(".uk") -> "GB" to "United Kingdom"
            lower.contains("united states") || lower.contains("🇺🇸") || lower.contains(" us ") || lower.contains("usa") -> "US" to "United States"
            lower.contains("canada") || lower.contains("🇨🇦") || lower.contains(" ca ") -> "CA" to "Canada"
            lower.contains("sweden") || lower.contains("🇸🇪") || lower.contains(" se ") -> "SE" to "Sweden"
            lower.contains("switzerland") || lower.contains("🇨🇭") || lower.contains(" ch ") -> "CH" to "Switzerland"
            lower.contains("japan") || lower.contains("🇯🇵") || lower.contains(" jp ") || lower.contains("tokyo") -> "JP" to "Japan"
            lower.contains("uae") || lower.contains("dubai") || lower.contains("🇦🇪") -> "AE" to "UAE"
            lower.contains("iran") || lower.contains("🇮🇷") || lower.contains(" ir ") || lower.contains("tehran") || lower.endsWith(".ir") -> "IR" to "Iran"
            else -> "DE" to "Germany"
        }
    }
}
