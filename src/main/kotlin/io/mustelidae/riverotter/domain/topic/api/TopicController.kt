package io.mustelidae.riverotter.domain.topic.api

import io.mustelidae.riverotter.common.Replies
import io.mustelidae.riverotter.common.Reply
import io.mustelidae.riverotter.domain.topic.TopicFinder
import io.mustelidae.riverotter.domain.topic.TopicInteraction
import io.mustelidae.riverotter.domain.topic.TopicWorkScheduleFinder
import io.mustelidae.riverotter.utils.toReplies
import io.mustelidae.riverotter.utils.toReply
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Topic", description = "Topic registration")
@RestController
@RequestMapping("topics")
class TopicController(
    private val topicInteraction: TopicInteraction,
    private val topicFinder: TopicFinder,
    private val topicWorkScheduleFinder: TopicWorkScheduleFinder,
) {

    @Operation(summary = "Add topic")
    @PostMapping
    fun add(@RequestBody request: TopicResources.Request): Reply<String> {
        return topicInteraction.addBy(request.name, request.code)
            .toReply()
    }

    @Operation(summary = "Remove topic")
    @DeleteMapping("{id}")
    fun remove(
        @PathVariable id: String,
    ) {
        topicInteraction.remove(id)
    }

    @Operation(summary = "Find all topic")
    @GetMapping
    fun findAll(): Replies<TopicResources.Reply> {
        return topicFinder.findAll()
            .map { TopicResources.Reply.from(it, topicWorkScheduleFinder.find(it.id)) }
            .toReplies()
    }

    @Operation(summary = "Find topic")
    @GetMapping("{id}")
    fun find(
        @PathVariable id: String,
    ): Reply<TopicResources.ReplyWithCalendar> {
        val topic = topicFinder.findOrThrow(id)
        val workSchedule = topicWorkScheduleFinder.find(id)
        return TopicResources.ReplyWithCalendar.from(topic, workSchedule)
            .toReply()
    }
}
