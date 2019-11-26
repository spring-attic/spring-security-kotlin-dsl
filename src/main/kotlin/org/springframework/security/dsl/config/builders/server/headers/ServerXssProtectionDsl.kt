package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] XSS protection header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 */
class ServerXssProtectionDsl {
    private var disabled = false

    /**
     * Disables cache control response headers
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.HeaderSpec.XssProtectionSpec) -> Unit {
        return { xss ->
            if (disabled) {
                xss.disable()
            }
        }
    }
}
