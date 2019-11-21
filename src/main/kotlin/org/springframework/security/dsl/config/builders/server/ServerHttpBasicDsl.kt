package org.springframework.security.dsl.config.builders.server

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.context.ReactorContextWebFilter
import org.springframework.security.web.server.context.ServerSecurityContextRepository

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] basic authorization using idiomatic
 * Kotlin code.
 *
 * @author Eleftheria Stein
 * @property authenticationManager the [ReactiveAuthenticationManager] used to authenticate.
 * @property securityContextRepository the [ServerSecurityContextRepository] used to save
 * the [Authentication]. For the [SecurityContext] to be loaded on subsequent requests the
 * [ReactorContextWebFilter] must be configured to be able to load the value (they are not
 * implicitly linked).
 * @property authenticationEntryPoint the [ServerAuthenticationEntryPoint] to be
 * populated on [BasicAuthenticationFilter] in the event that authentication fails.
 */
class ServerHttpBasicDsl {
    var authenticationManager: ReactiveAuthenticationManager? = null
    var securityContextRepository: ServerSecurityContextRepository? = null
    var authenticationEntryPoint: ServerAuthenticationEntryPoint? = null

    private var disabled = false

    /**
     * Disables HTTP basic authentication
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.HttpBasicSpec) -> Unit {
        return { httpBasic ->
            authenticationManager?.also { httpBasic.authenticationManager(authenticationManager) }
            securityContextRepository?.also { httpBasic.securityContextRepository(securityContextRepository) }
            authenticationEntryPoint?.also { httpBasic.authenticationEntryPoint(authenticationEntryPoint) }
            if (disabled) {
                httpBasic.disable()
            }
        }
    }
}