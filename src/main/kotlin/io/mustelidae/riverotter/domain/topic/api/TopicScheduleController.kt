package io.mustelidae.riverotter.domain.topic.api

import io.mustelidae.riverotter.common.Reply
import io.mustelidae.riverotter.domain.topic.TopicWorkScheduleInteraction
import io.mustelidae.riverotter.utils.toReply
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek

@Tag(name = "Topic calendar", description = "Topic custom calendar")
@RestController
@RequestMapping("topics/{id}")
class TopicScheduleController(
    private val topicWorkScheduleInteraction: TopicWorkScheduleInteraction,
) {

    @Operation(summary = "Add work schedule to topic")
    @PostMapping("work-schedule")
    fun add(
        @PathVariable id: String,
        @RequestBody request: TopicResources.WorkSchedule,
    ): Reply<Unit> {
        topicWorkScheduleInteraction.add(id, request)
        return Unit.toReply()
    }

    @Operation(summary = "Add work schedule to topic")
    @PutMapping("work-schedule/{dayOfWeek}")
    fun modify(
        @PathVariable id: String,
        @Parameter(example = "ex) MONDAY, TUESDAY, WEDNESDAY ...")
        @PathVariable
        dayOfWeek: String,
        @RequestBody request: TopicResources.WorkSchedule.Schedule,
    ): Reply<Unit> {
        val dow = DayOfWeek.valueOf(dayOfWeek.uppercase())

        topicWorkScheduleInteraction.modify(id, dow, request)
        return Unit.toReply()
    }

    @Operation(summary = "Remove work schedule to topic")
    @DeleteMapping("work-schedule")
    fun remove(
        @PathVariable id: String,
    ): Reply<Unit> {
        topicWorkScheduleInteraction.remove(id)
        return Unit.toReply()
    }
}
