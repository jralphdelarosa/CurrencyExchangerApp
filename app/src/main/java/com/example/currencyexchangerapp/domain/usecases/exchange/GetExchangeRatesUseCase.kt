package com.example.currencyexchangerapp.domain.usecases.exchange

import com.example.currencyexchangerapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
class GetExchangeRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<Map<String, Double>> {
        return repository.getExchangeRates()
    }
}