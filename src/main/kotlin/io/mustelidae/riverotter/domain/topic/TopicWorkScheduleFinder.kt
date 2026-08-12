package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.domain.topic.repository.TopicWorkScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TopicWorkScheduleFinder(
    private val topicWorkScheduleRepository: TopicWorkScheduleRepository,
) {
    fun find(topicId: String): TopicWorkSchedule? = topicWorkScheduleRepository.findByTopicId(topicId)
}
