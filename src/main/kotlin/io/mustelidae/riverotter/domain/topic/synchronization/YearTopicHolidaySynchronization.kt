package io.mustelidae.riverotter.domain.topic.synchronization

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.topic.TopicCalendar
import io.mustelidae.riverotter.domain.topic.TopicWorkSchedule
import java.time.DayOfWeek
import java.time.LocalDate

class YearTopicHolidaySynchronization(
    countryHoliday: List<Holiday>,
) : TopicHolidaySynchronization {
    private val topicHolidays: MutableMap<LocalDate, Holiday> = countryHoliday.associateBy { it.date }.toMutableMap()
    private val year: Int = countryHoliday.first().date.year

    override fun syncWorkSchedule(workSchedule: TopicWorkSchedule) {
        val dates = LocalDate.of(year, 1, 1).datesUntil(LocalDate.of(year, 12, 31))
            .toList()

        for (date in dates) {
            // Add holiday
            val holiday = makeHolidayOwingToWorkSchedule(date, workSchedule)
            if (holiday != null) {
                topicHolidays[date] = holiday
            }

            // Remove working days on weekends.
            if (date.dayOfWeek == DayOfWeek.SATURDAY && workSchedule.sat.isOn && topicHolidays.contains(date)) {
                topicHolidays.remove(date)
            }

            if (date.dayOfWeek == DayOfWeek.SUNDAY && workSchedule.sun.isOn && topicHolidays.contains(date)) {
                topicHolidays.remove(date)
            }
        }
    }

    override fun syncTopicCalendar(topicCalendar: TopicCalendar) {
        // Add holiday
        topicHolidays.putAll(
            topicCalendar.holidays.associate { it.date to it.toHoliday() },
        )

        // All days on which a topic works are removed from holidays.
        topicCalendar.workdays.map { it.date }
            .forEach {
                topicHolidays.remove(it)
            }
    }

    fun getHolidays(): List<Holiday> {
        return topicHolidays.values.toList()
    }
}
