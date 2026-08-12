package io.mustelidae.riverotter.domain.calendar.holiday.country

import io.mustelidae.riverotter.config.AppEnvironment
import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarDSLRepository
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarRepository
import io.mustelidae.riverotter.domain.client.korea.government.GovernmentOpenClient
import io.mustelidae.riverotter.utils.sort
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class KoreaHoliday(
    korEnv: AppEnvironment.Country.Korea,
    private val governmentOpenClient: GovernmentOpenClient,
    private val holidayCalendarRepository: HolidayCalendarRepository,
    private val holidayCalendarDSLRepository: HolidayCalendarDSLRepository,
) : CountryHoliday {

    override val localeOfCountry: Locale = Locale.KOREA
    private val patternOfLocDate = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val saturdayIsHoliday = korEnv.saturdayIsHoliday

    override fun create(year: Int): Long {
        val holidayCalendar = holidayCalendarDSLRepository.findByYearAndLocale(year, localeOfCountry)
        if (holidayCalendar != null) {
            return holidayCalendar.id!!
        }

        val weekendHolidays = super.getWeekend(year, saturdayIsHoliday)

        val publicHolidays = governmentOpenClient.findAllHoliday(year)
            .response.body.items.item
            .map {
                val date = LocalDate.parse(it.locdate.toString(), patternOfLocDate)
                Holiday(
                    date,
                    it.dateName,
                    Holiday.Type.PUBLIC_HOLIDAY,
                )
            }

        val holidays = mutableListOf<Holiday>().apply {
            addAll(publicHolidays)
            addAll(weekendHolidays)
        }

        for (weekendHoliday in weekendHolidays) {
            publicHolidays.find { it.date == weekendHoliday.date }?.let {
                holidays.remove(weekendHoliday)
            }
        }

        holidays.sort()

        val calendar = HolidayCalendar(localeOfCountry, year)
        holidays.forEach { calendar.addBy(it) }

        return holidayCalendarRepository.save(calendar).id!!
    }
}
