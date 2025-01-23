package com.example.currencyexchangerapp.viemodel

import com.example.currencyexchangerapp.domain.usecases.exchange.GetExchangeRatesUseCase
import com.example.currencyexchangerapp.presentation.viewmodel.ExchangeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by John Ralph Dela Rosa on 11/20/2025.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeViewModelTest {

    private lateinit var viewModel: ExchangeViewModel
    private lateinit var getExchangeRatesUseCase: GetExchangeRatesUseCase
    private val testDispatcher = StandardTestDispatcher()

    // Sample test data
    private val testRates = mapOf(
        "USD" to 1.0896,
        "GBP" to 0.8572,
        "JPY" to 159.52
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getExchangeRatesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)

        // When
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1000.0, state.balances.first().amount, 0.01)
        assertEquals("EUR", state.balances.first().currency)
        assertEquals("EUR", state.sellCurrency)
        assertEquals("USD", state.buyCurrency)
        assertEquals("", state.sellAmountInput)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `exchange rates are loaded successfully`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)

        // When
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.supportedCurrencies.contains("EUR"))
        assertTrue(state.supportedCurrencies.contains("USD"))
        assertTrue(state.supportedCurrencies.contains("GBP"))
        assertTrue(state.supportedCurrencies.contains("JPY"))
    }

    @Test
    fun `onSellCurrencyChanged updates state correctly`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSellCurrencyChanged("GBP")

        // Then
        assertEquals("GBP", viewModel.uiState.value.sellCurrency)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onBuyCurrencyChanged updates state correctly`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onBuyCurrencyChanged("JPY")

        // Then
        assertEquals("JPY", viewModel.uiState.value.buyCurrency)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onSellAmountChanged updates state and preview`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSellAmountChanged("100")

        // Then
        assertEquals("100", viewModel.uiState.value.sellAmountInput)
        assertNotNull(viewModel.uiState.value.lastRatePreview)
        assertTrue(viewModel.uiState.value.lastRatePreview!!.contains("100"))
    }

    @Test
    fun `onSwapCurrencies swaps sell and buy currencies`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        val initialSell = viewModel.uiState.value.sellCurrency
        val initialBuy = viewModel.uiState.value.buyCurrency

        // When
        viewModel.onSwapCurrencies()

        // Then
        assertEquals(initialBuy, viewModel.uiState.value.sellCurrency)
        assertEquals(initialSell, viewModel.uiState.value.buyCurrency)
    }

    @Test
    fun `successful conversion updates balances correctly`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        viewModel.onSellAmountChanged("100")

        // When
        viewModel.onConvertClicked()

        // Then
        val state = viewModel.uiState.value
        val eurBalance = state.balances.find { it.currency == "EUR" }
        val usdBalance = state.balances.find { it.currency == "USD" }

        assertEquals(900.0, eurBalance?.amount ?: 0.0, 0.01)
        assertEquals(108.96, usdBalance?.amount ?: 0.0, 0.01)
        assertEquals("", state.sellAmountInput) // Input should be cleared
        assertNull(state.error)
    }

    @Test
    fun `conversion with invalid amount shows error`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        viewModel.onSellAmountChanged("")

        // When
        viewModel.onConvertClicked()

        // Then
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Please enter a valid amount", viewModel.uiState.value.error)
    }

    @Test
    fun `conversion with zero amount shows error`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        viewModel.onSellAmountChanged("0")

        // When
        viewModel.onConvertClicked()

        // Then
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Please enter a valid amount", viewModel.uiState.value.error)
    }

    @Test
    fun `conversion to same currency shows error`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        viewModel.onSellCurrencyChanged("EUR")
        viewModel.onBuyCurrencyChanged("EUR")
        viewModel.onSellAmountChanged("100")

        // When
        viewModel.onConvertClicked()

        // Then
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Cannot convert to the same currency", viewModel.uiState.value.error)
    }

    @Test
    fun `conversion with insufficient balance shows error`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        viewModel.onSellAmountChanged("1500") // More than 1000 EUR initial balance

        // When
        viewModel.onConvertClicked()

        // Then
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Insufficient balance", viewModel.uiState.value.error)
    }

    @Test
    fun `rate preview shows correctly for EUR to other currency`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSellAmountChanged("100")

        // Then
        val preview = viewModel.uiState.value.lastRatePreview
        assertNotNull(preview)
        assertTrue(preview!!.contains("100"))
        assertTrue(preview.contains("EUR"))
        assertTrue(preview.contains("USD"))
    }

    @Test
    fun `rate preview shows default rate when no amount entered`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSellAmountChanged("")

        // Then
        val preview = viewModel.uiState.value.lastRatePreview
        assertNotNull(preview)
        assertTrue(preview!!.startsWith("1 EUR"))
    }

    @Test
    fun `multiple conversions update balances correctly`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // First conversion: EUR to USD
        viewModel.onSellAmountChanged("100")
        viewModel.onConvertClicked()

        // Second conversion: EUR to GBP
        viewModel.onBuyCurrencyChanged("GBP")
        viewModel.onSellAmountChanged("100")
        viewModel.onConvertClicked()

        // Then
        val state = viewModel.uiState.value
        assertEquals(800.0, state.balances.find { it.currency == "EUR" }?.amount ?: 0.0, 0.01)
        assertEquals(108.96, state.balances.find { it.currency == "USD" }?.amount ?: 0.0, 0.01)
        assertEquals(85.72, state.balances.find { it.currency == "GBP" }?.amount ?: 0.0, 0.01)
    }

    @Test
    fun `conversion between non-EUR currencies calculates correctly`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // First get some USD balance
        viewModel.onSellAmountChanged("100")
        viewModel.onConvertClicked()

        // Then convert USD to GBP
        viewModel.onSellCurrencyChanged("USD")
        viewModel.onBuyCurrencyChanged("GBP")
        viewModel.onSellAmountChanged("50")
        viewModel.onConvertClicked()

        // Then
        val state = viewModel.uiState.value
        val gbpBalance = state.balances.find { it.currency == "GBP" }?.amount ?: 0.0
        assertTrue(gbpBalance > 0)
    }

    @Test
    fun `error state is cleared when user starts new input`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // Create an error
        viewModel.onSellAmountChanged("0")
        viewModel.onConvertClicked()
        assertNotNull(viewModel.uiState.value.error)

        // When - user changes input
        viewModel.onSellAmountChanged("100")

        // Then
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `balances cannot go negative`() = runTest {
        // Given
        coEvery { getExchangeRatesUseCase() } returns flowOf(testRates)
        viewModel = ExchangeViewModel(getExchangeRatesUseCase)
        advanceUntilIdle()

        // When - try to convert more than available
        viewModel.onSellAmountChanged("2000")
        viewModel.onConvertClicked()

        // Then
        val eurBalance = viewModel.uiState.value.balances.find { it.currency == "EUR" }?.amount
        assertEquals(1000.0, eurBalance ?: 0.0, 0.01) // Balance should remain unchanged
        assertNotNull(viewModel.uiState.value.error)
    }
}