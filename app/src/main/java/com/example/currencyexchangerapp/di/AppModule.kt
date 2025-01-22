package com.example.currencyexchangerapp.di

import com.example.currencyexchangerapp.data.remote.services.ExchangeRateApiService
import com.example.currencyexchangerapp.data.repository.CurrencyRepositoryImpl
import com.example.currencyexchangerapp.domain.repository.CurrencyRepository
import com.example.currencyexchangerapp.domain.usecases.exchange.GetExchangeRatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 11/19/2025.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideHttpClient(): HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) { json() }
        }

    @Provides @Singleton
    fun provideApiService(client: HttpClient): ExchangeRateApiService =
        ExchangeRateApiService(client)

    @Provides
    @Singleton
    fun provideCurrencyRepository(api: ExchangeRateApiService): CurrencyRepository = CurrencyRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideConvertUseCase(repository: CurrencyRepository): GetExchangeRatesUseCase = GetExchangeRatesUseCase(repository)
}