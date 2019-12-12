package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] logout support using idiomatic Kotlin
 * code.
 *
 * @author Eleftheria Stein
 * @property logoutHandler a [ServerLogoutHandler] that is invoked when logout occurs.
 * @property logoutUrl the URL that triggers logout to occur.
 * @property requiresLogout the [ServerWebExchangeMatcher] that triggers logout to occur.
 * @property logoutSuccessHandler the [ServerLogoutSuccessHandler] to use after logout has
 * occurred.
 */
class ServerLogoutDsl {
    var logoutHandler: ServerLogoutHandler? = null
    var logoutUrl: String? = null
    var requiresLogout: ServerWebExchangeMatcher? = null
    var logoutSuccessHandler: ServerLogoutSuccessHandler? = null

    private var disabled = false

    /**
     * Disables logout
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.LogoutSpec) -> Unit {
        return { logout ->
            logoutHandler?.also { logout.logoutHandler(logoutHandler) }
            logoutUrl?.also { logout.logoutUrl(logoutUrl) }
            requiresLogout?.also { logout.requiresLogout(requiresLogout) }
            logoutSuccessHandler?.also { logout.logoutSuccessHandler(logoutSuccessHandler) }
            if (disabled) {
                logout.disable()
            }
        }
    }
}