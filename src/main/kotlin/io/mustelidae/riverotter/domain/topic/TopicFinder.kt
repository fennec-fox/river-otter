package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.config.DataNotFoundException
import io.mustelidae.riverotter.domain.topic.repository.TopicDSLRepository
import io.mustelidae.riverotter.domain.topic.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TopicFinder(
    private val topicRepository: TopicRepository,
    private val topicDSLRepository: TopicDSLRepository,
) {
    fun findOrThrow(id: String): Topic {
        return topicDSLRepository.findWithCalendarsById(id) ?: throw DataNotFoundException("topic id($id) not found")
    }

    fun findAll(): List<Topic> {
        return topicRepository.findAll()
    }
}
