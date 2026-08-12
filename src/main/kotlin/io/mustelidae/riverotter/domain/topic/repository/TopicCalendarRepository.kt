package io.mustelidae.riverotter.domain.topic.repository

import io.mustelidae.riverotter.domain.topic.TopicCalendar
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Locale

@Repository
interface TopicCalendarRepository : JpaRepository<TopicCalendar, Long> {

    @EntityGraph(attributePaths = ["holidays"])
    fun findWithHolidaysByTopicIdAndLocaleAndYear(topicId: String, locale: Locale, year: Int): TopicCalendar?

    @EntityGraph(attributePaths = ["workdays"])
    fun findWithWorkdaysByTopicIdAndLocaleAndYear(topicId: String, locale: Locale, year: Int): TopicCalendar?
}
