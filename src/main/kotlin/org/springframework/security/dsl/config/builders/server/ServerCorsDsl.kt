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
import org.springframework.web.cors.reactive.CorsConfigurationSource

/**
 * A Kotlin DSL to configure [ServerHttpSecurity] CORS headers using idiomatic
 * Kotlin code.
 *
 * @author Eleftheria Stein
 * @property configurationSource the [CorsConfigurationSource] to use.
 */
@Deprecated("Use Spring Security 5.4 or greater for a native Kotlin DSL.")
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