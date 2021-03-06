package com.dicoding.tourismapp.domain.usecase

import com.dicoding.tourismapp.core.data.Resource
import com.dicoding.tourismapp.domain.model.Tourism
import kotlinx.coroutines.flow.Flow

interface TourismUseCase {
    fun getAllTourism(): Flow<Resource<List<Tourism>>>
    fun getFavoriteTourism(): Flow<List<Tourism>>
    fun setFavoriteTourism(tourism: Tourism, state: Boolean)
}