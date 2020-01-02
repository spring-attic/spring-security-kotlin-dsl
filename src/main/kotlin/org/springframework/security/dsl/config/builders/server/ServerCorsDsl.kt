package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.web.cors.reactive.CorsConfigurationSource

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] CORS headers using idiomatic
 * Kotlin code.
 *
 * @author Eleftheria Stein
 * @property configurationSource the [CorsConfigurationSource] to use.
 */
class ServerCorsDsl {
    var configurationSource: CorsConfigurationSource? = null

    private var disabled = false

    /**
     * Disables CORS support within Spring Security.
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.CorsSpec) -> Unit {
        return { cors ->
            configurationSource?.also { cors.configurationSource(configurationSource) }
            if (disabled) {
                cors.disable()
            }
        }
    }
}