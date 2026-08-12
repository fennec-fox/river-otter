package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendarFinder
import io.mustelidae.riverotter.domain.topic.repository.TopicWorkScheduleRepository
import io.mustelidae.riverotter.domain.topic.synchronization.DayTopicHolidaySynchronization
import io.mustelidae.riverotter.domain.topic.synchronization.YearTopicHolidaySynchronization
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Locale

@Service
class TopicHolidayCalendarFinder(
    private val topicFinder: TopicFinder,
    private val holidayCalendarFinder: HolidayCalendarFinder,
    private val topicWorkScheduleRepository: TopicWorkScheduleRepository,
) {

    @Transactional(readOnly = true)
    fun findBy(id: String, locale: Locale, year: Int): HolidayCalendar {
        val topic = topicFinder.findOrThrow(id)
        val topicCalendar = topic.getCalendar(locale, year)
        val countryHolidayCalendar = holidayCalendarFinder.findOrThrow(locale, year)

        val holidaySynchronization = YearTopicHolidaySynchronization(countryHolidayCalendar.holidays)

        topicWorkScheduleRepository.findByTopicId(id)?.let {
            holidaySynchronization.syncWorkSchedule(it)
        }

        topicCalendar?.let {
            holidaySynchronization.syncTopicCalendar(it)
        }

        return HolidayCalendar.virtualOf(locale, year, holidaySynchronization.getHolidays())
    }

    @Transactional(readOnly = true)
    fun findBy(id: String, locale: Locale, year: Int, month: Int): List<Holiday> {
        val holidayCalendar = findBy(id, locale, year)
        return holidayCalendar.holidays.filter { it.date.monthValue == month }
    }

    @Transactional(readOnly = true)
    fun findBy(id: String, locale: Locale, year: Int, month: Int, day: Int): Holiday? {
        val date = LocalDate.of(year, month, day)
        val topic = topicFinder.findOrThrow(id)
        val topicCalendar = topic.getCalendar(locale, year)
        val countryHoliday = holidayCalendarFinder.findBy(locale, year, month, day)

        val holidaySynchronization = DayTopicHolidaySynchronization(date, countryHoliday)

        topicWorkScheduleRepository.findByTopicId(id)?.let {
            holidaySynchronization.syncWorkSchedule(it)
        }

        topicCalendar?.let {
            holidaySynchronization.syncTopicCalendar(it)
        }

        return holidaySynchronization.getHoliday()
    }
}
