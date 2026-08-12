package io.mustelidae.riverotter.domain.calendar.api

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.riverotter.common.AvailableCountry
import io.mustelidae.riverotter.config.IntegrationSupport
import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarDSLRepository
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class HolidayCalendarControllerTest : IntegrationSupport() {

    @Autowired
    private lateinit var holidayCalendarController: HolidayCalendarController

    @Autowired
    private lateinit var holidayCalendarDSLRepository: HolidayCalendarDSLRepository

    private val year = 2020

    @Test
    @Order(1)
    fun crawling() {
        // Given

        val request = HolidayCalendarResources.Request.Crawling(
            year,
        )

        // When
        val yearOfCountries = holidayCalendarController.crawling(request).getContent()

        // Then

        for (yearOfCountry in yearOfCountries) {
            yearOfCountry.id

            val calendar = holidayCalendarDSLRepository.findByYearAndLocale(yearOfCountry.year, AvailableCountry.getLocale(yearOfCountry.country))

            calendar shouldNotBe null
            calendar!!.holidays.size shouldBeGreaterThan 0
            calendar.year shouldBe year
        }
    }

    @Test
    @Order(2)
    fun findYear() {
        // Given
        val country = "KR"
        // When
        val holiday = holidayCalendarController.findYear(country, year).content!!
        // Then
        holiday.year shouldBe year
        holiday.country shouldBe country
        holiday.holidays.size shouldBe 116
    }

    @Test
    @Order(3)
    fun findMonth() {
        // Given
        val country = "US"
        val month = 1
        // When
        val holidays = holidayCalendarController.findMonth(country, year, month).getContent()
        // Then
        holidays.size shouldBe 6
    }

    @Test
    @Order(4)
    fun findDay() {
        // Given
        val country = "US"
        val month = 1
        val day = 1
        // When
        val holiday = holidayCalendarController.findDay(country, year, month, day).content!!
        // Then
        holiday.isHoliday shouldBe true
    }

    @Test
    @Order(5)
    fun findDayWithParam() {
        // Given
        val country = "KR"
        val month = 1
        val day = 1
        // When
        val holiday = holidayCalendarController.findDayWithParam(country, year, month, day).content!!
        // Then
        holiday.isHoliday shouldBe true
    }
}
