package io.mustelidae.riverotter.domain.calendar.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.riverotter.common.AvailableCountry
import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import io.mustelidae.riverotter.domain.calendar.holiday.Holiday.Type
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class HolidayCalendarResources {

    class Request {
        @Schema(name = "HolidayCalendar.Request.Crawling")
        data class Crawling(
            val year: Int? = null,
            val country: String? = null,
        ) {
            fun getYear(): Int = this.year ?: LocalDate.now().plusYears(1).year

            fun getLocale(): Locale? {
                return if (country.isNullOrEmpty().not()) {
                    AvailableCountry.getLocale(country!!)
                } else {
                    null
                }
            }
        }

        data class Holiday(
            val date: LocalDate,
            val name: String,
            val type: Type,
            val description: String? = null,
        )
    }

    class Reply {

        @Schema(name = "HolidayCalendar.Reply.YearOfCountry")
        data class YearOfCountry(
            val country: String,
            val id: String,
            val year: Int,
        )

        @Schema(name = "HolidayCalendar.Reply.Calendar")
        data class Calendar(
            val id: String,
            val country: String,
            val year: Int,
            val holidays: List<HolidayOfCountry>,
            val createdAt: LocalDateTime?,
        ) {
            companion object {
                fun from(holidayCalendar: HolidayCalendar): Calendar {
                    val holidays = holidayCalendar.holidays.map { HolidayOfCountry.from(it, holidayCalendar.locale) }
                    return Calendar(
                        holidayCalendar.id.toString(),
                        holidayCalendar.locale.country,
                        holidayCalendar.year,
                        holidays,
                        holidayCalendar.createdAt,
                    )
                }
            }
        }

        @Schema(name = "HolidayCalendar.Reply.DayOfHoliday")
        data class DayOfHoliday(
            @get:JsonProperty("isHoliday")
            val isHoliday: Boolean,
            val holiday: HolidayOfCountry? = null,
        ) {
            companion object {
                fun fromNotHoliday(): DayOfHoliday = DayOfHoliday(false, null)

                fun fromHasHoliday(holiday: Holiday, locale: Locale): DayOfHoliday {
                    return DayOfHoliday(
                        true,
                        HolidayOfCountry.from(holiday, locale),
                    )
                }
            }
        }

        @Schema(name = "HolidayCalendar.Reply.Day")
        data class HolidayOfCountry(
            val date: LocalDate,
            val month: Int,
            val day: Int,
            val name: String,
            val type: Type,
            val dayOfWeek: String,
            val description: String? = null,
        ) {
            companion object {
                fun from(holiday: Holiday, locale: Locale): HolidayOfCountry {
                    return holiday.run {
                        HolidayOfCountry(
                            date,
                            date.monthValue,
                            date.dayOfMonth,
                            name,
                            type,
                            date.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
                            description,
                        )
                    }
                }
            }
        }
    }
}
