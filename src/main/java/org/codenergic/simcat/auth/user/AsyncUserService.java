package org.codenergic.simcat.auth.user;

import java.util.List;

import org.codenergic.simcat.data.User;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface AsyncUserService {
	void findAllUsers(int limit, Handler<AsyncResult<List<User>>> handler);

	void getUserById(Long id, Handler<AsyncResult<User>> handler);

	void getUserByUsername(String username, Handler<AsyncResult<User>> handler);

	void saveUser(User user, Handler<AsyncResult<User>> handler);

	void updateUser(Long id, User user, Handler<AsyncResult<User>> handler);
}
