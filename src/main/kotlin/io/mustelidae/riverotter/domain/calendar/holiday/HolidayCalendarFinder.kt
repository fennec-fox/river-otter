package io.mustelidae.riverotter.domain.calendar.holiday

import io.mustelidae.riverotter.config.DataNotFoundException
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarDSLRepository
import io.mustelidae.riverotter.utils.searchIndex
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.Locale

@Service
class HolidayCalendarFinder(
    private val holidayCalendarDSLRepository: HolidayCalendarDSLRepository,
) {

    fun findBy(locale: Locale, year: Int, month: Int): List<Holiday> {
        val holidayCalendar = findOrThrow(locale, year)
        return holidayCalendar.holidays.filter { it.date.monthValue == month }
    }

    fun findBy(locale: Locale, year: Int, month: Int, day: Int): Holiday? {
        val holidayCalendar = findOrThrow(locale, year)

        val index = holidayCalendar.holidays.searchIndex(LocalDate.of(year, month, day))
        return if (index >= 0) {
            holidayCalendar.holidays[index]
        } else {
            null
        }
    }

    fun findOrThrow(locale: Locale, year: Int): HolidayCalendar {
        return holidayCalendarDSLRepository.findByYearAndLocale(year, locale) ?: throw DataNotFoundException("$locale ($year) is not found")
    }
}
