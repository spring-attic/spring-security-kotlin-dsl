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
import org.assertj.core.api.Assertions
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
 * Tests for [HttpPublicKeyPinningDsl]
 *
 * @author Eleftheria Stein
 */
class HttpPublicKeyPinningDslTests {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc


    @Test
    fun headersWhenHpkpConfiguredAndNoPinThenNoHeadersInResponse() {
        this.spring.register(HpkpNoPinConfig::class.java).autowire()

        val result = this.mockMvc.get("/") {
            secure = true
        }.andReturn()

        Assertions.assertThat(result.response.headerNames).isEmpty()
    }

    @EnableWebSecurity
    open class HpkpNoPinConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpPublicKeyPinning { }
                }
            }
        }
    }

    @Test
    fun headersWhenHpkpConfiguredWithPinThenHeaderInResponse() {
        this.spring.register(HpkpPinConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.PUBLIC_KEY_PINS_REPORT_ONLY, "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"") }
        }
    }

    @EnableWebSecurity
    class HpkpPinConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpPublicKeyPinning {
                        pins = mapOf(Pair("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256"))
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenHpkpConfiguredWithMaximumAgeThenMaximumAgeInHeader() {
        this.spring.register(HpkpMaxAgeConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.PUBLIC_KEY_PINS_REPORT_ONLY, "max-age=604800 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"") }
        }
    }

    @EnableWebSecurity
    open class HpkpMaxAgeConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpPublicKeyPinning {
                        pins = mapOf(Pair("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256"))
                        maxAgeInSeconds = 604800
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenHpkpConfiguredWithReportOnlyFalseThenPublicKeyPinsHeaderInResponse() {
        this.spring.register(HpkpReportOnlyFalseConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.PUBLIC_KEY_PINS, "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\"") }
        }
    }

    @EnableWebSecurity
    class HpkpReportOnlyFalseConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpPublicKeyPinning {
                        pins = mapOf(Pair("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256"))
                        reportOnly = false
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenHpkpConfiguredWithIncludeSubdomainsThenIncludeSubdomainsInHeader() {
        this.spring.register(HpkpIncludeSubdomainsConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.PUBLIC_KEY_PINS_REPORT_ONLY,
                    "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; includeSubDomains") }
        }
    }

    @EnableWebSecurity
    class HpkpIncludeSubdomainsConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpPublicKeyPinning {
                        pins = mapOf(Pair("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256"))
                        includeSubDomains = true
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenHpkpConfiguredWithReportUriThenReportUriInHeader() {
        this.spring.register(HpkpReportUriConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(HttpHeaders.PUBLIC_KEY_PINS_REPORT_ONLY,
                    "max-age=5184000 ; pin-sha256=\"d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=\" ; report-uri=\"https://example.com\"") }
        }
    }

    @EnableWebSecurity
    class HpkpReportUriConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    httpPublicKeyPinning {
                        pins = mapOf(Pair("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM=", "sha256"))
                        reportUri = "https://example.com"
                    }
                }
            }
        }
    }
}
