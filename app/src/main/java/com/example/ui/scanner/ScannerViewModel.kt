package com.example.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.scanner.ScannerState
import com.example.core.scanner.ServerScraper
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val scraper: ServerScraper
) : ViewModel() {

    val scannerState: StateFlow<ScannerState> = scraper.scannerState
    val presetSources = scraper.presetSources

    fun startScan() {
        viewModelScope.launch {
            scraper.startScan()
        }
    }

    fun stopScan() {
        scraper.stopScan()
    }
}
