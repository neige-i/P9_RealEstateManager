package com.openclassrooms.realestatemanager.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import java.time.ZoneId
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FrameworkProvidingModule {

    @Singleton
    @Provides
    fun provideDefaultClock(): Clock = Clock.systemDefaultZone()

    @Singleton
    @Provides
    fun provideDefaultZoneId(): ZoneId = ZoneId.systemDefault()
}