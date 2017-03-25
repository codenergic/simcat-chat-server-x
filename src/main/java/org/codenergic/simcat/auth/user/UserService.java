package org.codenergic.simcat.auth.user;

import java.util.Optional;
import java.util.stream.Stream;

import org.codenergic.simcat.data.User;

public interface UserService {
	Stream<User> findAllUsers(int limit);

	Optional<User> getUserById(Long id);

	Optional<User> getUserByUsername(String username);

	User saveUser(User user);

	Optional<User> updateUser(Long id, User user);
}
