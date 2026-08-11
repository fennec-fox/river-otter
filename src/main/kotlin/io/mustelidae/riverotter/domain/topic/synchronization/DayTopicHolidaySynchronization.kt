package io.mustelidae.riverotter.domain.topic.synchronization

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.topic.TopicCalendar
import io.mustelidae.riverotter.domain.topic.TopicWorkSchedule
import java.time.DayOfWeek
import java.time.LocalDate

class DayTopicHolidaySynchronization(
    val date: LocalDate,
    countryHoliday: Holiday?,
) : TopicHolidaySynchronization {

    private var topicHoliday: Holiday? = countryHoliday

    override fun syncWorkSchedule(workSchedule: TopicWorkSchedule) {
        topicHoliday = makeHolidayOwingToWorkSchedule(date, workSchedule)

        // Remove working days on weekends.
        if (date.dayOfWeek == DayOfWeek.SATURDAY && workSchedule.sat.isOn) {
            topicHoliday = null
        }

        if (date.dayOfWeek == DayOfWeek.SUNDAY && workSchedule.sun.isOn) {
            topicHoliday = null
        }
    }

    override fun syncTopicCalendar(topicCalendar: TopicCalendar) {
        topicCalendar.holidays.find { it.date == date }?.let {
            topicHoliday = it.toHoliday()
        }

        topicCalendar.workdays.find { it.date == date }?.let {
            topicHoliday = null
        }
    }

    fun getHoliday(): Holiday? = topicHoliday
}
