package com.example.currencyexchangerapp.presentation.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyexchangerapp.presentation.viewmodel.ExchangeUiState
import com.example.currencyexchangerapp.presentation.model.BalanceUi

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */

// --- Top-level screen composable ---
@Composable
fun CurrencyExchangerScreen(
    uiState: ExchangeUiState,
    onSellCurrencyChanged: (String) -> Unit,
    onBuyCurrencyChanged: (String) -> Unit,
    onSellAmountChanged: (String) -> Unit,
    onSwapCurrencies: () -> Unit,
    onConvertClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Currency Exchanger",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        BalancesCard(balances = uiState.balances)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CurrencyDropdown(
                        label = "From",
                        currencies = uiState.supportedCurrencies,
                        selected = uiState.sellCurrency,
                        onSelected = onSellCurrencyChanged,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onSwapCurrencies,
                        modifier = Modifier
                            .padding(top = 20.dp, start = 5.dp, end = 5.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    CurrencyDropdown(
                        label = "To",
                        currencies = uiState.supportedCurrencies,
                        selected = uiState.buyCurrency,
                        onSelected = onBuyCurrencyChanged,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AmountInput(
                    value = uiState.sellAmountInput,
                    onValueChange = onSellAmountChanged,
                    placeholder = "Amount to sell (${uiState.sellCurrency})"
                )

                Spacer(modifier = Modifier.height(16.dp))

                RatePreview(text = uiState.lastRatePreview)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onConvertClicked,
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "Convert",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.width(12.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                uiState.error?.let { err ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalancesCard(
    balances: List<BalanceUi>
) {
    val listState = rememberLazyListState()

    // Auto-scroll to newly added balance
    LaunchedEffect(balances.size) {
        if (balances.isNotEmpty()) {
            listState.animateScrollToItem(balances.lastIndex)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Your Balances",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(balances.size) { index ->
                    AnimatedBalanceItem(balances[index])
                }
            }

            if (balances.size > 3) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Swipe to see more →",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun AnimatedBalanceItem(balance: BalanceUi) {
    val scale = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
    }

    Spacer(Modifier.width(6.dp))
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .width(140.dp)
            .height(75.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = balance.currency,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.2f".format(balance.amount),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
    Spacer(Modifier.width(6.dp))
}

@Composable
private fun CurrencyDropdown(
    label: String,
    currencies: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(4.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface
                ),
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "select",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                c,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onSelected(c)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AmountInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = { new ->
            // allow digits and decimal point only
            if (new.isEmpty() || new.matches(Regex("""^[0-9]*\.?[0-9]*$"""))) {
                onValueChange(new)
            }
        },
        placeholder = {
            Text(
                placeholder,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun RatePreview(text: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (text != null)
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = text ?: "Rate preview will appear here",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (text != null) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// --- MOCK / PREVIEWS ---

private val mockBalances = listOf(
    BalanceUi("EUR", 1000.00),
    BalanceUi("USD", 200.50),
    BalanceUi("GBP", 50.0),
    BalanceUi("JPY", 10000.0)
)

private val mockCurrencies = listOf("EUR", "USD", "GBP", "JPY", "AUD", "CAD")

@Preview(showBackground = true, name = "Default Preview")
@Composable
fun PreviewCurrencyExchangerDefault() {
    val uiState = ExchangeUiState(
        balances = mockBalances,
        supportedCurrencies = mockCurrencies,
        sellCurrency = "EUR",
        buyCurrency = "USD",
        sellAmountInput = "100",
        lastRatePreview = "1 EUR = 1.08 USD",
        isLoading = false
    )

    CurrencyExchangerScreen(
        uiState = uiState,
        onSellCurrencyChanged = {},
        onBuyCurrencyChanged = {},
        onSellAmountChanged = {},
        onSwapCurrencies = {},
        onConvertClicked = {}
    )
}

@PreviewLightDark
@Composable
fun PreviewCurrencyExchangerLoading() {
    val uiState = ExchangeUiState(
        balances = mockBalances,
        supportedCurrencies = mockCurrencies,
        sellCurrency = "EUR",
        buyCurrency = "USD",
        sellAmountInput = "",
        lastRatePreview = null,
        isLoading = true,
        error = null
    )

    CurrencyExchangerScreen(
        uiState = uiState,
        onSellCurrencyChanged = {},
        onBuyCurrencyChanged = {},
        onSellAmountChanged = {},
        onSwapCurrencies = {},
        onConvertClicked = {}
    )
}

@PreviewLightDark
@Composable
fun PreviewCurrencyExchangerError() {
    val uiState = ExchangeUiState(
        balances = mockBalances,
        supportedCurrencies = mockCurrencies,
        sellCurrency = "EUR",
        buyCurrency = "USD",
        sellAmountInput = "500",
        lastRatePreview = "1 EUR = 1.08 USD",
        isLoading = false,
        error = "Insufficient balance"
    )

    CurrencyExchangerScreen(
        uiState = uiState,
        onSellCurrencyChanged = {},
        onBuyCurrencyChanged = {},
        onSellAmountChanged = {},
        onSwapCurrencies = {},
        onConvertClicked = {}
    )
}
