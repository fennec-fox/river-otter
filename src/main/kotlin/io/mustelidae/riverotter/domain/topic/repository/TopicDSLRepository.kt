package io.mustelidae.riverotter.domain.topic.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.mustelidae.riverotter.domain.topic.QTopic.topic
import io.mustelidae.riverotter.domain.topic.QTopicCalendar.topicCalendar
import io.mustelidae.riverotter.domain.topic.Topic
import org.springframework.stereotype.Repository

@Repository
class TopicDSLRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findWithCalendarsById(id: String): Topic? =
        queryFactory
            .selectFrom(topic)
            .leftJoin(topic.calendars, topicCalendar).fetchJoin()
            .where(topic.id.eq(id))
            .fetchOne()
}
