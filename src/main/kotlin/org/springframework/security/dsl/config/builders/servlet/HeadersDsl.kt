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

package org.springframework.security.dsl.config.builders.servlet

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.dsl.config.builders.servlet.headers.*
import org.springframework.security.dsl.config.builders.util.delegates.CallbackDelegates
import org.springframework.security.web.header.writers.*
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter

/**
 * A Kotlin DSL to configure [HttpSecurity] headers using idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @since 5.2
 * @property defaultsDisabled whether all of the default headers should be included in the response
 */
class HeadersDsl(
        private val headersConfigurer: HeadersConfigurer<HttpSecurity>
) {

    var defaultsDisabled by CallbackDelegates.callOnSet(headersConfigurer::defaultsDisabled)

    /**
     * Configures the [XContentTypeOptionsHeaderWriter] which inserts the <a href=
     * "https://msdn.microsoft.com/en-us/library/ie/gg622941(v=vs.85).aspx"
     * >X-Content-Type-Options header</a>
     *
     * @param contentTypeOptionsConfig the customization to apply to the header
     */
    fun contentTypeOptions(contentTypeOptionsConfig: ContentTypeOptionsDsl.() -> Unit) {
        ContentTypeOptionsDsl(headersConfigurer.contentTypeOptions()).apply(contentTypeOptionsConfig)
    }

    /**
     * <strong>Note this is not comprehensive XSS protection!</strong>
     *
     * <p>
     * Allows customizing the [XXssProtectionHeaderWriter] which adds the <a href=
     * "https://blogs.msdn.com/b/ieinternals/archive/2011/01/31/controlling-the-internet-explorer-xss-filter-with-the-x-xss-protection-http-header.aspx"
     * >X-XSS-Protection header</a>
     * </p>
     *
     * @param xssProtectionConfig the customization to apply to the header
     */
    fun xssProtection(xssProtectionConfig: XssProtectionConfigDsl.() -> Unit) {
        XssProtectionConfigDsl(headersConfigurer.xssProtection()).apply(xssProtectionConfig)
    }

    /**
     * Allows customizing the [CacheControlHeadersWriter]. Specifically it adds the
     * following headers:
     * <ul>
     * <li>Cache-Control: no-cache, no-store, max-age=0, must-revalidate</li>
     * <li>Pragma: no-cache</li>
     * <li>Expires: 0</li>
     * </ul>
     *
     * @param cacheControlConfig the customization to apply to the header
     */
    fun cacheControl(cacheControlConfig: CacheControlDsl.() -> Unit) {
        CacheControlDsl(headersConfigurer.cacheControl()).apply(cacheControlConfig)
    }

    /**
     * Allows customizing the [HstsHeaderWriter] which provides support for <a
     * href="https://tools.ietf.org/html/rfc6797">HTTP Strict Transport Security
     * (HSTS)</a>.
     *
     * @param hstsConfig the customization to apply to the header
     */
    fun httpStrictTransportSecurity(hstsConfig: HttpStrictTransportSecurityDsl.() -> Unit) {
        HttpStrictTransportSecurityDsl(headersConfigurer.httpStrictTransportSecurity()).apply(hstsConfig)
    }

    /**
     * Allows customizing the [XFrameOptionsHeaderWriter] which add the X-Frame-Options
     * header.
     *
     * @param frameOptionsConfig the customization to apply to the header
     */
    fun frameOptions(frameOptionsConfig: FrameOptionsDsl.() -> Unit) {
        FrameOptionsDsl(headersConfigurer.frameOptions()).apply(frameOptionsConfig)
    }

    /**
     * Allows customizing the [HpkpHeaderWriter] which provides support for <a
     * href="https://tools.ietf.org/html/rfc7469">HTTP Public Key Pinning (HPKP)</a>.
     *
     * @param hpkpConfig the customization to apply to the header
     */
    fun httpPublicKeyPinning(hpkpConfig: HttpPublicKeyPinningDsl.() -> Unit) {
        HttpPublicKeyPinningDsl(headersConfigurer.httpPublicKeyPinning()).apply(hpkpConfig)
    }

    /**
     * Allows configuration for <a href="https://www.w3.org/TR/CSP2/">Content Security Policy (CSP) Level 2</a>.
     *
     * <p>
     * Calling this method automatically enables (includes) the Content-Security-Policy header in the response
     * using the supplied security policy directive(s).
     * </p>
     *
     * @param contentSecurityPolicyConfig the customization to apply to the header
     */
    fun contentSecurityPolicy(contentSecurityPolicyConfig: ContentSecurityPolicyDsl.() -> Unit) {
        headersConfigurer.contentSecurityPolicy { ContentSecurityPolicyDsl(it).apply(contentSecurityPolicyConfig) }
    }

    /**
     * Allows configuration for <a href="https://www.w3.org/TR/referrer-policy/">Referrer Policy</a>.
     *
     * <p>
     * Configuration is provided to the [ReferrerPolicyHeaderWriter] which support the writing
     * of the header as detailed in the W3C Technical Report:
     * </p>
     * <ul>
     *  <li>Referrer-Policy</li>
     * </ul>
     *
     * @param referrerPolicyConfig the customization to apply to the header
     */
    fun referrerPolicy(referrerPolicyConfig: ReferrerPolicyDsl.() -> Unit) {
        ReferrerPolicyDsl(headersConfigurer.referrerPolicy()).apply(referrerPolicyConfig)
    }

    /**
     * Allows configuration for <a href="https://wicg.github.io/feature-policy/">Feature
     * Policy</a>.
     *
     * <p>
     * Calling this method automatically enables (includes) the Feature-Policy
     * header in the response using the supplied policy directive(s).
     * <p>
     *
     * @param policyDirectives policyDirectives the security policy directive(s)
     */
    fun featurePolicy(policyDirectives: String) {
        headersConfigurer.featurePolicy(policyDirectives)
    }
}
