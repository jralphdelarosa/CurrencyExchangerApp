package com.example.currencyexchangerapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.currencyexchangerapp.presentation.screen.CurrencyExchangerScreen
import com.example.currencyexchangerapp.presentation.viewmodel.ExchangeUiState
import com.example.currencyexchangerapp.presentation.viewmodel.ExchangeViewModel
import com.example.currencyexchangerapp.presentation.model.BalanceUi
import com.example.currencyexchangerapp.ui.theme.CurrencyExchangerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ExchangeViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyExchangerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val uiState by viewModel.uiState.collectAsState()

                    CurrencyExchangerScreen(
                        uiState = uiState,
                        onSellCurrencyChanged = viewModel::onSellCurrencyChanged,
                        onBuyCurrencyChanged = viewModel::onBuyCurrencyChanged,
                        onSellAmountChanged = viewModel::onSellAmountChanged,
                        onSwapCurrencies = viewModel::onSwapCurrencies,
                        onConvertClicked = viewModel::onConvertClicked,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


}

private val previewBalances = listOf(
    BalanceUi("EUR", 1000.0),
    BalanceUi("USD", 200.5),
    BalanceUi("GBP", 50.0),
    BalanceUi("JPY", 10000.0)
)

private val previewCurrencies = listOf("EUR", "USD", "GBP", "JPY", "AUD", "CAD")

@PreviewLightDark
@Composable
fun PreviewCurrencyExchangerScreen() {
    val uiState = ExchangeUiState(
        balances = previewBalances,
        supportedCurrencies = previewCurrencies,
        sellCurrency = "EUR",
        buyCurrency = "USD",
        sellAmountInput = "100",
        lastRatePreview = "1 EUR = 1.08 USD",
        isLoading = false
    )

    CurrencyExchangerTheme {
        CurrencyExchangerScreen(
            uiState = uiState,
            onSellCurrencyChanged = {},
            onBuyCurrencyChanged = {},
            onSellAmountChanged = {},
            onSwapCurrencies = {},
            onConvertClicked = {}
        )
    }
}