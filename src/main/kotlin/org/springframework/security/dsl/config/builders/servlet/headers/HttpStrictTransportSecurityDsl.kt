/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.security.dsl.config.builders.servlet.headers

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.dsl.config.builders.util.delegates.CallbackDelegates
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * A Kotlin DSL to configure the [HttpSecurity] HTTP Strict Transport Security header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @since 5.2
 * @property maxAgeInSeconds the value (in seconds) for the max-age directive of the
 * Strict-Transport-Security header.
 * @property requestMatcher the [RequestMatcher] used to determine if the
 * "Strict-Transport-Security" header should be added. If true the header is added,
 * else the header is not added.
 * @property includeSubDomains if true, subdomains should be considered HSTS Hosts too.
 * @property preload if true, preload will be included in HSTS Header.
 */
class HttpStrictTransportSecurityDsl(
        private val hstsConfig: HeadersConfigurer<HttpSecurity>.HstsConfig
) {
    var maxAgeInSeconds by CallbackDelegates.callOnSet(hstsConfig::maxAgeInSeconds)
    var requestMatcher by CallbackDelegates.callOnSet<RequestMatcher>(hstsConfig::requestMatcher)
    var includeSubDomains by CallbackDelegates.callOnSet(hstsConfig::includeSubDomains)
    var preload by CallbackDelegates.callOnSet(hstsConfig::preload)

    fun disable() {
        hstsConfig.disable()
    }
}
