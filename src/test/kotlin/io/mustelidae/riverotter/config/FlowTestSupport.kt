package io.mustelidae.riverotter.config

import io.mustelidae.riverotter.RiverOtterApplication
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("embedded")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [RiverOtterApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
// Own H2 database, isolated from IntegrationSupport's context, so Flow tests can't leak mutations into it.
@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:flow;DB_CLOSE_ON_EXIT=FALSE;AUTOCOMMIT=OFF;MODE=MySQL;NON_KEYWORDS=USER,YEAR"])
class FlowTestSupport {

    @Autowired
    final lateinit var mockMvc: MockMvc
}
