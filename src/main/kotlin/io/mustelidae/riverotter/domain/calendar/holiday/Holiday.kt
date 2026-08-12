package io.mustelidae.riverotter.domain.calendar.holiday

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.ZoneOffset

@Entity
@Table(name = "holiday")
class Holiday(
    val date: LocalDate,
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: Type,
    val description: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "holidayCalendarId")
    var holidayCalendar: HolidayCalendar? = null
        protected set

    val time: Long
        get() = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

    fun setBy(holidayCalendar: HolidayCalendar) {
        this.holidayCalendar = holidayCalendar
        if (!holidayCalendar.holidays.contains(this)) {
            holidayCalendar.addBy(this)
        }
    }

    enum class Type {
        PUBLIC_HOLIDAY,
        WEEKEND_HOLIDAY,
        TOPIC_HOLIDAY,
        SCHEDULE_HOLIDAY,
    }
}
