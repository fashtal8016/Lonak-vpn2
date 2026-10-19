package com.example.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.model.DnsProvider
import com.example.core.model.VpnSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lonak_vpn_prefs")

class AppPreferences(private val context: Context) {

    private object Keys {
        val SELECTED_SERVER_ID = longPreferencesKey("selected_server_id")
        val DNS_PROVIDER = stringPreferencesKey("dns_provider")
        val CUSTOM_DNS = stringPreferencesKey("custom_dns")
        val AD_BLOCK = booleanPreferencesKey("ad_block")
        val KILL_SWITCH = booleanPreferencesKey("kill_switch")
        val AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val SPLIT_TUNNELING = booleanPreferencesKey("split_tunneling")
        val EXCLUDED_APPS = stringSetPreferencesKey("excluded_apps")
        val PREFERRED_CORE = stringPreferencesKey("preferred_core")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val selectedServerId: Flow<Long?> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.SELECTED_SERVER_ID]
        if (id == null || id <= 0L) null else id
    }

    val settings: Flow<VpnSettings> = context.dataStore.data.map { prefs ->
        val dnsName = prefs[Keys.DNS_PROVIDER] ?: DnsProvider.CLOUDFLARE.name
        val dnsProvider = try {
            DnsProvider.valueOf(dnsName)
        } catch (e: Exception) {
            DnsProvider.CLOUDFLARE
        }

        VpnSettings(
            dnsProvider = dnsProvider,
            customDns = prefs[Keys.CUSTOM_DNS] ?: "",
            adBlockEnabled = prefs[Keys.AD_BLOCK] ?: true,
            killSwitchEnabled = prefs[Keys.KILL_SWITCH] ?: false,
            autoReconnect = prefs[Keys.AUTO_RECONNECT] ?: true,
            splitTunnelingEnabled = prefs[Keys.SPLIT_TUNNELING] ?: false,
            excludedAppPackages = prefs[Keys.EXCLUDED_APPS] ?: emptySet(),
            preferredCore = prefs[Keys.PREFERRED_CORE] ?: "Xray-core (Reality)",
            appLanguage = prefs[Keys.APP_LANGUAGE] ?: "fa",
            isDarkMode = prefs[Keys.IS_DARK_MODE] ?: true
        )
    }

    suspend fun setSelectedServerId(id: Long?) {
        context.dataStore.edit { prefs ->
            if (id == null) {
                prefs.remove(Keys.SELECTED_SERVER_ID)
            } else {
                prefs[Keys.SELECTED_SERVER_ID] = id
            }
        }
    }

    suspend fun updateDnsProvider(provider: DnsProvider) {
        context.dataStore.edit { it[Keys.DNS_PROVIDER] = provider.name }
    }

    suspend fun setAdBlockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AD_BLOCK] = enabled }
    }

    suspend fun setKillSwitchEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KILL_SWITCH] = enabled }
    }

    suspend fun setAutoReconnect(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_RECONNECT] = enabled }
    }

    suspend fun setSplitTunnelingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SPLIT_TUNNELING] = enabled }
    }

    suspend fun setExcludedApps(packages: Set<String>) {
        context.dataStore.edit { it[Keys.EXCLUDED_APPS] = packages }
    }

    suspend fun setPreferredCore(core: String) {
        context.dataStore.edit { it[Keys.PREFERRED_CORE] = core }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[Keys.APP_LANGUAGE] = lang }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { it[Keys.IS_DARK_MODE] = isDark }
    }
}
