package org.codenergic.simcat.auth.user.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codenergic.simcat.Simcat;
import org.codenergic.simcat.auth.user.AsyncUserService;
import org.codenergic.simcat.auth.user.UserRepository;
import org.codenergic.simcat.auth.user.UserService;
import org.codenergic.simcat.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

@Service
@Profile({ Simcat.PROFILE_DEVELOPMENT, Simcat.PROFILE_PRODUCTION })
public class UserServiceImpl implements UserService, AsyncUserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Vertx vertx;

	@Override
	public Stream<User> findAllUsers(int limit) {
		return userRepository.findAll(limit);
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return userRepository.getById(id);
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		return userRepository.getByUsername(username);
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public Optional<User> updateUser(Long id, User user) {
		return userRepository.update(id, user);
	}

	@Override
	public void findAllUsers(int limit, Handler<AsyncResult<List<User>>> handler) {
		vertx.executeBlocking(h -> h.complete(findAllUsers(limit).collect(Collectors.toList())), false, handler);
	}

	@Override
	public void getUserById(Long id, Handler<AsyncResult<User>> handler) {
		vertx.executeBlocking(h -> h.complete(getUserById(id).orElseThrow(IllegalArgumentException::new)), false, handler);
	}

	@Override
	public void getUserByUsername(String username, Handler<AsyncResult<User>> handler) {
		vertx.executeBlocking(h -> h.complete(getUserByUsername(username).orElseThrow(IllegalArgumentException::new)), false, handler);
	}

	@Override
	public void saveUser(User user, Handler<AsyncResult<User>> handler) {
		vertx.executeBlocking(h -> h.complete(saveUser(user)), false, handler);
	}

	@Override
	public void updateUser(Long id, User user, Handler<AsyncResult<User>> handler) {
		vertx.executeBlocking(h -> h.complete(updateUser(id, user).orElseThrow(IllegalArgumentException::new)), false, handler);
	}
}
