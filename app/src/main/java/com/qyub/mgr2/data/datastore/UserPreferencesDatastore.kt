package com.qyub.mgr2.data.datastore

import com.qyub.mgr2.domain.model.UserPreferences
import com.qyub.mgr2.domain.repository.UserPreferencesRepository
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.preferences: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserPreferencesRepository {

    private object Keys {
        val TEST_PREFERENCE = booleanPreferencesKey("test_preference")
    }

    override fun getUserPreferences(): Flow<UserPreferences> =
        context.preferences.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences())
                else throw e
            }
            .map { prefs -> prefs.toUserPreferences() }

    override suspend fun setTestPreference(enabled: Boolean) {
        context.preferences.edit { it[Keys.TEST_PREFERENCE] = enabled }
    }

    private fun Preferences.toUserPreferences() = UserPreferences(
        testPreference = get(Keys.TEST_PREFERENCE) ?: false
    )
}