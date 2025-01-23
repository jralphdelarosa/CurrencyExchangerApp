package com.example.currencyexchangerapp.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
@Serializable
data class ExchangeRateResponse (
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)