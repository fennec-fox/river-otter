package io.mustelidae.riverotter.domain.topic.repository

import io.mustelidae.riverotter.domain.topic.TopicCalendar
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicCalendarRepository : JpaRepository<TopicCalendar, Long>
