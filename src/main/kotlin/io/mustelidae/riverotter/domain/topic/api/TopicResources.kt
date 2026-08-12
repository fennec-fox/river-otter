package io.mustelidae.riverotter.domain.topic.api

import io.mustelidae.riverotter.domain.topic.Topic
import io.mustelidae.riverotter.domain.topic.TopicWorkSchedule
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.time.LocalTime

@Schema(name = "Topic")
class TopicResources {

    @Schema(name = "Topic.Request")
    data class Request(
        val name: String,
        val code: String? = null,
    )

    @Schema(name = "Topic.ReqReply.WorkSchedule")
    data class WorkSchedule(
        val mon: Schedule,
        val tue: Schedule,
        val wed: Schedule,
        val thu: Schedule,
        val fri: Schedule,
        val sat: Schedule,
        val sun: Schedule,
    ) {
        @Schema(name = "Topic.ReqReply.WorkSchedule.Schedule")
        data class Schedule(
            val isOn: Boolean,
            val is24Hours: Boolean,
            val startTime: LocalTime? = null,
            val endTime: LocalTime? = null,
        )
    }

    @Schema(name = "Topic.Reply")
    data class Reply(
        val id: String,
        val name: String,
        val createdAt: LocalDateTime,
        val workSchedule: WorkSchedule? = null,
    ) {
        companion object {
            fun from(topic: Topic, workSchedule: TopicWorkSchedule? = null): Reply {
                return topic.run {
                    Reply(
                        id,
                        name,
                        createdAt!!,
                        workSchedule?.let {
                            WorkSchedule(
                                WorkSchedule.Schedule(
                                    it.mon.isOn,
                                    it.mon.is24Hours,
                                    it.mon.startTime,
                                    it.mon.endTime,
                                ),
                                WorkSchedule.Schedule(
                                    it.tue.isOn,
                                    it.tue.is24Hours,
                                    it.tue.startTime,
                                    it.tue.endTime,
                                ),
                                WorkSchedule.Schedule(
                                    it.wed.isOn,
                                    it.wed.is24Hours,
                                    it.wed.startTime,
                                    it.wed.endTime,
                                ),
                                WorkSchedule.Schedule(
                                    it.thu.isOn,
                                    it.thu.is24Hours,
                                    it.thu.startTime,
                                    it.thu.endTime,
                                ),
                                WorkSchedule.Schedule(
                                    it.fri.isOn,
                                    it.fri.is24Hours,
                                    it.fri.startTime,
                                    it.fri.endTime,
                                ),
                                WorkSchedule.Schedule(
                                    it.sat.isOn,
                                    it.sat.is24Hours,
                                    it.sat.startTime,
                                    it.sat.endTime,
                                ),
                                WorkSchedule.Schedule(
                                    it.sun.isOn,
                                    it.sun.is24Hours,
                                    it.sun.startTime,
                                    it.sun.endTime,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }

    @Schema(name = "Topic.Reply.WithCalendar")
    data class ReplyWithCalendar(
        val id: String,
        val name: String,
        val createdAt: LocalDateTime,
        val calendars: List<TopicCalendarResources.Reply>,
        val workSchedule: WorkSchedule? = null,
    ) {
        private constructor(reply: Reply, calendars: List<TopicCalendarResources.Reply>) : this (
            reply.id,
            reply.name,
            reply.createdAt,
            calendars,
            reply.workSchedule,
        )

        companion object {
            fun from(topic: Topic, workSchedule: TopicWorkSchedule? = null): ReplyWithCalendar {
                val reply = Reply.from(topic, workSchedule)
                val calendars = topic.calendars.map { TopicCalendarResources.Reply.from(it) }
                return ReplyWithCalendar(
                    reply,
                    calendars,
                )
            }
        }
    }
}
