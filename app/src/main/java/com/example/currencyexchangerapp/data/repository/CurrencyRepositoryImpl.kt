package com.example.currencyexchangerapp.data.repository

import android.util.Log
import com.example.currencyexchangerapp.data.remote.services.ExchangeRateApiService
import com.example.currencyexchangerapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
class CurrencyRepositoryImpl @Inject constructor(
    private val api: ExchangeRateApiService
) : CurrencyRepository {

    companion object{
        private const val TAG = "CurrencyRepository"
    }

    override fun getExchangeRates(): Flow<Map<String, Double>> = flow {
        while (true) {
            try {
                Log.d(TAG, "Fetching exchange rates...")
                val result = api.getRates()
                Log.d(TAG, "Rates fetched successfully: ${result.size} currencies")
                Log.d(TAG, "Rates: $result")
                emit(result)
                Log.d(TAG, "Waiting 5 seconds before next fetch...")
                delay(5000)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching rates: ${e.message}", e)
                delay(5000) // Wait before retrying
            }
        }
    }
}

