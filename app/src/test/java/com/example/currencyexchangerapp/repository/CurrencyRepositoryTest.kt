package com.example.currencyexchangerapp.repository

import app.cash.turbine.test
import com.example.currencyexchangerapp.data.remote.services.ExchangeRateApiService
import com.example.currencyexchangerapp.data.repository.CurrencyRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlin.time.Duration.Companion.seconds

/**
 * Created by John Ralph Dela Rosa on 11/20/2025.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyRepositoryTest {

    private lateinit var repository: CurrencyRepositoryImpl
    private lateinit var apiService: ExchangeRateApiService

    private val testRates = mapOf(
        "USD" to 1.0896,
        "GBP" to 0.8572,
        "JPY" to 159.52,
        "AUD" to 1.6234
    )

    @Before
    fun setup() {
        apiService = mockk()
        repository = CurrencyRepositoryImpl(apiService)
    }

    @Test
    fun `getExchangeRates emits data successfully`() = runTest {
        // Given
        coEvery { apiService.getRates() } returns testRates

        // When & Then
        repository.getExchangeRates().test {
            val emission = awaitItem()
            assertEquals(testRates, emission)

            // Verify it continues emitting
            advanceTimeBy(5.seconds)
            val secondEmission = awaitItem()
            assertEquals(testRates, secondEmission)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates refreshes every 5 seconds`() = runTest {
        // Given
        coEvery { apiService.getRates() } returns testRates

        // When & Then
        repository.getExchangeRates().test {
            // First emission
            awaitItem()
            coVerify(exactly = 1) { apiService.getRates() }

            // Advance time by 5 seconds
            advanceTimeBy(5.seconds)
            awaitItem()
            coVerify(exactly = 2) { apiService.getRates() }

            // Advance another 5 seconds
            advanceTimeBy(5.seconds)
            awaitItem()
            coVerify(exactly = 3) { apiService.getRates() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates retries on failure`() = runTest {
        // Given - first call fails, second succeeds
        coEvery { apiService.getRates() } throws Exception("Network error") andThen testRates

        // When & Then
        repository.getExchangeRates().test {
            // First attempt fails, waits 5 seconds
            advanceTimeBy(5.seconds)

            // Second attempt succeeds
            val emission = awaitItem()
            assertEquals(testRates, emission)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates continues after error`() = runTest {
        // Given - alternating success and failure
        coEvery { apiService.getRates() } returns testRates andThenThrows
                Exception("Temporary error") andThen testRates

        // When & Then
        repository.getExchangeRates().test {
            // First emission succeeds
            val first = awaitItem()
            assertEquals(testRates, first)

            // Second call fails (5 seconds later)
            advanceTimeBy(5.seconds)
            // No emission on error

            // Third call succeeds (another 5 seconds)
            advanceTimeBy(5.seconds)
            val third = awaitItem()
            assertEquals(testRates, third)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates returns all currencies from API`() = runTest {
        // Given
        val largeCurrencySet = mapOf(
            "USD" to 1.0896,
            "GBP" to 0.8572,
            "JPY" to 159.52,
            "AUD" to 1.6234,
            "CAD" to 1.4521,
            "CHF" to 0.9345,
            "CNY" to 7.8234
        )
        coEvery { apiService.getRates() } returns largeCurrencySet

        // When & Then
        repository.getExchangeRates().test {
            val emission = awaitItem()
            assertEquals(7, emission.size)
            assertTrue(emission.containsKey("USD"))
            assertTrue(emission.containsKey("GBP"))
            assertTrue(emission.containsKey("JPY"))
            assertTrue(emission.containsKey("AUD"))
            assertTrue(emission.containsKey("CAD"))
            assertTrue(emission.containsKey("CHF"))
            assertTrue(emission.containsKey("CNY"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates handles empty response`() = runTest {
        // Given
        coEvery { apiService.getRates() } returns emptyMap()

        // When & Then
        repository.getExchangeRates().test {
            val emission = awaitItem()
            assertTrue(emission.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates preserves rate precision`() = runTest {
        // Given - rates with high precision
        val preciseRates = mapOf(
            "USD" to 1.089654321,
            "JPY" to 159.52398765
        )
        coEvery { apiService.getRates() } returns preciseRates

        // When & Then
        repository.getExchangeRates().test {
            val emission = awaitItem()
            assertEquals(1.089654321, emission["USD"] ?: 0.0, 0.0000001)
            assertEquals(159.52398765, emission["JPY"] ?: 0.0, 0.0000001)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple collectors receive same data`() = runTest {
        // Given
        coEvery { apiService.getRates() } returns testRates

        // When & Then - collect from two different collectors
        val flow = repository.getExchangeRates()

        flow.test {
            val emission1 = awaitItem()
            assertEquals(testRates, emission1)
            cancelAndIgnoreRemainingEvents()
        }

        flow.test {
            val emission2 = awaitItem()
            assertEquals(testRates, emission2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}