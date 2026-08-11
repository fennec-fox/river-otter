package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.domain.topic.repository.TopicCalendarRepository
import io.mustelidae.riverotter.domain.topic.repository.TopicHolidayRepository
import io.mustelidae.riverotter.domain.topic.repository.WorkdayRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Locale

@Service
class TopicCalendarInteraction(
    private val topicCalendarFinder: TopicCalendarFinder,
    private val topicCalendarRepository: TopicCalendarRepository,
    private val topicHolidayRepository: TopicHolidayRepository,
    private val workdayRepository: WorkdayRepository,
) {

    @Transactional
    fun add(topic: Topic, locale: Locale, year: Int): Long {
        val topicCalendar = TopicCalendar(locale, year)
        topic.addBy(topicCalendar)
        return topicCalendarRepository.save(topicCalendar).id!!
    }

    @Transactional
    fun remove(id: Long) {
        topicCalendarRepository.deleteById(id)
    }

    @Transactional
    fun addHoliday(id: Long, date: LocalDate, name: String, description: String? = null) {
        val topicCalendar = topicCalendarFinder.findOrThrow(id)
        val holiday = TopicHoliday(date, name, description)

        topicCalendar.addBy(holiday)
        topicCalendarRepository.save(topicCalendar)
    }

    @Transactional
    fun addWorkday(id: Long, date: LocalDate, name: String, type: Workday.Type, description: String? = null) {
        val topicCalendar = topicCalendarFinder.findOrThrow(id)
        val workday = Workday(date, name, type, description)

        topicCalendar.addBy(workday)
        topicCalendarRepository.save(topicCalendar)
    }

    @Transactional
    fun removeHoliday(id: Long, date: LocalDate) {
        val topicCalendar = topicCalendarFinder.findOrThrow(id)
        val holiday = topicCalendar.holidays.find { it.date == date } ?: return

        topicCalendar.removeHoliday(date)
        topicHolidayRepository.delete(holiday)
    }

    @Transactional
    fun removeWorkday(id: Long, date: LocalDate) {
        val topicCalendar = topicCalendarFinder.findOrThrow(id)
        val workday = topicCalendar.workdays.find { it.date == date } ?: return

        topicCalendar.removeWorkDay(date)
        workdayRepository.delete(workday)
    }
}
