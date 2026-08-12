package io.mustelidae.riverotter.domain.client.korea.government

import io.kotest.matchers.shouldBe
import io.mustelidae.riverotter.config.AppEnvironment
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("실제 API 호출 테스트")
internal class GovernmentOpenStableClientTest {

    private val env = AppEnvironment.Client.Government().apply {
        host = "http://apis.data.go.kr"
        key = "lpQXHxRKaKapgLrvvnx9%2BuZHMW4xD8Nm%2Fnr1Nr9My%2F4V9g7tJe1yikoJyLq1KpzgQXA1dNir5XTVNRsbdp6KSg%3D%3D"
        timeout = 5000
    }

    private val governmentOpenClient = GovernmentOpenStableClient(env)

    @Test
    fun findAllHoliday() {
        // Given
        val year = 2020

        // When
        val holiday = governmentOpenClient.findAllHoliday(year)

        // Then
        holiday.response.header.resultCode shouldBe "00"
    }
}
