/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.security.dsl.config.builders.servlet.oauth2.client

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

/**
 * A Kotlin DSL to configure OAuth 2.0 Authorization Code Grant.
 *
 * @author Eleftheria Stein
 * @property authorizationRequestResolver the resolver used for resolving [OAuth2AuthorizationRequest]'s.
 * @property authorizationRequestRepository the repository used for storing [OAuth2AuthorizationRequest]'s.
 * @property accessTokenResponseClient the client used for requesting the access token credential
 * from the Token Endpoint.
 */
class AuthorizationCodeGrantDsl {
    var authorizationRequestResolver: OAuth2AuthorizationRequestResolver? = null
    var authorizationRequestRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest>? = null
    var accessTokenResponseClient: OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>? = null

    internal fun get(): (OAuth2ClientConfigurer<HttpSecurity>.AuthorizationCodeGrantConfigurer) -> Unit {
        return { authorizationCodeGrant ->
            authorizationRequestResolver?.also { authorizationCodeGrant.authorizationRequestResolver(authorizationRequestResolver) }
            authorizationRequestRepository?.also { authorizationCodeGrant.authorizationRequestRepository(authorizationRequestRepository) }
            accessTokenResponseClient?.also { authorizationCodeGrant.accessTokenResponseClient(accessTokenResponseClient) }
        }
    }
}
