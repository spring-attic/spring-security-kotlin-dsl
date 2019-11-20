package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] CSRF protection using idiomatic
 * Kotlin code.
 *
 * @author Eleftheria Stein
 * @property accessDeniedHandler the [ServerAccessDeniedHandler] used when a CSRF token is invalid.
 * @property csrfTokenRepository the [ServerCsrfTokenRepository] used to persist the CSRF token.
 * @property requireCsrfProtectionMatcher the [ServerWebExchangeMatcher] used to determine when CSRF protection
 * is enabled.
 */
class ServerCsrfDsl {

    var accessDeniedHandler: ServerAccessDeniedHandler? = null
    var csrfTokenRepository: ServerCsrfTokenRepository? = null
    var requireCsrfProtectionMatcher: ServerWebExchangeMatcher? = null

    private var disabled = false

    /**
     * Disables CSRF protection
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.CsrfSpec) -> Unit {
        return { csrf ->
            accessDeniedHandler?.also { csrf.accessDeniedHandler(accessDeniedHandler) }
            csrfTokenRepository?.also { csrf.csrfTokenRepository(csrfTokenRepository) }
            requireCsrfProtectionMatcher?.also { csrf.requireCsrfProtectionMatcher(requireCsrfProtectionMatcher) }
            if (disabled) {
                csrf.disable()
            }
        }
    }
}