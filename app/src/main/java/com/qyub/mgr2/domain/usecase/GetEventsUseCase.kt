package com.qyub.mgr2.domain.usecase

import com.qyub.mgr2.domain.model.Event
import com.qyub.mgr2.domain.repository.EventRepository
import java.time.LocalDate
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(day: LocalDate): List<Event> {
        return eventRepository.getEventsForDate(day)
    }
}