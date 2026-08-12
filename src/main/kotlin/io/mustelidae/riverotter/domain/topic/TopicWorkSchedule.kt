package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.domain.topic.api.TopicResources
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalTime

@Entity
@Table(name = "topic_work_schedule")
class TopicWorkSchedule(
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "mon_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "mon_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "mon_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "mon_end_time")),
    )
    var mon: WorkTime,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "tue_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "tue_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "tue_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "tue_end_time")),
    )
    var tue: WorkTime,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "wed_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "wed_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "wed_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "wed_end_time")),
    )
    var wed: WorkTime,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "thu_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "thu_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "thu_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "thu_end_time")),
    )
    var thu: WorkTime,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "fri_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "fri_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "fri_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "fri_end_time")),
    )
    var fri: WorkTime,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "sat_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "sat_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "sat_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "sat_end_time")),
    )
    var sat: WorkTime,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "isOn", column = Column(name = "sun_is_on")),
        AttributeOverride(name = "is24Hours", column = Column(name = "sun_is_24h")),
        AttributeOverride(name = "startTime", column = Column(name = "sun_start_time")),
        AttributeOverride(name = "endTime", column = Column(name = "sun_end_time")),
    )
    var sun: WorkTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @OneToOne
    @JoinColumn(name = "topicId")
    var topic: Topic? = null
        protected set

    fun setBy(topic: Topic) {
        this.topic = topic
    }

    @Embeddable
    data class WorkTime(
        val isOn: Boolean,
        val is24Hours: Boolean,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null,
    ) {
        companion object {
            fun from(request: TopicResources.WorkSchedule.Schedule): WorkTime {
                return if (request.isOn.not()) {
                    WorkTime(isOn = false, is24Hours = false)
                } else {
                    WorkTime(
                        true,
                        request.is24Hours,
                        request.startTime,
                        request.endTime,
                    )
                }
            }
        }
    }

    companion object {
        fun from(topic: Topic, request: TopicResources.WorkSchedule): TopicWorkSchedule {
            val schedule = request.run {
                TopicWorkSchedule(
                    WorkTime.from(mon),
                    WorkTime.from(tue),
                    WorkTime.from(wed),
                    WorkTime.from(thu),
                    WorkTime.from(fri),
                    WorkTime.from(sat),
                    WorkTime.from(sun),
                )
            }
            schedule.setBy(topic)
            return schedule
        }
    }
}
