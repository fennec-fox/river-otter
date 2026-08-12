package io.mustelidae.riverotter.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
open class Audit {
    @CreatedBy
    @LastModifiedBy
    @Column(length = 120)
    var auditor: String? = null

    @CreatedDate
    @Column(nullable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    var modifiedAt: LocalDateTime? = null
        protected set
}
