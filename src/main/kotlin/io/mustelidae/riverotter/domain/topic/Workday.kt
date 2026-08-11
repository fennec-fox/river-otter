package io.mustelidae.riverotter.domain.topic

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
@Table(name = "topic_workday")
class Workday(
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
    @JoinColumn(name = "topicCalendarId")
    var topicCalendar: TopicCalendar? = null
        protected set

    fun setBy(topicCalendar: TopicCalendar) {
        this.topicCalendar = topicCalendar
        if (!topicCalendar.workdays.contains(this)) {
            topicCalendar.addBy(this)
        }
    }

    enum class Type {
        REPLACED_WORK,
        FORCE_WORK,
    }
}
