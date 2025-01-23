CURRENCY EXCHANGER APP

A modern Android currency exchange application built with Kotlin and Jetpack Compose, featuring real-time exchange rates and an intuitive user interface.

--Features--

- **Real-time Currency Conversion**: Convert between multiple currencies instantly
- **Live Exchange Rates**: Automatically refreshes rates every 5 seconds from Paysera API
- **Balance Management**: Track multiple currency balances with visual cards
- **Smart Validation**: Prevents negative balances and invalid conversions
- **Rate Preview**: See conversion rates in real-time as you type
- **Modern UI**: Material Design 3 with Paysera brand colors
- **Dark Mode Support**: Adaptive theme based on system preferences
- **Smooth Animations**: Polished entrance animations for balance cards
- **Error Handling**: Graceful handling of network issues and edge cases

--Architecture--

The app follows **Clean Architecture** principles with **MVVM** pattern for clear separation of concerns:

```
┌───────────────────────────────────────────────┐
│      		         Presentation Layer           │
│  ┌──────────────────┐    ┌──────────────────┐ │
│  │ CurrencyExchanger│◄───│ExchangeViewModel │ │
│  │      Screen      │    │                  │ │
│  └──────────────────┘    └──────────────────┘ │
└───────────────────┬───────────────────────────┘
                    │
┌───────────────────▼───────────────────────────────┐
│                  Domain Layer                     │
│           ┌──────────────────────────┐            │
│           │ GetExchangeRatesUseCase  │            │
│           └──────────────────────────┘            │
└───────────────────┬───────────────────────────────┘
                    │
┌───────────────────▼───────────────────────────────┐
│              		 Data Layer                       │
│  ┌──────────────────────┐   ┌─────────────────┐   │
│  │CurrencyRepositoryImpl│ ◄─│ ExchangeRateApi │   │
│  │                 		  │   │       Service   │   │
│  └──────────────────────┘   └─────────────────┘   │
└───────────────────────────────────────────────────┘
```

--Layer Responsibilities--

1. Presentation Layer

- **CurrencyExchangerScreen**: Composable UI with Material Design 3
- **ExchangeViewModel**: Manages UI state, business logic, and user interactions
- **ExchangeUiState**: Immutable state holder for UI

2. Domain Layer

- **GetExchangeRatesUseCase**: Encapsulates exchange rate fetching logic
- Acts as a bridge between presentation and data layers

3. Data Layer

- **CurrencyRepository**: Abstract data source interface
- **CurrencyRepositoryImpl**: Concrete implementation with auto-refresh
- **ExchangeRateApiService**: Ktor HTTP client for API calls

--Technologies & Libraries--

| Technology            | Purpose                         |
| --------------------- | ------------------------------- |
| **Kotlin**            | Primary programming language    |
| **Jetpack Compose**   | Modern declarative UI framework |
| **Kotlin Coroutines** | Asynchronous programming        |
| **Kotlin Flow**       | Reactive data streams           |
| **Hilt**              | Dependency injection            |
| **Ktor Client**       | HTTP networking                 |
| **Material Design 3** | UI components and theming       |
| **StateFlow**         | State management                |

--Setup & Installation--

1. Clone the Repository

```bash
git clone <your-repository-url>
cd currency-exchanger-app
```

2. Open in Android Studio

- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned directory
- Click "OK"

3. Sync Gradle
   Android Studio will automatically start syncing Gradle. If not:

- Click "File" → "Sync Project with Gradle Files"
- Wait for dependencies to download

4. Run the App

- Connect an Android device or start an emulator
- Click the "Run" button (▶️) or press `Shift + F10`
- Select your target device
- Wait for the app to build and install

--Running Tests--

### Run All Unit Tests

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests ExchangeViewModelTest
```

### View Test Report

After running tests, open:

```
app/build/reports/tests/testDebugUnitTest/index.html
```

---API Information--

1. Exchange Rates API

- **Method**: GET
- **Base Currency**: EUR
- **Refresh Rate**: Every 5 seconds (automatic)
- **No Authentication Required**

2. Sample Response

```json
{
  "base": "EUR",
  "date": "2024-01-15",
  "rates": {
    "USD": 1.0896,
    "JPY": 159.52,
    "GBP": 0.8572,
    "AUD": 1.6234,
    ...
  }
}
```

--App Features Breakdown--

Currency Conversion

1. Select "From" currency
2. Select "To" currency
3. Enter amount to convert
4. View real-time rate preview
5. Click "Convert" to execute transaction

Balance Management

- Initial balance: 1000 EUR
- View all balances in scrollable cards
- Balances update after each conversion
- New currency balances are automatically added
- Cannot convert more than available balance

### Validation Rules

- ✅ Amount must be greater than zero
- ✅ Cannot convert to the same currency
- ✅ Must have sufficient balance
- ✅ Only numeric input allowed with max 2 decimal places

## 🎨 UI/UX Features

--Theming--

- **Light Mode**: Clean white surfaces with Paysera blue accents
- **Dark Mode**: Dark backgrounds with adjusted colors for visibility
- Automatically adapts to system theme

--Animations--

- Balance cards scale in on appearance
- Auto-scroll to newly added currencies
- Smooth loading indicators
- Responsive button states

--Error Handling--

- Network errors display user-friendly messages
- Input validation with inline error messages
- Retry mechanism for failed API calls
- Loading states clearly indicated

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/yourpackage/
│   │   │   ├── data/
│   │   │   │   ├── remote/
│   │   │   │   │   ├── ExchangeRateApiService.kt
│   │   │   │   │   └── dto/
│   │   │   │   │       └── ExchangeRateResponse.kt
│   │   │   │   └── repository/
│   │   │   │       ├── CurrencyRepository.kt
│   │   │   │       └── CurrencyRepositoryImpl.kt
│   │   │   ├── domain/
│   │   │   │   └── usecase/
│   │   │   │       └── GetExchangeRatesUseCase.kt
│   │   │   ├── presentation/
│   │   │   │   ├── screen/
│   │   │   │   │   └── CurrencyExchangerScreen.kt
│   │   │   │   └── viewmodel/
│   │   │   │       ├── ExchangeViewModel.kt
│   │   │   │       └── ExchangeUiState.kt
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt
│   │   │   └── ui/
│   │   │       └── theme/
│   │   │           ├── Color.kt
│   │   │           ├── Theme.kt
│   │   │           └── Type.kt
│   │   └── res/
│   └── test/
│       └── java/com/yourpackage/
│           ├── viewmodel/
│           │   └── ExchangeViewModelTest.kt
│           ├── repository/
│           │   └── CurrencyRepositoryTest.kt
│           └── usecase/
│               └── GetExchangeRatesUseCaseTest.kt
```

--Key Components--

### ExchangeUiState

```kotlin
data class ExchangeUiState(
    val balances: List<BalanceUi>,
    val supportedCurrencies: List<String>,
    val sellCurrency: String,
    val buyCurrency: String,
    val sellAmountInput: String,
    val lastRatePreview: String?,
    val isLoading: Boolean,
    val error: String? = null
)
```

--Rate Calculation Logic--

- **EUR to Other**: Direct rate from API
- **Other to EUR**: Inverse of API rate (1 / rate)
- **Other to Other**: Cross-rate calculation (toRate / fromRate)

--Known Issues & Limitations--

- Rates are fetched continuously (consider pausing when app is backgrounded)
- No local persistence (balances reset on app restart)
- No transaction history
- No commission fees implemented
- Limited to currencies with the provided API

--Future Enhancements--

- [ ] Add transaction history
- [ ] Implement commission fees (0.7% after first 5 conversions)
- [ ] Persist balances locally with DataStore/Room
- [ ] Add currency search/filter in dropdown
- [ ] Support for favorites currencies
- [ ] Chart view for rate history
- [ ] Pull-to-refresh for manual rate updates
- [ ] Offline mode with cached rates
- [ ] Multi-language support
- [ ] Accessibility improvements

-- License

This project was created as a technical assessment task.

-Author-

Created by John Ralph Dela Rosa for technical assessment.

-Acknowledgments-

- Exchange rates was provided
- Built with modern Android development best practices

---

**Last Updated**: January 2025  
**Version**: 1.0.0
