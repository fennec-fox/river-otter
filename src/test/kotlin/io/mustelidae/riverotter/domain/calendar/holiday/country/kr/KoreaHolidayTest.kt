package io.mustelidae.riverotter.domain.calendar.holiday.country.kr

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mustelidae.riverotter.config.AppEnvironment
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import io.mustelidae.riverotter.domain.calendar.holiday.aFixture
import io.mustelidae.riverotter.domain.calendar.holiday.country.KoreaHoliday
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarDSLRepository
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarRepository
import io.mustelidae.riverotter.domain.client.korea.government.GovernmentOpenDummyClient
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

internal class KoreaHolidayTest {

    private val holidayCalenderRepository: HolidayCalendarRepository = mockk()
    private val holidayCalendarDSLRepository: HolidayCalendarDSLRepository = mockk()
    private val governmentOpenClient = GovernmentOpenDummyClient()
    private val appEnv = AppEnvironment().apply {
        country.korea.saturdayIsHoliday = true
    }
    private val koreaHoliday = KoreaHoliday(appEnv.country.korea, governmentOpenClient, holidayCalenderRepository, holidayCalendarDSLRepository)

    @Test
    fun getWeekend() {
        // Given
        val year = 2020
        // When

        val holidays = koreaHoliday.getWeekend(year, true)

        // Then
        holidays.size shouldBe 104
        holidays.filter { it.date.dayOfWeek == DayOfWeek.SUNDAY }.size shouldBe 52
    }

    @Test
    fun create() {
        // Given
        val year = 2020

        val slot = slot<HolidayCalendar>()
        every { holidayCalendarDSLRepository.findByYearAndLocale(year, any()) } returns null
        every { holidayCalenderRepository.save(capture(slot)) } answers { HolidayCalendar.Companion.aFixture(year) }

        // When
        koreaHoliday.create(year)

        // Then
        val savedCalender = slot.captured
        savedCalender.locale shouldBe koreaHoliday.localeOfCountry
        savedCalender.holidays.size shouldBe 116
    }
}
