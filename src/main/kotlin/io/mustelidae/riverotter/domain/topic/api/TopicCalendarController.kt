package io.mustelidae.riverotter.domain.topic.api

import io.mustelidae.riverotter.common.AvailableCountry
import io.mustelidae.riverotter.domain.topic.TopicInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "Topic calendar", description = "Topic custom calendar")
@RestController
@RequestMapping("topics/{id}")
class TopicCalendarController(
    private val topicInteraction: TopicInteraction,
) {

    @Operation(summary = "Add holidays to calendar.")
    @PostMapping("holidays/{country}")
    fun addHoliday(
        @PathVariable id: String,
        @PathVariable country: String,
        @RequestBody request: TopicCalendarResources.Request.TopicHoliday,
    ) {
        val locale = AvailableCountry.getLocale(country)
        request.run {
            topicInteraction.addHoliday(id, locale, date, name, description)
        }
    }

    @Operation(summary = "Remove holidays to calendar.")
    @DeleteMapping("holidays/{country}/dates/{date}")
    fun removeHoliday(
        @PathVariable id: String,
        @PathVariable country: String,
        @PathVariable date: LocalDate,
    ) {
        val locale = AvailableCountry.getLocale(country)
        topicInteraction.removeHoliday(id, locale, date)
    }

    @Operation(summary = "Add workdays to calendar.")
    @PostMapping("workdays/{country}")
    fun addWorkday(
        @PathVariable id: String,
        @PathVariable country: String,
        @RequestBody request: TopicCalendarResources.Request.TopicWorkday,
    ) {
        val locale = AvailableCountry.getLocale(country)
        request.run {
            topicInteraction.addWorkday(id, locale, date, name, type, description)
        }
    }

    @Operation(summary = "Remove workdays to calendar.")
    @DeleteMapping("workdays/{country}/dates/{date}")
    fun removeWorkday(
        @PathVariable id: String,
        @PathVariable country: String,
        @PathVariable date: LocalDate,
    ) {
        val locale = AvailableCountry.getLocale(country)
        topicInteraction.removeWorkday(id, locale, date)
    }
}
