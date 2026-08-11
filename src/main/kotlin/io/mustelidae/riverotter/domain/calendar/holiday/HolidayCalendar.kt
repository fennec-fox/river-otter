package io.mustelidae.riverotter.domain.calendar.holiday

import io.mustelidae.riverotter.common.Error
import io.mustelidae.riverotter.common.ErrorCode
import io.mustelidae.riverotter.config.HumanException
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

@Entity
@Table(name = "holiday_calendar")
class HolidayCalendar(
    val locale: Locale,
    val year: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @OneToMany(mappedBy = "holidayCalendar", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderBy("date ASC")
    var holidays: MutableList<Holiday> = arrayListOf()
        protected set

    fun addBy(holiday: Holiday) {
        if (hasHoliday(holiday.date)) {
            throw HumanException(Error(ErrorCode.HD03, "There is already a public holiday for that date."))
        }

        holidays.add(holiday)
        if (this != holiday.holidayCalendar) {
            holiday.setBy(this)
        }
    }

    fun removeBy(localDate: LocalDate) {
        val holiday = this.holidays.find { it.date == localDate }

        holiday?.let {
            holidays.remove(holiday)
        }
    }

    private fun hasHoliday(localDate: LocalDate): Boolean {
        val holiday = this.holidays.find { it.date == localDate }
        return (holiday != null)
    }

    companion object {
        // Non-persisted read view; bypasses addBy so it never rewrites an already-persisted Holiday's back-reference.
        fun virtualOf(locale: Locale, year: Int, holidays: List<Holiday>): HolidayCalendar {
            val calendar = HolidayCalendar(locale, year)
            calendar.holidays = holidays.toMutableList()
            return calendar
        }
    }
}
