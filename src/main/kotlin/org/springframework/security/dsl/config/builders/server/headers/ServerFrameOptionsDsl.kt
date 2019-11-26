package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] X-Frame-Options header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @property mode the X-Frame-Options mode to set in the response header.
 */
class ServerFrameOptionsDsl {
    var mode: XFrameOptionsServerHttpHeadersWriter.Mode? = null

    private var disabled = false

    /**
     * Disables the X-Frame-Options response header
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.HeaderSpec.FrameOptionsSpec) -> Unit {
        return { frameOptions ->
            mode?.also {
                frameOptions.mode(mode)
            }
            if (disabled) {
                frameOptions.disable()
            }
        }
    }
}
