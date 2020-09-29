/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
@Deprecated("Use Spring Security 5.4 or greater for a native Kotlin DSL.")
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