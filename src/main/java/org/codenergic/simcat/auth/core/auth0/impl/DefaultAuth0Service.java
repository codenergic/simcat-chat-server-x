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

	public DefaultAuth0Service(Auth0Credentials credentials, JWTVerifier jwtVerifier, Vertx vertx) {
		this.credentials = credentials;
		this.httpClient = vertx.createHttpClient(new HttpClientOptions().setLocalAddress(credentials.getDomain()));
		this.jwtVerifier = jwtVerifier;
	}

	@Override
	public void sendPasswordlessEmail(String email, Handler<AsyncResult<PasswordlessInfo>> handler) {
		JsonObject requestBody = new JsonObject()
				.put("client_id", credentials.getClientId())
				.put("connection", "email")
				.put("email", email)
				.put("send", "code");
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
				.put("client_id", credentials.getClientId())
				.put("connection", "email")
				.put("grant_type", "password")
				.put("scope", "openid profile email")
				.put("username", username)
				.put("password", password);
		Future<Auth0User> future = Future.future();
		future.setHandler(handler);
		httpClient.post("/passwordless/start", h -> {
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
