package com.qyub.mgr2.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.qyub.mgr2.data.repo.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val repo: EventRepository
) : ViewModel()
