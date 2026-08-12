package io.mustelidae.riverotter.domain.calendar.holiday.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.mustelidae.riverotter.domain.calendar.holiday.HolidayCalendar
import io.mustelidae.riverotter.domain.calendar.holiday.QHoliday.holiday
import io.mustelidae.riverotter.domain.calendar.holiday.QHolidayCalendar.holidayCalendar
import org.springframework.stereotype.Repository
import java.util.Locale

@Repository
class HolidayCalendarDSLRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findByYearAndLocale(year: Int, locale: Locale): HolidayCalendar? =
        queryFactory
            .selectFrom(holidayCalendar)
            .leftJoin(holidayCalendar.holidays, holiday).fetchJoin()
            .where(
                holidayCalendar.year.eq(year),
                holidayCalendar.locale.eq(locale),
            )
            .fetchOne()
}
