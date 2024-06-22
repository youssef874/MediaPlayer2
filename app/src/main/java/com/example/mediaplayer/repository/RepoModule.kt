package com.example.mediaplayer.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideAudioRepo(): IAudioRepository{
        return AudioRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideAudioConfigurationRepo(): IAudioConfigurationRepository{
        return AudioConfigurationRepositoryImpl
    }
}