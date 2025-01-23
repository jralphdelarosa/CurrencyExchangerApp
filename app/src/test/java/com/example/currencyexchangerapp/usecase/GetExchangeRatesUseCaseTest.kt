package com.example.currencyexchangerapp.usecase

import app.cash.turbine.test
import com.example.currencyexchangerapp.domain.repository.CurrencyRepository
import com.example.currencyexchangerapp.domain.usecases.exchange.GetExchangeRatesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Created by John Ralph Dela Rosa on 11/20/2025.
 */
class GetExchangeRatesUseCaseTest {

    private lateinit var useCase: GetExchangeRatesUseCase
    private lateinit var repository: CurrencyRepository

    private val testRates = mapOf(
        "USD" to 1.0896,
        "GBP" to 0.8572,
        "JPY" to 159.52
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetExchangeRatesUseCase(repository)
    }

    @Test
    fun `invoke calls repository getExchangeRates`() = runTest {
        // Given
        every { repository.getExchangeRates() } returns flowOf(testRates)

        // When
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then
        verify(exactly = 1) { repository.getExchangeRates() }
    }

    @Test
    fun `invoke returns data from repository`() = runTest {
        // Given
        every { repository.getExchangeRates() } returns flowOf(testRates)

        // When & Then
        useCase().test {
            val emission = awaitItem()
            assertEquals(testRates, emission)
            awaitComplete()
        }
    }

    @Test
    fun `invoke propagates multiple emissions from repository`() = runTest {
        // Given
        val rates1 = mapOf("USD" to 1.08)
        val rates2 = mapOf("USD" to 1.09)
        val rates3 = mapOf("USD" to 1.10)

        every { repository.getExchangeRates() } returns flowOf(rates1, rates2, rates3)

        // When & Then
        useCase().test {
            assertEquals(rates1, awaitItem())
            assertEquals(rates2, awaitItem())
            assertEquals(rates3, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty map when repository returns empty`() = runTest {
        // Given
        every { repository.getExchangeRates() } returns flowOf(emptyMap())

        // When & Then
        useCase().test {
            val emission = awaitItem()
            assertEquals(emptyMap<String, Double>(), emission)
            awaitComplete()
        }
    }

    @Test
    fun `invoke propagates errors from repository`() = runTest {
        // Given
        val exception = Exception("Network error")
        every { repository.getExchangeRates() } returns kotlinx.coroutines.flow.flow {
            throw exception
        }

        // When & Then
        useCase().test {
            val error = awaitError()
            assertEquals("Network error", error.message)
        }
    }
}