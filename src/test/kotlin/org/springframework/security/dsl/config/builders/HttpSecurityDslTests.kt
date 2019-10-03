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

import com.google.common.net.HttpHeaders
import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import  org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * Tests for [HttpSecurityDsl]
 *
 * @author Eleftheria Stein
 */
class HttpSecurityDslTests {
    @Rule
    @JvmField
    val spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `post when default security configured then CSRF prevents the request`() {
        this.spring.register(DefaultSecurityConfig::class.java).autowire()

        this.mockMvc.post("/")
                .andExpect {
                    status { isForbidden }
                }
    }

    @Test
    fun `when default security configured then default headers are in the response`() {
        this.spring.register(DefaultSecurityConfig::class.java).autowire()

        this.mockMvc.get("/") {
            secure = true
        }.andExpect {
            header {
                string(HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff")
            }
            header {
                string(HttpHeaders.X_FRAME_OPTIONS, XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY.name)
            }
            header {
                string(HttpHeaders.STRICT_TRANSPORT_SECURITY, "max-age=31536000 ; includeSubDomains")
            }
            header {
                string(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate")
            }
            header {
                string(HttpHeaders.EXPIRES, "0")
            }
            header {
                string(HttpHeaders.PRAGMA, "no-cache")
            }
            header {
                string(HttpHeaders.X_XSS_PROTECTION, "1; mode=block")
            }
        }
    }

    @EnableWebSecurity
    class DefaultSecurityConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {}
        }

        @Configuration
        class UserConfig {
            @Bean
            fun userDetailsService(): UserDetailsService {
                val userDetails = User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build()
                return InMemoryUserDetailsManager(userDetails)
            }
        }
    }

    @Test
    fun `request when it does not match the security request matcher then the security rules do not apply`() {
        this.spring.register(SecurityRequestMatcherConfig::class.java).autowire()

        this.mockMvc.get("/")
                .andExpect {
                    status { isNotFound }
                }
    }

    @Test
    fun `request when it matches the security request matcher then the security rules apply`() {
        this.spring.register(SecurityRequestMatcherConfig::class.java).autowire()

        this.mockMvc.get("/path")
                .andExpect {
                    status { isForbidden }
                }
    }

    @EnableWebSecurity
    class SecurityRequestMatcherConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher(RegexRequestMatcher("/path", null))
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
            }
        }
    }

    @Test
    fun `request when it does not match the security pattern matcher then the security rules do not apply`() {
        this.spring.register(SecurityPatternMatcherConfig::class.java).autowire()

        this.mockMvc.get("/")
                .andExpect {
                    status { isNotFound }
                }
    }

    @Test
    fun `request when it matches the security pattern matcher then the security rules apply`() {
        this.spring.register(SecurityPatternMatcherConfig::class.java).autowire()

        this.mockMvc.get("/path")
                .andExpect {
                    status { isForbidden }
                }
    }

    @EnableWebSecurity
    @EnableWebMvc
    class SecurityPatternMatcherConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher("/path")
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
            }
        }
    }

    @Test
    fun `security pattern matcher when used with security request matcher then both apply`() {
        this.spring.register(MultiMatcherConfig::class.java).autowire()

        this.mockMvc.get("/path1")
                .andExpect {
                    status { isForbidden }
                }

        this.mockMvc.get("/path2")
                .andExpect {
                    status { isForbidden }
                }

        this.mockMvc.get("/path3")
                .andExpect {
                    status { isNotFound }
                }
    }

    @EnableWebSecurity
    @EnableWebMvc
    class MultiMatcherConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher("/path1")
                securityMatcher(RegexRequestMatcher("/path2", null))
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
            }
        }
    }
}
