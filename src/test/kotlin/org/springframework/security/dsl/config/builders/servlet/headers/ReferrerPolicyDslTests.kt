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

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.dsl.config.builders.servlet.invoke
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Tests for [ReferrerPolicyDsl]
 *
 * @author Eleftheria Stein
 */
class ReferrerPolicyDslTests {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun headersWhenReferrerPolicyConfiguredThenHeaderInResponse() {
        this.spring.register(ReferrerPolicyConfig::class.java).autowire()

        this.mockMvc.get("/")
                .andExpect {
                    header { string("Referrer-Policy", ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER.policy) }
                }
    }

    @EnableWebSecurity
    class ReferrerPolicyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    referrerPolicy { }
                }
            }
        }
    }

    @Test
    fun headersWhenReferrerPolicyConfiguredWithCustomPolicyThenCustomPolicyInHeader() {
        this.spring.register(ReferrerPolicyCustomPolicyConfig::class.java).autowire()

        this.mockMvc.get("/")
                .andExpect {
                    header { string("Referrer-Policy", ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN.policy) }
                }
    }

    @EnableWebSecurity
    class ReferrerPolicyCustomPolicyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    referrerPolicy {
                        policy = ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN
                    }
                }
            }
        }
    }
}
