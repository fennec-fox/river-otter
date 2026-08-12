package io.mustelidae.riverotter.config

import io.mustelidae.riverotter.RiverOtterApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@ActiveProfiles("embedded")
@ComponentScan(excludeFilters = [ComponentScan.Filter(classes = [SwaggerConfiguration::class])])
@SpringBootTest(classes = [RiverOtterApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Own H2 database, isolated from FlowTestSupport's context, so tests here can't leak mutations into it.
@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:integration;DB_CLOSE_ON_EXIT=FALSE;AUTOCOMMIT=OFF;MODE=MySQL;NON_KEYWORDS=USER,YEAR"])
class IntegrationSupport
