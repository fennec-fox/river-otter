package io.mustelidae.riverotter.domain.topic.synchronization

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.topic.TopicCalendar
import io.mustelidae.riverotter.domain.topic.TopicWorkSchedule
import java.time.DayOfWeek
import java.time.LocalDate

interface TopicHolidaySynchronization {
    fun syncWorkSchedule(workSchedule: TopicWorkSchedule)
    fun syncTopicCalendar(topicCalendar: TopicCalendar)

    fun makeHolidayOwingToWorkSchedule(
        date: LocalDate,
        workSchedule: TopicWorkSchedule,
        name: String = "Closed",
    ): Holiday? {
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (date.dayOfWeek) {
            DayOfWeek.MONDAY ->
                if (workSchedule.mon.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
            DayOfWeek.TUESDAY ->
                if (workSchedule.tue.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
            DayOfWeek.WEDNESDAY ->
                if (workSchedule.wed.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
            DayOfWeek.THURSDAY ->
                if (workSchedule.thu.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
            DayOfWeek.FRIDAY ->
                if (workSchedule.fri.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
            DayOfWeek.SATURDAY ->
                if (workSchedule.sat.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
            DayOfWeek.SUNDAY ->
                if (workSchedule.sun.isOn.not()) {
                    return Holiday(date, name, Holiday.Type.SCHEDULE_HOLIDAY)
                }
        }
        return null
    }
}
