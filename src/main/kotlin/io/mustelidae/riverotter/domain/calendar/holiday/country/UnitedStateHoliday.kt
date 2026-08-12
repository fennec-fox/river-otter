package io.mustelidae.riverotter.domain.calendar.holiday.country

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarRepository
import io.mustelidae.riverotter.domain.client.abstractapi.WorldHolidayClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.Locale

class UnitedStateHoliday(
    private val worldHolidayClient: WorldHolidayClient,
    private val holidayCalendarRepository: HolidayCalendarRepository,
) : CountryHoliday {
    override val localeOfCountry: Locale = Locale.US

    override fun create(year: Int): Long {
        val holidayCalendar = holidayCalendarRepository.findByYearAndLocale(year, localeOfCountry)
        if (holidayCalendar != null) {
            return holidayCalendar.id!!
        }

        val weekendHolidays = super.getWeekend(year, false)

        val startOfYear = LocalDate.of(year, 1, 1)

        val allDays = mutableListOf<LocalDate>()
        for (i in 0..364) {
            allDays.add(startOfYear.plusDays(i.toLong()))
        }

        val chunkedDays = allDays.chunked(50)
        var publicHolidays: List<Holiday>? = null

        runBlocking {
            publicHolidays = chunkedDays.map { dates ->
                async(Dispatchers.Default) {
                    getHolidays(localeOfCountry, dates)
                }
            }.flatMap { it.await() }
        }

        val holidays = mutableListOf<Holiday>().apply {
            publicHolidays?.let { addAll(it) }
            addAll(weekendHolidays)
        }

        for (weekendHoliday in weekendHolidays) {
            publicHolidays?.find { it.date == weekendHoliday.date }?.let {
                holidays.remove(weekendHoliday)
            }
        }

        holidays.sortBy { it.time }

        val calendar = HolidayCalendar(localeOfCountry, year)
        holidays.forEach { calendar.addBy(it) }

        return holidayCalendarRepository.save(calendar).id!!
    }

    private fun getHolidays(locale: Locale, days: List<LocalDate>): List<Holiday> {
        val holidays = mutableListOf<Holiday>()
        for (date in days) {
            val holiday = getHoliday(locale, date)
            if (holiday != null) {
                holidays.add(holiday)
            }
        }

        return holidays
    }

    private fun getHoliday(locale: Locale, localDate: LocalDate): Holiday? {
        val replies = worldHolidayClient.findHoliday(locale, localDate.year, localDate.monthValue, localDate.dayOfMonth)
        return if (replies.isEmpty().not()) {
            val reply = replies.first()

            val name = if (reply.name_local.isBlank()) reply.name else reply.name_local
            Holiday(
                localDate,
                name,
                Holiday.Type.PUBLIC_HOLIDAY,
                reply.description,
            )
        } else {
            null
        }
    }
}
