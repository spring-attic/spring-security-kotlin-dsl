package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] Content-Security-Policy header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 */
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
