package com.qyub.mgr2.domain.usecase

import com.qyub.mgr2.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

class PreloadEventsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(day: LocalDate) {
        return taskRepository.preloadEventsForDate(day)
    }
}