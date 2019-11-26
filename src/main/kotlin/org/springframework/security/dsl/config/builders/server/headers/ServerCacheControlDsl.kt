package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] cache control headers using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 */
class ServerCacheControlDsl {
    private var disabled = false

    /**
     * Disables cache control response headers
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.HeaderSpec.CacheSpec) -> Unit {
        return { cacheControl ->
            if (disabled) {
                cacheControl.disable()
            }
        }
    }
}
