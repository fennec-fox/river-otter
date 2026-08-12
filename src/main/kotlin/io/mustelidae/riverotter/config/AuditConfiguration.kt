package io.mustelidae.riverotter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@Configuration
@EnableJpaAuditing
class AuditConfiguration {
    @Bean
    internal fun auditorAware(): AuditorAware<*> = AuditorAwareImpl()
}

class AuditorAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of("unknown")
    }
}
