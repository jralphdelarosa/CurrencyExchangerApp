package com.example.currencyexchangerapp.data.remote.services

import android.util.Log
import com.example.currencyexchangerapp.data.remote.dto.response.ExchangeRateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
class ExchangeRateApiService @Inject constructor(
    private val client: HttpClient
) {
    private val BASE_URL = "https://developers.paysera.com/tasks/api/currency-exchange-rates"
    private val TAG = "ExchangeRateApiService"

    suspend fun getRates(): Map<String, Double> {
        return try {
            Log.d(TAG, "Fetching exchange rates from: $BASE_URL")
            val response: ExchangeRateResponse = client.get(BASE_URL).body()
            Log.d(TAG, "Successfully fetched ${response.rates.size} exchange rates")
            Log.d(TAG, "Available currencies: ${response.rates.keys}")
            response.rates
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch exchange rates: ${e.message}", e)
            throw e // Re-throw to let repository handle it
        }
    }
}