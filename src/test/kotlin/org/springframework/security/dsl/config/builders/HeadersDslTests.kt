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

package org.springframework.security.dsl.config.builders

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import  org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.security.web.server.header.ContentTypeOptionsServerHttpHeadersWriter
import org.springframework.security.web.server.header.StrictTransportSecurityServerHttpHeadersWriter
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Tests for [HeadersDsl]
 *
 * @author Eleftheria Stein
 */
class HeadersDslTests {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun headersWhenDefaultsEnabledThenDefaultHeadersInResponse() {
        this.spring.register(DefaultHeadersConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(ContentTypeOptionsServerHttpHeadersWriter.X_CONTENT_OPTIONS, "nosniff") }
            header { string(XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS, XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY.name) }
            header { string(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY, "max-age=31536000 ; includeSubDomains") }
            header { string(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate") }
            header { string(HttpHeaders.EXPIRES, "0") }
            header { string(HttpHeaders.PRAGMA, "no-cache") }
            header { string(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION, "1; mode=block") }
        }
    }

    @EnableWebSecurity
    class DefaultHeadersConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers { }
            }
        }
    }

    @Test
    fun headersWhenFeaturePolicyConfiguredThenHeaderInResponse() {
        this.spring.register(FeaturePolicyConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string("Feature-Policy", "geolocation 'self'") }
        }
    }

    @EnableWebSecurity
    class FeaturePolicyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    featurePolicy(policyDirectives = "geolocation 'self'")
                }
            }
        }
    }
}
