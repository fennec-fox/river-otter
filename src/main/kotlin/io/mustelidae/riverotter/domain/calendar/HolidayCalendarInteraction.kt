package io.mustelidae.riverotter.domain.calendar

import io.mustelidae.riverotter.config.AppEnvironment
import io.mustelidae.riverotter.config.DataNotFoundException
import io.mustelidae.riverotter.domain.calendar.api.HolidayCalendarResources
import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendarFinder
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCrawler
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarRepository
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Locale

@Service
class HolidayCalendarInteraction(
    private val environment: AppEnvironment,
    private val holidayCrawler: HolidayCrawler,
    private val holidayCalendarFinder: HolidayCalendarFinder,
    private val holidayCalendarRepository: HolidayCalendarRepository,
    private val holidayRepository: HolidayRepository,
) {

    fun crawling(year: Int): List<Pair<Locale, Long>> {
        val locales = environment.getAvailableLocales()

        return locales.map {
            crawling(year, it)
        }
    }

    @Transactional
    fun crawling(year: Int, locale: Locale): Pair<Locale, Long> {
        val id = holidayCrawler.crawling(year, locale)
        return Pair(locale, id)
    }

    @Transactional
    fun add(year: Int, locale: Locale, request: HolidayCalendarResources.Request.Holiday) {
        val holidayCalendar = holidayCalendarFinder.findOrThrow(locale, year)

        val holiday = request.run {
            Holiday(date, name, type, description)
        }

        holidayCalendar.addBy(holiday)

        holidayCalendarRepository.save(holidayCalendar)
    }

    @Transactional
    fun remove(locale: Locale, date: LocalDate) {
        val year = date.year
        val holidayCalendar = holidayCalendarFinder.findOrThrow(locale, year)
        val holiday = holidayCalendar.holidays.find { it.date == date }
            ?: throw DataNotFoundException("$locale ($date) holiday is not found")

        holidayCalendar.removeBy(date)
        holidayRepository.delete(holiday)
    }
}
