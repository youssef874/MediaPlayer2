package com.example.mediaplayer.domain

import com.example.mediaplayer.repository.IAudioConfigurationRepository
import com.example.mediaplayer.repository.IAudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun getFetchDataUseCase(audiDataRepository: IAudioRepository): IFetchDataUseCase {
        return FetchDataUseCase(audiDataRepository)
    }

    @Provides
    @Singleton
    fun getPlayOrStopUseCase(
        audiDataRepository: IAudioRepository,
        fetchDataUseCase: IFetchDataUseCase,
        randomAndRepeatConfigurationUseCase: IRandomAndRepeatConfigurationUseCase
    ): IPlayOrStopAudioUseCase {
        return PlayOrStopAudioUseCase(audiDataRepository, fetchDataUseCase,randomAndRepeatConfigurationUseCase)
    }

    @Provides
    @Singleton
    fun getNextOrPreviousUseCase(
        fetchDataUseCase: IFetchDataUseCase,
        playOrStopAudioUseCase: IPlayOrStopAudioUseCase,
        randomAndRepeatConfigurationUseCase: IRandomAndRepeatConfigurationUseCase
    ): INextOrPreviousUseCase {
        return NextOrPreviousUseCase(fetchDataUseCase, playOrStopAudioUseCase,randomAndRepeatConfigurationUseCase)
    }

    @Provides
    @Singleton
    fun getRandomAndRepeatConfigurationUseCase(audioConfigurationRepository: IAudioConfigurationRepository): IRandomAndRepeatConfigurationUseCase {
        return RandomAndRepeatConfigurationUseCase(audioConfigurationRepository)
    }

    @Provides
    @Singleton
    fun getEditSongUseCase(audiDataRepository: IAudioRepository): IEditSongUseCase{
        return EditSongUseCase(audiDataRepository)
    }
}