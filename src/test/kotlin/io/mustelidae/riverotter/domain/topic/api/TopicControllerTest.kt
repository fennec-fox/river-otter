package io.mustelidae.riverotter.domain.topic.api

import io.kotest.matchers.shouldBe
import io.mustelidae.riverotter.config.IntegrationSupport
import io.mustelidae.riverotter.domain.topic.repository.TopicRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

internal class TopicControllerTest : IntegrationSupport() {

    @Autowired
    private lateinit var topicController: TopicController

    @Autowired
    private lateinit var topicRepository: TopicRepository

    @Test
    fun add() {
        // Given
        val request = TopicResources.Request(
            "새로운 토픽",
        )

        // When
        val id = topicController.add(request).content!!

        // Then
        val savedTopic = topicRepository.findByIdOrNull(id)!!

        request.name shouldBe savedTopic.name
    }

    @Test
    fun remove() {
        // Given
        val request = TopicResources.Request(
            "삭제 토픽",
        )
        val removeId = topicController.add(request).content!!

        // When
        topicController.remove(removeId)

        // Then
        val removedTopic = topicRepository.findByIdOrNull(removeId)

        removedTopic shouldBe null
    }
}
