package com.example.currencyexchangerapp.presentation.viewmodel

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.currencyexchangerapp.domain.usecases.exchange.GetExchangeRatesUseCase
import com.example.currencyexchangerapp.presentation.model.BalanceUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ExchangeUiState(
            balances = listOf(BalanceUi("EUR", 1000.0)),
            supportedCurrencies = listOf("EUR"),
            sellCurrency = "EUR",
            buyCurrency = "USD",
            sellAmountInput = "",
            lastRatePreview = null,
            isLoading = false
        )
    )
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    private val rates = mutableMapOf<String, Double>()

    init {
        viewModelScope.launch {
            getExchangeRatesUseCase()
                .catch { e ->
                    Log.e("ExchangeViewModel", "Error collecting rates: ${e.message}", e)
                    _uiState.update { it.copy(error = "Failed to load exchange rates") }
                }
                .collect { newRates ->
                    Log.d("ExchangeViewModel", "Received new rates: ${newRates.size} currencies")
                    rates.clear()
                    rates.putAll(newRates)

                    // Update supported currencies from API
                    val currencies = listOf("EUR") + newRates.keys.sorted()
                    _uiState.update {
                        it.copy(
                            supportedCurrencies = currencies,
                            isLoading = false
                        )
                    }

                    updateRatePreview()
                }
        }
    }

    fun onSellCurrencyChanged(currency: String) {
        _uiState.update { it.copy(sellCurrency = currency, error = null) }
        updateRatePreview()
    }

    fun onBuyCurrencyChanged(currency: String) {
        _uiState.update { it.copy(buyCurrency = currency, error = null) }
        updateRatePreview()
    }

    fun onSellAmountChanged(amount: String) {
        _uiState.update { it.copy(sellAmountInput = amount, error = null) }
        updateRatePreview()
    }

    fun onSwapCurrencies() {
        _uiState.update {
            it.copy(
                sellCurrency = it.buyCurrency,
                buyCurrency = it.sellCurrency
            )
        }
        updateRatePreview()
    }

    fun onConvertClicked() {
        val currentState = _uiState.value
        val amount = currentState.sellAmountInput.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        val from = currentState.sellCurrency
        val to = currentState.buyCurrency

        if (from == to) {
            _uiState.update { it.copy(error = "Cannot convert to the same currency") }
            return
        }

        val rate = if (from == "EUR") {
            rates[to]
        } else if (to == "EUR") {
            rates[from]?.let { 1.0 / it }
        } else {
            // Convert from -> EUR -> to
            val fromRate = rates[from]
            val toRate = rates[to]
            if (fromRate != null && toRate != null) {
                toRate / fromRate
            } else null
        }

        if (rate == null) {
            _uiState.update { it.copy(error = "Exchange rate not available") }
            return
        }

        val currentBalance = currentState.balances.find { it.currency == from }?.amount ?: 0.0

        if (amount > currentBalance) {
            _uiState.update { it.copy(error = "Insufficient balance") }
            return
        }

        // Check if 'to' currency exists in balances
        val toBalance = currentState.balances.find { it.currency == to }

        val updatedBalances = if (toBalance != null) {
            // Currency exists, update both balances
            currentState.balances.map { balance ->
                when (balance.currency) {
                    from -> balance.copy(amount = balance.amount - amount)
                    to -> balance.copy(amount = balance.amount + (amount * rate))
                    else -> balance
                }
            }
        } else {
            // Currency doesn't exist, add it to the list
            currentState.balances.map { balance ->
                if (balance.currency == from) {
                    balance.copy(amount = balance.amount - amount)
                } else {
                    balance
                }
            } + BalanceUi(to, amount * rate)
        }

        _uiState.update {
            it.copy(
                balances = updatedBalances,
                sellAmountInput = "",
                error = null
            )
        }

        Log.d("ExchangeViewModel", "Conversion successful: $amount $from -> ${amount * rate} $to")
    }

    private fun updateRatePreview() {
        val from = _uiState.value.sellCurrency
        val to = _uiState.value.buyCurrency
        val amount = _uiState.value.sellAmountInput.toDoubleOrNull()

        val rate = if (from == "EUR") {
            rates[to]
        } else if (to == "EUR") {
            rates[from]?.let { 1.0 / it }
        } else {
            val fromRate = rates[from]
            val toRate = rates[to]
            if (fromRate != null && toRate != null) {
                toRate / fromRate
            } else null
        }

        val preview = if (rate != null && amount != null && amount > 0) {
            val converted = amount * rate
            "%.2f %s = %.2f %s".format(amount, from, converted, to)
        } else if (rate != null) {
            "1 %s = %.4f %s".format(from, rate, to)
        } else {
            null
        }

        _uiState.update { it.copy(lastRatePreview = preview) }
    }
}