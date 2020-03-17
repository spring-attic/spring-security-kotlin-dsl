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

package org.springframework.security.dsl.config.builders.servlet.oauth2.login

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer

/**
 * A Kotlin DSL to configure the Authorization Server's Redirection Endpoint using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @property baseUri the URI where the authorization response will be processed.
 */
@Deprecated("Use Spring Security 5.3 for a native servlet Kotlin DSL.")
class RedirectionEndpointDsl {
    var baseUri: String? = null

    internal fun get(): (OAuth2LoginConfigurer<HttpSecurity>.RedirectionEndpointConfig) -> Unit {
        return { redirectionEndpoint ->
            baseUri?.also { redirectionEndpoint.baseUri(baseUri) }
        }
    }
}
