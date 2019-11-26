package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import java.time.Duration

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] HTTP Strict Transport Security
 * header using idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @property maxAge he value for the max-age directive of the Strict-Transport-Security
 * header.
 * @property includeSubdomains if true, subdomains should be considered HSTS Hosts too.
 * @property preload if true, preload will be included in HSTS Header.
 */
class ServerHttpStrictTransportSecurityDsl {
    var maxAge: Duration? = null
    var includeSubdomains: Boolean? = null
    var preload: Boolean? = null

    private var disabled = false

    /**
     * Disables the X-Frame-Options response header
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.HeaderSpec.HstsSpec) -> Unit {
        return { hsts ->
            maxAge?.also { hsts.maxAge(maxAge) }
            includeSubdomains?.also { hsts.includeSubdomains(includeSubdomains!!) }
            preload?.also { hsts.preload(preload!!) }
            if (disabled) {
                hsts.disable()
            }
        }
    }
}
