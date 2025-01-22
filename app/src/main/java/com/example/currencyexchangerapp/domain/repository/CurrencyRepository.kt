package com.example.currencyexchangerapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
interface CurrencyRepository {
    fun getExchangeRates(): Flow<Map<String, Double>>
}