package com.qyub.mgr2.domain.repository

import com.qyub.mgr2.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun setTestPreference(enabled: Boolean)
}