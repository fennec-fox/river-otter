package io.mustelidae.riverotter.domain.topic.repository

import io.mustelidae.riverotter.domain.topic.TopicHoliday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicHolidayRepository : JpaRepository<TopicHoliday, Long>
