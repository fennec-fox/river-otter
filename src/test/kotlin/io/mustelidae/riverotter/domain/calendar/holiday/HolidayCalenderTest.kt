package io.mustelidae.riverotter.domain.calendar.holiday

import java.time.LocalDate
import java.util.Locale

internal class HolidayCalenderTest

internal fun HolidayCalendar.Companion.aFixture(year: Int): HolidayCalendar {
    val holidayCalendar = HolidayCalendar(Locale.KOREA, year)
    holidayCalendar.addBy(
        Holiday(
            LocalDate.of(year, 1, 5),
            "",
            Holiday.Type.WEEKEND_HOLIDAY,
        ),
    )
    return holidayCalendar.withGeneratedId(1L)
}

// Simulates the id a real JPA save() would assign, since MockK-based unit tests never touch a database.
private fun HolidayCalendar.withGeneratedId(id: Long): HolidayCalendar {
    val field = HolidayCalendar::class.java.getDeclaredField("id")
    field.isAccessible = true
    field.set(this, id)
    return this
}
