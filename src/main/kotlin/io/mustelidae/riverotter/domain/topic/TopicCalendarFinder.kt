package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.config.DataNotFoundException
import io.mustelidae.riverotter.domain.topic.repository.TopicCalendarRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TopicCalendarFinder(
    private val topicCalendarRepository: TopicCalendarRepository,
) {

    fun findOrThrow(id: Long): TopicCalendar {
        return topicCalendarRepository.findByIdOrNull(id) ?: throw DataNotFoundException("topic calendar id($id) not found")
    }
}
