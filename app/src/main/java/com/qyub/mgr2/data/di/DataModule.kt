package com.qyub.mgr2.data.di

import com.qyub.mgr2.data.datastore.UserPreferencesDataStore
import com.qyub.mgr2.data.repository.EventRepositoryImpl
import com.qyub.mgr2.domain.repository.EventRepository
import com.qyub.mgr2.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesDataStore,
    ): UserPreferencesRepository


    @Binds
    @Singleton
    abstract fun bindEventRepository(
        impl: EventRepositoryImpl,
    ): EventRepository
}