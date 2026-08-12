package io.mustelidae.riverotter.domain.topic

import java.util.UUID

internal class TopicTest

fun Topic.Companion.aFixture(): Topic {
    return Topic(UUID.randomUUID().toString(), "Local test topic")
}
