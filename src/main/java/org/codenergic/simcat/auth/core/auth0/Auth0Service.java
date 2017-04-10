package org.codenergic.simcat.auth.core.auth0;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Auth0Service {
	public enum Connection {
		EMAIL("email", "email"), SMS("sms", "phone_number");
		String connectionKey;
		String field;

		Connection(String connectionKey, String field) {
			this.connectionKey = connectionKey;
			this.field = field;
		}

		public String getConnectionKey() {
			return connectionKey;
		}

		public String getField() {
			return field;
		}
	}

	void sendVerificationCode(String emailOrPhone, Connection connection, Handler<AsyncResult<PasswordlessInfo>> handler);

	void verifyCode(String emailOrPhone, String code, Connection connection, Handler<AsyncResult<Auth0User>> handler);
}
