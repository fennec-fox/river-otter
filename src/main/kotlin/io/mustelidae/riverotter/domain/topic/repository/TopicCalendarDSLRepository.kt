package io.mustelidae.riverotter.domain.topic.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.mustelidae.riverotter.domain.topic.QTopic.topic
import io.mustelidae.riverotter.domain.topic.QTopicCalendar.topicCalendar
import io.mustelidae.riverotter.domain.topic.QTopicHoliday.topicHoliday
import io.mustelidae.riverotter.domain.topic.QWorkday.workday
import io.mustelidae.riverotter.domain.topic.TopicCalendar
import org.springframework.stereotype.Repository
import java.util.Locale

@Repository
class TopicCalendarDSLRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findWithHolidaysByTopicIdAndLocaleAndYear(topicId: String, locale: Locale, year: Int): TopicCalendar? =
        queryFactory
            .selectFrom(topicCalendar)
            .join(topicCalendar.topic, topic).fetchJoin()
            .leftJoin(topicCalendar.holidays, topicHoliday).fetchJoin()
            .where(
                topic.id.eq(topicId),
                topicCalendar.locale.eq(locale),
                topicCalendar.year.eq(year),
            )
            .fetchOne()

    fun findWithWorkdaysByTopicIdAndLocaleAndYear(topicId: String, locale: Locale, year: Int): TopicCalendar? =
        queryFactory
            .selectFrom(topicCalendar)
            .join(topicCalendar.topic, topic).fetchJoin()
            .leftJoin(topicCalendar.workdays, workday).fetchJoin()
            .where(
                topic.id.eq(topicId),
                topicCalendar.locale.eq(locale),
                topicCalendar.year.eq(year),
            )
            .fetchOne()
}
