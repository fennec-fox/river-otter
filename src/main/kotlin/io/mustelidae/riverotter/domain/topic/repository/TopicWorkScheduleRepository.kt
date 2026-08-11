package io.mustelidae.riverotter.domain.topic.repository

import io.mustelidae.riverotter.domain.topic.TopicWorkSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicWorkScheduleRepository : JpaRepository<TopicWorkSchedule, Long> {
    fun findByTopicId(topicId: String): TopicWorkSchedule?
}
