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

import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.dsl.config.builders.invoke
import org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.web.server.header.StrictTransportSecurityServerHttpHeadersWriter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Tests for [HttpStrictTransportSecurityDsl]
 *
 * @author Eleftheria Stein
 */
class HttpStrictTransportSecurityDslTests {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun headersWhenHstsConfiguredThenHeadersInResponse() {
        this.spring.register(HstsConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY, "max-age=31536000 ; includeSubDomains") }
        }
    }

    @EnableWebSecurity
    class HstsConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpStrictTransportSecurity { }
                }
            }
        }
    }

    @Test
    fun headersWhenHstsConfiguredWithPreloadThenPreloadInHeader() {
        this.spring.register(HstsPreloadConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY, "max-age=31536000 ; includeSubDomains ; preload") }
        }
    }

    @EnableWebSecurity
    class HstsPreloadConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpStrictTransportSecurity {
                        preload = true
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenHstsConfiguredWithMaxAgeThenMaxAgeInHeader() {
        this.spring.register(HstsMaxAgeConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY, "max-age=1 ; includeSubDomains") }
        }
    }

    @EnableWebSecurity
    class HstsMaxAgeConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpStrictTransportSecurity {
                        maxAgeInSeconds = 1
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenHstsConfiguredAndDoesNotMatchThenHstsHeaderNotInResponse() {
        this.spring.register(HstsCustomMatcherConfig::class.java).autowire()

        val result = this.mockMvc.get("/") {
            secure = true
        }.andReturn()

        Assertions.assertThat(result.response.headerNames).isEmpty()
    }

    @EnableWebSecurity
    class HstsCustomMatcherConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpStrictTransportSecurity {
                        requestMatcher = AntPathRequestMatcher("/secure/**")
                    }
                }
            }
        }
    }
}
