package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] exception handling using idiomatic Kotlin
 * code.
 *
 * @author Eleftheria Stein
 * @property authenticationEntryPoint the [ServerAuthenticationEntryPoint] to use when
 * the application request authentication
 * @property accessDeniedHandler the [ServerAccessDeniedHandler] to use when an
 * authenticated user does not hold a required authority
 */
class ServerExceptionHandlingDsl {
    var authenticationEntryPoint: ServerAuthenticationEntryPoint? = null
    var accessDeniedHandler: ServerAccessDeniedHandler? = null

    internal fun get(): (ServerHttpSecurity.ExceptionHandlingSpec) -> Unit {
        return { exceptionHandling ->
            authenticationEntryPoint?.also { exceptionHandling.authenticationEntryPoint(authenticationEntryPoint) }
            accessDeniedHandler?.also { exceptionHandling.accessDeniedHandler(accessDeniedHandler) }
        }
    }
}