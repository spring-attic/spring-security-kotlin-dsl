package org.springframework.security.dsl.config.builders.server.headers

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter

/**
 * A Kotlin DSL to configure the [ServerHttpSecurity] referrer policy header using
 * idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @property policy the policy to be used in the response header.
 */
class ServerReferrerPolicyDsl {
    var policy: ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy? = null

    internal fun get(): (ServerHttpSecurity.HeaderSpec.ReferrerPolicySpec) -> Unit {
        return { referrerPolicy ->
            policy?.also {
                referrerPolicy.policy(policy)
            }
        }
    }
}
