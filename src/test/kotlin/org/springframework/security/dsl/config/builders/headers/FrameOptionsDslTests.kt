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

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.dsl.config.builders.invoke
import org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Tests for [FrameOptionsDsl]
 *
 * @author Eleftheria Stein
 */
class FrameOptionsDslTests {
    @Rule
    @JvmField
    var spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun headersWhenFrameOptionsConfiguredThenFrameOptionsDenyHeader() {
        this.spring.register(FrameOptionsConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS, XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY.name) }
        }
    }

    @EnableWebSecurity
    class FrameOptionsConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    frameOptions { }
                }
            }
        }
    }

    @Test
    fun headersWhenFrameOptionsDenyConfiguredThenFrameOptionsDenyHeader() {
        this.spring.register(FrameOptionsDenyConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS, XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY.name) }
        }
    }

    @EnableWebSecurity
    class FrameOptionsDenyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    frameOptions {
                        deny = true
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenFrameOptionsSameOriginConfiguredThenFrameOptionsSameOriginHeader() {
        this.spring.register(FrameOptionsSameOriginConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS, XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN.name) }
        }
    }

    @EnableWebSecurity
    class FrameOptionsSameOriginConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    frameOptions {
                        sameOrigin = true
                    }
                }
            }
        }
    }

    @Test
    fun headersWhenFrameOptionsSameOriginAndDenyConfiguredThenFrameOptionsDenyHeader() {
        this.spring.register(FrameOptionsSameOriginAndDenyConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header { string(XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS, XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY.name) }
        }
    }

    @EnableWebSecurity
    class FrameOptionsSameOriginAndDenyConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                headers {
                    defaultsDisabled = true
                    frameOptions {
                        sameOrigin = true
                        deny = true
                    }
                }
            }
        }
    }
}
