package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.config.DataNotFoundException
import io.mustelidae.riverotter.domain.topic.repository.TopicRepository
import org.springframework.stereotype.Service

@Service
class TopicFinder(
    private val topicRepository: TopicRepository,
) {
    fun findOrThrow(id: String): Topic {
        return topicRepository.findWithCalendarsById(id) ?: throw DataNotFoundException("topic id($id) not found")
    }

    fun findAll(): List<Topic> {
        return topicRepository.findAll()
    }
}
