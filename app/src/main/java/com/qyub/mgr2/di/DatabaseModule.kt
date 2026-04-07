package com.qyub.mgr2.di

import android.content.Context
import com.qyub.mgr2.data.db.AppDatabase
import com.qyub.mgr2.data.db.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideEventDao(appDatabase: AppDatabase): EventDao {
        return appDatabase.eventDao()
    }
}
