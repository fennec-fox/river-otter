package io.mustelidae.riverotter.domain.calendar.holiday.repository

import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Locale

@Repository
interface HolidayCalendarRepository : JpaRepository<HolidayCalendar, Long> {

    fun findAllByLocale(locale: Locale): List<YearOfCalendar>

    interface YearOfCalendar {
        val id: Long
        val year: Int
    }
}
