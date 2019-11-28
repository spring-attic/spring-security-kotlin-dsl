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

/**
 * A Kotlin DSL to configure the [HttpSecurity] HTTP Public Key Pinning header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @since 5.2
 * @property pins the value for the pin- directive of the Public-Key-Pins header.
 * @property maxAgeInSeconds the value (in seconds) for the max-age directive of the
 * Public-Key-Pins header.
 * @property includeSubDomains if true, the pinning policy applies to this pinned host
 * as well as any subdomains of the host's domain name.
 * @property reportOnly if true, the browser should not terminate the connection with
 * the server.
 * @property reportUri the URI to which the browser should report pin validation failures.
 */
class HttpPublicKeyPinningDsl(
        private val hpkpConfig: HeadersConfigurer<HttpSecurity>.HpkpConfig
) {
    var pins: Map<String, String>? by CallbackDelegates.callOnSet(hpkpConfig::withPins)
    var maxAgeInSeconds by CallbackDelegates.callOnSet(hpkpConfig::maxAgeInSeconds)
    var includeSubDomains by CallbackDelegates.callOnSet(hpkpConfig::includeSubDomains)
    var reportOnly by CallbackDelegates.callOnSet(hpkpConfig::reportOnly)
    var reportUri by CallbackDelegates.callOnSet<String>(hpkpConfig::reportUri)

    fun disable() {
        hpkpConfig.disable()
    }
}
