package org.codenergic.simcat.auth.core.auth0;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Auth0Service {
	void sendPasswordlessEmail(String email, Handler<AsyncResult<PasswordlessInfo>> handler);

	void verifyPasswordlessEmail(String username, String password, Handler<AsyncResult<Auth0User>> handler);
}
