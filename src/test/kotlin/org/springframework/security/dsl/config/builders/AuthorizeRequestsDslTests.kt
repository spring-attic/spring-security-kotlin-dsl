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
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import  org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * Tests for [AuthorizeRequestsDsl]
 *
 * @author Eleftheria Stein
 */
class AuthorizeRequestsDslTests {
    @Rule
    @JvmField
    val spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun requestWhenSecuredByRegexMatcherThenRespondsWithForbidden() {
        this.spring.register(AuthorizeRequestsByRegexConfig::class.java).autowire()

        this.mockMvc.get("/private")
                .andExpect {
                    status { isForbidden }
                }
    }

    @Test
    fun requestWhenAllowedByRegexMatcherThenRespondsWithOk() {
        this.spring.register(AuthorizeRequestsByRegexConfig::class.java).autowire()

        this.mockMvc.get("/path")
                .andExpect {
                    status { isOk }
                }
    }

    @EnableWebSecurity
    class AuthorizeRequestsByRegexConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                authorizeRequests {
                    authorize(RegexRequestMatcher("/path", null), permitAll)
                    authorize(RegexRequestMatcher(".*", null), authenticated)
                }
            }
        }

        @RestController
        internal class PathController {
            @RequestMapping("/path")
            fun path() {
            }
        }
    }

    @Test
    fun requestWhenSecuredByMvcThenRespondsWithForbidden() {
        this.spring.register(AuthorizeRequestsByMvcConfig::class.java).autowire()

        this.mockMvc.get("/private")
                .andExpect {
                    status { isForbidden }
                }
    }

    @Test
    fun requestWhenAllowedByMvcThenRespondsWithOk() {
        this.spring.register(AuthorizeRequestsByMvcConfig::class.java).autowire()

        this.mockMvc.get("/path")
                .andExpect {
                    status { isOk }
                }

        this.mockMvc.get("/path.html")
                .andExpect {
                    status { isOk }
                }

        this.mockMvc.get("/path/")
                .andExpect {
                    status { isOk }
                }
    }

    @EnableWebSecurity
    @EnableWebMvc
    class AuthorizeRequestsByMvcConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                authorizeRequests {
                    authorize("/path", permitAll)
                    authorize("/**", authenticated)
                }
            }
        }

        @RestController
        internal class PathController {
            @RequestMapping("/path")
            fun path() {
            }
        }
    }

    @Test
    fun requestWhenSecuredByMvcPathVariablesThenRespondsBasedOnPathVariableValue() {
        this.spring.register(MvcMatcherPathVariablesConfig::class.java).autowire()

        this.mockMvc.get("/user/user")
                .andExpect {
                    status { isOk }
                }

        this.mockMvc.get("/user/deny")
                .andExpect {
                    status { isForbidden }
                }
    }

    @EnableWebSecurity
    @EnableWebMvc
    class MvcMatcherPathVariablesConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                authorizeRequests {
                    authorize("/user/{userName}", "#userName == 'user'")
                }
            }
        }

        @RestController
        internal class PathController {
            @RequestMapping("/user/{user}")
            fun path(@PathVariable user: String) {
            }
        }
    }

    @Test
    fun requestWhenSecuredByMvcWithServletPathThenRespondsBasedOnServletPath() {
        this.spring.register(MvcMatcherServletPathConfig::class.java).autowire()

        this.mockMvc.perform(MockMvcRequestBuilders.get("/spring/path")
                .with { request ->
                    request.servletPath = "/spring"
                    request
                })
                .andExpect(status().isForbidden)

        this.mockMvc.perform(MockMvcRequestBuilders.get("/other/path")
                .with { request ->
                    request.servletPath = "/other"
                    request
                })
                .andExpect(status().isOk)
    }

    @EnableWebSecurity
    @EnableWebMvc
    class MvcMatcherServletPathConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                authorizeRequests {
                    authorize("/path",
                            "/spring",
                            denyAll)
                }
            }
        }

        @RestController
        internal class PathController {
            @RequestMapping("/path")
            fun path() {
            }
        }
    }
}
