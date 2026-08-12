package io.mustelidae.riverotter.domain.topic

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Locale

internal class TopicCalendarTest {

    @Test
    fun addBy() {
        // Given
        val topicCalendar = TopicCalendar.aFixture()
        val holiday = TopicHoliday(LocalDate.of(2020, 10, 6), "test")
        // When
        topicCalendar.addBy(holiday)
        // Then
        topicCalendar.holidays.size shouldBe 2

        assertThrows(IllegalArgumentException::class.java) {
            topicCalendar.addBy(holiday)
        }
    }

    @Test
    fun testAddBy() {
        // Given
        val topicCalendar = TopicCalendar.aFixture()
        val workDay = Workday(LocalDate.of(2020, 10, 3), "test work", Workday.Type.REPLACED_WORK)
        // When
        topicCalendar.addBy(workDay)
        // Then
        topicCalendar.workdays.size shouldBe 2
        assertThrows(IllegalArgumentException::class.java) {
            topicCalendar.addBy(workDay)
        }
    }

    @Test
    fun removeByHoliday() {
        // Given
        val topicCalendar = TopicCalendar.aFixture()
        val date = topicCalendar.holidays.first().date

        // When
        topicCalendar.removeHoliday(date)

        // Then
        topicCalendar.holidays.size shouldBe 0
    }

    @Test
    fun removeByWorkDay() {
        // Given
        val topicCalendar = TopicCalendar.aFixture()
        val date = topicCalendar.workdays.first().date

        // When
        topicCalendar.removeWorkDay(date)

        // Then
        topicCalendar.workdays.size shouldBe 0
    }
}

internal fun TopicCalendar.Companion.aFixture(
    locale: Locale = Locale.KOREA,
): TopicCalendar {
    return TopicCalendar(
        locale,
        2020,
    ).apply {
        addBy(
            TopicHoliday(
                LocalDate.of(2020, 9, 29),
                "vacation",
            ),
        )

        addBy(
            Workday(
                LocalDate.of(2020, 10, 1),
                "replaced vacation",
                Workday.Type.REPLACED_WORK,
            ),
        )
    }
}
