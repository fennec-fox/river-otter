package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.common.Audit
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Locale

@Entity
@Table(name = "topic")
class Topic(
    @Id
    var id: String,
    val name: String,
) : Audit() {
    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var calendars: MutableList<TopicCalendar> = arrayListOf()
        protected set

    fun addBy(topicCalendar: TopicCalendar) {
        if (hasCalendar(topicCalendar.locale, topicCalendar.year)) {
            throw IllegalArgumentException("already exists topic calender")
        }

        calendars.add(topicCalendar)
        if (this != topicCalendar.topic) {
            topicCalendar.setBy(this)
        }
    }

    fun hasCalendar(locale: Locale, year: Int): Boolean {
        if (this.calendars.isEmpty()) {
            return false
        }

        val calendar = this.calendars.find { it.locale == locale && it.year == year }
        return calendar != null
    }

    fun getCalendar(locale: Locale, year: Int): TopicCalendar? {
        return this.calendars.find { it.locale == locale && it.year == year }
    }

    companion object
}
