package io.mustelidae.riverotter.domain.topic.repository

import io.mustelidae.riverotter.domain.topic.Topic
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : JpaRepository<Topic, String> {

    @EntityGraph(attributePaths = ["calendars"])
    fun findWithCalendarsById(id: String): Topic?
}
