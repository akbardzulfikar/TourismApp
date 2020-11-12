package com.dicoding.tourismapp.di

import com.dicoding.tourismapp.domain.usecase.TourismInteractor
import com.dicoding.tourismapp.domain.usecase.TourismUseCase
import dagger.Binds
import dagger.Module


@Module
abstract class AppModule {

    @Binds
    abstract fun provideTourismUseCase(tourismInteractor: TourismInteractor): TourismUseCase

}