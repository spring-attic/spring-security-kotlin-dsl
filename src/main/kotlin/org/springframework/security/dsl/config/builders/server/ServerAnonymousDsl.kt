package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.server.authentication.AnonymousAuthenticationWebFilter

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] anonymous authentication using idiomatic
 * Kotlin code.
 *
 * @author Eleftheria Stein
 * @property key the key to identify tokens created for anonymous authentication
 * @property principal the principal for [Authentication] objects of anonymous users
 * @property authorities the [Authentication.getAuthorities] for anonymous users
 * @property authenticationFilter the [AnonymousAuthenticationWebFilter] used to populate
 * an anonymous user.
 */
class ServerAnonymousDsl {
    var key: String? = null
    var principal: Any? = null
    var authorities: List<GrantedAuthority>? = null
    var authenticationFilter: AnonymousAuthenticationWebFilter? = null

    private var disabled = false

    /**
     * Disables anonymous authentication
     */
    fun disable() {
        disabled = true
    }

    internal fun get(): (ServerHttpSecurity.AnonymousSpec) -> Unit {
        return { anonymous ->
            key?.also { anonymous.key(key) }
            principal?.also { anonymous.principal(principal) }
            authorities?.also { anonymous.authorities(authorities) }
            authenticationFilter?.also { anonymous.authenticationFilter(authenticationFilter) }
            if (disabled) {
                anonymous.disable()
            }
        }
    }
}