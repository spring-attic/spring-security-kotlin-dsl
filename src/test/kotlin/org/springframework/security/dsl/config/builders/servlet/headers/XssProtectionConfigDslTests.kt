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
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Tests for [XssProtectionConfigDsl]
 *
 * @author Eleftheria Stein
 */
class XssProtectionConfigDslTests {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun headersWhenXssProtectionConfiguredThenHeaderInResponse() {
        this.spring.register(XssProtectionConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION, "1; mode=block") }
        }
    }

    @EnableWebSecurity
    class XssProtectionConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    xssProtection { }
                }
            }
        }
    }

    @Test
    fun headersWhenXssProtectionWithBlockFalseThenModeIsNotBlockInHeader() {
        this.spring.register(XssProtectionBlockFalseConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION, "1") }
        }
    }

    @EnableWebSecurity
    class XssProtectionBlockFalseConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    xssProtection {
                        block = false
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenXssProtectionDisabledThenHeaderValue0() {
        this.spring.register(XssProtectionDisabledConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION, "0") }
        }
    }

    @EnableWebSecurity
    class XssProtectionDisabledConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    xssProtection {
                        xssProtectionEnabled = false
                    }
                }
            }
        }
    }

    @Test
    fun `headers when XssProtection disabled via method then no X-Xss-Protection header in response`() {
        this.spring.register(XssProtectionDisabledViaMethodConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { doesNotExist(XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION) }
        }
    }

    @EnableWebSecurity
    class XssProtectionDisabledViaMethodConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    xssProtection {
                        disable()
                    }
                }
            }
        }
    }
}
