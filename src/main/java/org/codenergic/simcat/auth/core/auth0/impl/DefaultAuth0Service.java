package org.codenergic.simcat.auth.core.auth0.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.codenergic.simcat.auth.core.auth0.Auth0Credentials;
import org.codenergic.simcat.auth.core.auth0.Auth0Service;
import org.codenergic.simcat.auth.core.auth0.Auth0User;
import org.codenergic.simcat.auth.core.auth0.PasswordlessInfo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWTVerifier;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

@Service
@EnableConfigurationProperties(Auth0Credentials.class)
public class DefaultAuth0Service implements Auth0Service {
	private Auth0Credentials credentials;
	private HttpClient httpClient;
	private JWTVerifier jwtVerifier;

	private static final String PROPS_CLIENT_ID = "client_id";
	private static final String PROPS_CODE = "code";
	private static final String PROPS_CONNECTION = "connection";
	private static final String PROPS_EMAIL = "email";
	private static final String PROPS_GRANT_TYPE = "grant_type";
	private static final String PROPS_PASSWORD = "password";
	private static final String PROPS_SCOPE = "scope";
	private static final String PROPS_SEND = "send";
	private static final String PROPS_USERNAME = "username";

	public DefaultAuth0Service(Auth0Credentials credentials, JWTVerifier jwtVerifier, Vertx vertx) {
		this.credentials = credentials;
		this.httpClient = vertx.createHttpClient(new HttpClientOptions()
				.setDefaultHost(credentials.getDomain())
				.setDefaultPort(443)
				.setSsl(true));
		this.jwtVerifier = jwtVerifier;
	}

	@Override
	public void sendPasswordlessEmail(String email, Handler<AsyncResult<PasswordlessInfo>> handler) {
		JsonObject requestBody = new JsonObject()
				.put(PROPS_CLIENT_ID, credentials.getClientId())
				.put(PROPS_CONNECTION, PROPS_EMAIL)
				.put(PROPS_EMAIL, email)
				.put(PROPS_SEND, PROPS_CODE);
		Future<PasswordlessInfo> future = Future.future();
		future.setHandler(handler);
		httpClient.post("/passwordless/start", h -> {
			if (h.statusCode() != 200) {
				future.fail(new IllegalArgumentException(h.statusMessage()));
				return;
			}
			h.bodyHandler(body -> {
				PasswordlessInfo info = Json.decodeValue(body.toString(), PasswordlessInfo.class);
				future.complete(info);
			});
		})
			.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
			.end(requestBody.encode());
	}

	@Override
	public void verifyPasswordlessEmail(String username, String password, Handler<AsyncResult<Auth0User>> handler) {
		JsonObject requestBody = new JsonObject()
				.put(PROPS_CLIENT_ID, credentials.getClientId())
				.put(PROPS_CONNECTION, PROPS_EMAIL)
				.put(PROPS_GRANT_TYPE, PROPS_PASSWORD)
				.put(PROPS_SCOPE, "openid profile email")
				.put(PROPS_USERNAME, username)
				.put(PROPS_PASSWORD, password);
		Future<Auth0User> future = Future.future();
		future.setHandler(handler);
		httpClient.post("/oauth/ro", h -> {
			if (h.statusCode() != 200) {
				future.fail(new IllegalArgumentException(h.statusMessage()));
				return;
			}
			h.bodyHandler(body -> {
				PasswordlessInfo info = Json.decodeValue(body.toString(), PasswordlessInfo.class);
				String jwt = info.getIdToken();
				try {
					jwtVerifier.verify(jwt);
					String payload = StringUtils.split(jwt, '.')[1];
					byte[] decodedPayload = Base64.decodeBase64(payload);
					Auth0User user = Json.mapper.readValue(decodedPayload, Auth0User.class);
					future.complete(user);
				} catch (Exception e) {
					future.fail(e);
				}
			});
		})
			.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
			.end(requestBody.encode());
	}

}
