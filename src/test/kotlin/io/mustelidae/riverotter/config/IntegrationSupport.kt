package io.mustelidae.riverotter.config

import io.mustelidae.riverotter.RiverOtterApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("embedded")
@ComponentScan(excludeFilters = [ComponentScan.Filter(classes = [SwaggerConfiguration::class])])
@SpringBootTest(classes = [RiverOtterApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [MySqlContainerConfig::class])
class IntegrationSupport
