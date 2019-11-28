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
 * A Kotlin DSL to configure the [HttpSecurity] XSS protection header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @since 5.2
 * @property block whether to specify the mode as blocked
 * @property xssProtectionEnabled if true, the header value will contain a value of 1.
 * If false, will explicitly disable specify that X-XSS-Protection is disabled.
 */
class XssProtectionConfigDsl(
        private val xXssConfig: HeadersConfigurer<HttpSecurity>.XXssConfig
) {
    var block by CallbackDelegates.callOnSet(xXssConfig::block)
    var xssProtectionEnabled by CallbackDelegates.callOnSet(xXssConfig::xssProtectionEnabled)

    fun disable() {
        xXssConfig.disable()
    }
}
