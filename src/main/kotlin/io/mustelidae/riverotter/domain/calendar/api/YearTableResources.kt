package io.mustelidae.riverotter.domain.calendar.api

import io.mustelidae.riverotter.domain.calendar.holiday.repository.HolidayCalendarRepository
import io.swagger.v3.oas.annotations.media.Schema
import java.util.Locale
import java.util.UUID

class YearTableResources {

    class Reply {

        @Schema(name = "YearTable.Reply.Table")
        data class Table(
            val id: String,
            val locale: Locale,
            val years: List<Year>,
        ) {

            @Schema(name = "YearTable.Reply.Table.Year")
            data class Year(
                val year: Int,
                var calendarId: String,
            ) {
                companion object {
                    fun from(yearOfCalendar: HolidayCalendarRepository.YearOfCalendar): Year {
                        return Year(
                            yearOfCalendar.year,
                            yearOfCalendar.id.toString(),
                        )
                    }
                }
            }

            companion object {
                fun from(locale: Locale, yearsOfCalendar: List<HolidayCalendarRepository.YearOfCalendar>): Table {
                    val years = yearsOfCalendar.map { Year.from(it) }
                    val id = UUID.nameUUIDFromBytes(locale.country.toByteArray())

                    return Table(
                        id.toString(),
                        locale,
                        years,
                    )
                }
            }
        }
    }
}
