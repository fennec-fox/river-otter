package io.mustelidae.riverotter.domain.topic.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.riverotter.common.AvailableCountry
import io.mustelidae.riverotter.config.IntegrationSupport
import io.mustelidae.riverotter.domain.topic.Topic
import io.mustelidae.riverotter.domain.topic.Workday
import io.mustelidae.riverotter.domain.topic.aFixture
import io.mustelidae.riverotter.domain.topic.repository.TopicCalendarDSLRepository
import io.mustelidae.riverotter.domain.topic.repository.TopicRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TopicCalendarControllerTest : IntegrationSupport() {

    @Autowired
    private lateinit var topicCalendarController: TopicCalendarController

    @Autowired
    private lateinit var topicRepository: TopicRepository

    @Autowired
    private lateinit var topicCalendarDSLRepository: TopicCalendarDSLRepository

    private val country = "KR"
    private val year = 2020
    private val locale = AvailableCountry.getLocale(country)
    private lateinit var topicId: String
    private lateinit var modifiedAtAfterCreate: LocalDateTime

    @BeforeAll()
    fun beforeAll() {
        val topic = Topic.aFixture()
        topicRepository.save(topic)
        topicId = topic.id
    }

    @Test
    @Order(1)
    fun addHoliday() {
        // Given
        val request = TopicCalendarResources.Request.TopicHoliday(
            LocalDate.of(year, 1, 1),
            "fennec is free!",
        )

        // When
        topicCalendarController.addHoliday(topicId, country, request)

        // Then
        val calendar = topicCalendarDSLRepository.findWithHolidaysByTopicIdAndLocaleAndYear(topicId, locale, year)!!
        calendar.holidays.size shouldBe 1
        calendar.holidays.first().date shouldBe request.date
        calendar.createdAt shouldNotBe null
        calendar.modifiedAt shouldNotBe null
        calendar.auditor shouldBe "unknown"
        modifiedAtAfterCreate = calendar.modifiedAt!!
    }

    @Test
    @Order(2)
    fun removeHoliday() {
        val date = LocalDate.of(year, 1, 1)
        topicCalendarController.removeHoliday(topicId, country, date)

        // Then
        // holidays/workdays live on the mappedBy (child) side, so mutating them never
        // dirties the topic_calendar row itself, and modifiedAt must stay unchanged.
        val calendar = topicCalendarDSLRepository.findWithHolidaysByTopicIdAndLocaleAndYear(topicId, locale, year)!!
        calendar.holidays.size shouldBe 0
        calendar.modifiedAt shouldBe modifiedAtAfterCreate
    }

    @Test
    @Order(3)
    fun addWorkday() {
        // Given
        val request = TopicCalendarResources.Request.TopicWorkday(
            LocalDate.of(year, 1, 1),
            "fennec is free!",
            Workday.Type.FORCE_WORK,
        )
        // When
        topicCalendarController.addWorkday(topicId, country, request)
    }

    @Test
    @Order(4)
    fun removeWorkday() {
        val date = LocalDate.of(year, 1, 1)
        topicCalendarController.removeWorkday(topicId, country, date)

        // Then
        val calendar = topicCalendarDSLRepository.findWithWorkdaysByTopicIdAndLocaleAndYear(topicId, locale, year)!!
        calendar.workdays.size shouldBe 0
    }
}
