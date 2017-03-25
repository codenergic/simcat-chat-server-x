package org.codenergic.simcat.auth.user.impl;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.codenergic.simcat.Simcat;
import org.codenergic.simcat.auth.user.UserRepository;
import org.codenergic.simcat.data.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.hazelcast.query.PagingPredicate;

@Repository
@Profile({ Simcat.PROFILE_DEVELOPMENT, Simcat.PROFILE_PRODUCTION })
public class HzUserRepository implements UserRepository {
	private IAtomicLong idGen;
	private IMap<Long, HzUser> userMap;

	public HzUserRepository(HazelcastInstance hzInstance) {
		this.idGen = hzInstance.getAtomicLong("userIdGen");
		this.userMap = hzInstance.getMap("users");
	}

	@Override
	public Stream<User> findAll(int limit) {
		return userMap.values(new PagingPredicate<>(limit)).stream()
				.map(HzUser::getUser);
	}

	@Override
	public Optional<User> getById(Long id) {
		HzUser hzUser = userMap.get(id);
		return Optional.ofNullable(hzUser == null ? null : hzUser.getUser());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<User> getByUsername(String username) {
		return userMap.values(e -> ((Entry<Long, HzUser>) e).getValue().getUser().username.equals(username))
				.stream()
				.map(HzUser::getUser)
				.findFirst();
	}

	@Override
	public User save(User user) {
		return saveOrUpdate(idGen.getAndIncrement(), user).orElse(null);
	}

	@Override
	public Optional<User> update(Long id, User user) {
		if (!userMap.containsKey(id))
			throw new IllegalArgumentException("Cannot find user with id: " + id);
		return saveOrUpdate(id, user);
	}

	private Optional<User> saveOrUpdate(Long id, User user) {
		User u = new User.Builder()
				.id(id)
				.username(user.username)
				.password(user.password)
				.build();
		userMap.put(id, new HzUser(u));
		return Optional.ofNullable(u);
	}
}
