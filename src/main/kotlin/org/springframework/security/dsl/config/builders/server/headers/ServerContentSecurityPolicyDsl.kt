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

package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] Content-Security-Policy header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 */
@Deprecated("Use Spring Security 5.4 or greater for a native Kotlin DSL.")
class ServerContentSecurityPolicyDsl {
    var policyDirectives: String? = null
    var reportOnly: Boolean? = null

    internal fun get(): (ServerHttpSecurity.HeaderSpec.ContentSecurityPolicySpec) -> Unit {
        return { contentSecurityPolicy ->
            policyDirectives?.also {
                contentSecurityPolicy.policyDirectives(policyDirectives)
            }
            reportOnly?.also {
                contentSecurityPolicy.reportOnly(reportOnly!!)
            }
        }
    }
}
