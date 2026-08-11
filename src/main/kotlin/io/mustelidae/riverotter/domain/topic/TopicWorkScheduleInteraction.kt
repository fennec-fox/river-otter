package io.mustelidae.riverotter.domain.topic

import io.mustelidae.riverotter.config.DataNotFoundException
import io.mustelidae.riverotter.config.DuplicateDataException
import io.mustelidae.riverotter.domain.topic.api.TopicResources
import io.mustelidae.riverotter.domain.topic.repository.TopicWorkScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek

@Service
class TopicWorkScheduleInteraction(
    private val topicFinder: TopicFinder,
    private val topicWorkScheduleRepository: TopicWorkScheduleRepository,
) {

    @Transactional
    fun add(topicId: String, request: TopicResources.WorkSchedule) {
        val topic = topicFinder.findOrThrow(topicId)
        if (topicWorkScheduleRepository.findByTopicId(topicId) != null) {
            throw DuplicateDataException("The topic already has a schedule.")
        }

        val schedule = TopicWorkSchedule.from(topic, request)
        topicWorkScheduleRepository.save(schedule)
    }

    @Transactional
    fun modify(
        topicId: String,
        dayOfWeek: DayOfWeek,
        request: TopicResources.WorkSchedule.Schedule,
    ) {
        topicFinder.findOrThrow(topicId)
        val schedule = topicWorkScheduleRepository.findByTopicId(topicId)
            ?: throw DataNotFoundException("Please register your weekly schedule first.")

        val workTime = TopicWorkSchedule.WorkTime.from(request)

        when (dayOfWeek) {
            DayOfWeek.MONDAY -> schedule.mon = workTime
            DayOfWeek.TUESDAY -> schedule.tue = workTime
            DayOfWeek.WEDNESDAY -> schedule.wed = workTime
            DayOfWeek.THURSDAY -> schedule.thu = workTime
            DayOfWeek.FRIDAY -> schedule.fri = workTime
            DayOfWeek.SATURDAY -> schedule.sat = workTime
            DayOfWeek.SUNDAY -> schedule.sun = workTime
        }

        topicWorkScheduleRepository.save(schedule)
    }

    @Transactional
    fun remove(
        topicId: String,
    ) {
        topicFinder.findOrThrow(topicId)
        val schedule = topicWorkScheduleRepository.findByTopicId(topicId) ?: return
        topicWorkScheduleRepository.delete(schedule)
    }
}
