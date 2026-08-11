package io.mustelidae.riverotter.domain.topic.repository

import io.mustelidae.riverotter.domain.topic.Workday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkdayRepository : JpaRepository<Workday, Long>
