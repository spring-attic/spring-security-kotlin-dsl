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

package org.springframework.security.dsl.config.builders.servlet.oauth2.resourceserver

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder

/**
 * A Kotlin DSL to configure JWT Resource Server Support using idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @property jwtAuthenticationConverter the [Converter] to use for converting a [Jwt] into
 * an [AbstractAuthenticationToken].
 * @property jwtDecoder the [JwtDecoder] to use.
 * @property jwkSetUri configures a [JwtDecoder] using a
 * <a target="_blank" href="https://tools.ietf.org/html/rfc7517">JSON Web Key (JWK)</a> URL
 */
@Deprecated("Use Spring Security 5.3 for a native servlet Kotlin DSL.")
class JwtDsl {
    var jwtAuthenticationConverter: Converter<Jwt, out AbstractAuthenticationToken>? = null
    var jwtDecoder: JwtDecoder? = null
    var jwkSetUri: String? = null

    internal fun get(): (OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer) -> Unit {
        return { jwt ->
            jwtAuthenticationConverter?.also { jwt.jwtAuthenticationConverter(jwtAuthenticationConverter) }
            jwtDecoder?.also { jwt.decoder(jwtDecoder) }
            jwkSetUri?.also { jwt.jwkSetUri(jwkSetUri) }
        }
    }
}
