package com.example.currencyexchangerapp.presentation.viewmodel

import com.example.currencyexchangerapp.presentation.model.BalanceUi

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
data class ExchangeUiState(
    val balances: List<BalanceUi>,
    val supportedCurrencies: List<String>,
    val sellCurrency: String,
    val buyCurrency: String,
    val sellAmountInput: String,
    val lastRatePreview: String?,
    val isLoading: Boolean,
    val error: String? = null,
)