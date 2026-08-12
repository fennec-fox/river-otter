package io.mustelidae.riverotter.domain.calendar.holiday.country

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.util.Locale

interface CountryHoliday {
    val localeOfCountry: Locale

    fun create(year: Int): Long

    fun getWeekend(year: Int, saturdayIsHoliday: Boolean): List<Holiday> {
        val startOfYear = LocalDate.of(year, 1, 1)

        val holidays = mutableListOf<Holiday>()

        val nameOfHoliday = when (localeOfCountry) {
            Locale.KOREA -> "주말"
            Locale.JAPAN -> "週末"
            Locale.CHINESE -> "周末"
            else -> "Weekend"
        }

        for (i in 0..364) {
            val date = startOfYear.plusDays(i.toLong())
            val dayOfWeek = date.dayOfWeek

            if (dayOfWeek == SUNDAY) {
                holidays.add(Holiday(date, nameOfHoliday, Holiday.Type.WEEKEND_HOLIDAY))
            }

            if (dayOfWeek == SATURDAY && saturdayIsHoliday) {
                holidays.add(Holiday(date, nameOfHoliday, Holiday.Type.WEEKEND_HOLIDAY))
            }
        }

        return holidays
    }
}
