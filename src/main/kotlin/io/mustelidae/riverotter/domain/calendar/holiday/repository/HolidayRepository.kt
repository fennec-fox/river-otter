package io.mustelidae.riverotter.domain.calendar.holiday.repository

import io.mustelidae.riverotter.domain.calendar.holiday.Holiday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HolidayRepository : JpaRepository<Holiday, Long>
