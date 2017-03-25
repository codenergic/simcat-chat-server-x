package org.codenergic.simcat.auth.user;

import java.util.Optional;
import java.util.stream.Stream;

import org.codenergic.simcat.data.User;

public interface UserRepository {
	Stream<User> findAll(int limit);

	Optional<User> getById(Long id);

	Optional<User> getByUsername(String username);

	User save(User user);

	Optional<User> update(Long id, User user);
}
