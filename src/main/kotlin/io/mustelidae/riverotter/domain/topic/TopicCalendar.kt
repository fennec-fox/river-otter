package io.mustelidae.riverotter.domain.topic

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

@Entity
@Table(name = "topic_calendar")
class TopicCalendar(
    val locale: Locale,
    val year: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "topicId")
    var topic: Topic? = null
        protected set

    @OneToMany(mappedBy = "topicCalendar", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderBy("date ASC")
    var holidays: MutableList<TopicHoliday> = arrayListOf()
        protected set

    @OneToMany(mappedBy = "topicCalendar", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderBy("date ASC")
    var workdays: MutableList<Workday> = arrayListOf()
        protected set

    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    var modifiedAt: LocalDateTime = LocalDateTime.now()
        protected set

    fun setBy(topic: Topic) {
        this.topic = topic
        if (!topic.calendars.contains(this)) {
            topic.addBy(this)
        }
    }

    fun addBy(holiday: TopicHoliday) {
        holidays.find { it.date == holiday.date }?.let {
            throw IllegalArgumentException("already exists working day")
        }

        holidays.add(holiday)
        if (this != holiday.topicCalendar) {
            holiday.setBy(this)
        }
        modifiedAt = LocalDateTime.now()
    }

    fun addBy(workday: Workday) {
        workdays.find { it.date == workday.date }?.let {
            throw IllegalArgumentException("already exists working day")
        }

        workdays.add(workday)
        if (this != workday.topicCalendar) {
            workday.setBy(this)
        }
        modifiedAt = LocalDateTime.now()
    }

    fun removeHoliday(date: LocalDate) {
        holidays.removeIf { it.date == date }
        modifiedAt = LocalDateTime.now()
    }

    fun removeWorkDay(date: LocalDate) {
        workdays.removeIf { it.date == date }
        modifiedAt = LocalDateTime.now()
    }

    companion object
}
