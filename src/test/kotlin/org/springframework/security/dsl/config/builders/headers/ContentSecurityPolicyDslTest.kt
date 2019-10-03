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

package org.springframework.security.dsl.config.builders.headers

import com.google.common.net.HttpHeaders
import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.dsl.config.builders.invoke
import org.springframework.security.dsl.test.SpringTestRule
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Tests for [ContentSecurityPolicyDsl]
 *
 * @author Eleftheria Stein
 */
class ContentSecurityPolicyDslTest {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun headersWhenContentSecurityPolicyConfiguredThenHeaderInResponse() {
        this.spring.register(ContentSecurityPolicyConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.CONTENT_SECURITY_POLICY, "default-src 'self'") }
        }
    }

    @EnableWebSecurity
    class ContentSecurityPolicyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    contentSecurityPolicy { }
                }
            }
        }
    }

    @Test
    fun headersWhenContentSecurityPolicyConfiguredWithCustomPolicyDirectivesThenCustomDirectivesInHeader() {
        this.spring.register(HpkpNoPinConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.CONTENT_SECURITY_POLICY, "default-src 'self'; script-src trustedscripts.example.com") }
        }
    }

    @EnableWebSecurity
    class HpkpNoPinConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    contentSecurityPolicy {
                        policyDirectives = "default-src 'self'; script-src trustedscripts.example.com"
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenReportOnlyContentSecurityPolicyReportOnlyHeaderInResponse() {
        this.spring.register(ReportOnlyConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.CONTENT_SECURITY_POLICY_REPORT_ONLY, "default-src 'self'") }
        }
    }

    @EnableWebSecurity
    class ReportOnlyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    contentSecurityPolicy {
                        reportOnly = true
                    }
                }
            }
        }
    }
}
