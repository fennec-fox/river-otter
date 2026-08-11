package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
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

@Entity
@Table(name = "topic_holiday")
class TopicHoliday(
    val date: LocalDate,
    val name: String,
    val description: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    val type: Holiday.Type = Holiday.Type.TOPIC_HOLIDAY

    @ManyToOne
    @JoinColumn(name = "topicCalendarId")
    var topicCalendar: TopicCalendar? = null
        protected set

    fun setBy(topicCalendar: TopicCalendar) {
        this.topicCalendar = topicCalendar
        if (!topicCalendar.holidays.contains(this)) {
            topicCalendar.addBy(this)
        }
    }

    fun toHoliday(): Holiday = Holiday(date, name, type, description)
}
