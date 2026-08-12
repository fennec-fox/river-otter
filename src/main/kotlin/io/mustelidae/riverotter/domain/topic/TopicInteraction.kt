package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.domain.topic.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Locale
import java.util.UUID

@Service
class TopicInteraction(
    private val topicFinder: TopicFinder,
    private val topicRepository: TopicRepository,
    private val topicCalendarInteraction: TopicCalendarInteraction,
) {

    @Transactional
    fun addBy(name: String, code: String?): String {
        val id = code ?: UUID.randomUUID().toString()

        val topic = Topic(id, name)
        return topicRepository.save(topic).id
    }

    @Transactional
    fun remove(id: String) {
        topicRepository.deleteById(id)
    }

    @Transactional
    fun addHoliday(id: String, locale: Locale, date: LocalDate, name: String, description: String? = null) {
        val topic = topicFinder.findOrThrow(id)
        val calendarId = addCalendarIfNotExist(topic, locale, date)
        topicCalendarInteraction.addHoliday(calendarId, date, name, description)
    }

    @Transactional
    fun removeHoliday(id: String, locale: Locale, date: LocalDate) {
        val topic = topicFinder.findOrThrow(id)
        val calendar = topic.getCalendar(locale, date.year)
        if (calendar != null) {
            topicCalendarInteraction.removeHoliday(calendar.id!!, date)
        }
    }

    @Transactional
    fun addWorkday(id: String, locale: Locale, date: LocalDate, name: String, type: Workday.Type, description: String? = null) {
        val topic = topicFinder.findOrThrow(id)
        val calendarId = addCalendarIfNotExist(topic, locale, date)
        topicCalendarInteraction.addWorkday(calendarId, date, name, type, description)
    }

    @Transactional
    fun removeWorkday(id: String, locale: Locale, date: LocalDate) {
        val topic = topicFinder.findOrThrow(id)
        val calendar = topic.getCalendar(locale, date.year)
        if (calendar != null) {
            topicCalendarInteraction.removeWorkday(calendar.id!!, date)
        }
    }

    private fun addCalendarIfNotExist(
        topic: Topic,
        locale: Locale,
        date: LocalDate,
    ): Long {
        val hasCalendar = topic.hasCalendar(locale, date.year)

        return if (hasCalendar.not()) {
            topicCalendarInteraction.add(topic, locale, date.year)
        } else {
            topic.getCalendar(locale, date.year)!!.id!!
        }
    }
}
