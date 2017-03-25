package org.codenergic.simcat.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.codenergic.simcat.auth.user.impl.UserServiceImpl;
import org.codenergic.simcat.data.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import io.vertx.core.Vertx;

public class AsyncUserServiceTest {
	@InjectMocks
	private AsyncUserService userService = new UserServiceImpl();
	@Mock
	private UserRepository userRepository;
	@Spy
	private Vertx vertx = Vertx.vertx();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(timeout = 1000)
	public void testSaveAndGetUser() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		User input = new User.Builder()
				.username("test")
				.password("test")
				.build();
		User output = new User.Builder()
				.id(0L)
				.username("test")
				.password("test")
				.build();
		when(userRepository.save(input)).thenReturn(output);
		userService.saveUser(input, h -> {
			User result = h.result();
			assertThat(result).isNotNull();
			assertThat(result).isEqualTo(output);
			latch.countDown();
		});
		latch.await();
	}

	@SuppressWarnings("unchecked")
	@Test(timeout = 1000)
	public void testUpdateUserFail() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		User input = new User.Builder()
				.username("test")
				.password("test")
				.build();
		when(userRepository.update(0L, input)).thenThrow(IllegalArgumentException.class);
		userService.udpateUser(0L, input, h -> {
			assertThat(h.failed()).isTrue();
			assertThat(h.cause()).isInstanceOf(IllegalArgumentException.class);
			assertThat(h.result()).isNull();
			latch.countDown();
		});
		latch.await();
	}

	@Test(timeout = 1000)
	public void testUpdateAndGetUser() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		User input = new User.Builder()
				.username("test")
				.password("test")
				.build();
		User output = new User.Builder()
				.id(0L)
				.username("test")
				.password("test")
				.build();
		when(userRepository.update(0L, input)).thenReturn(Optional.of(output));
		userService.udpateUser(0L, input, h -> {
			User result = h.result();
			assertThat(result).isNotNull();
			assertThat(result).isEqualTo(output);
			latch.countDown();
		});
		latch.await();
	}

	@Test(timeout = 1000)
	public void testFindAllUsers() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		User output = new User.Builder()
				.id(0L)
				.username("test")
				.password("test")
				.build();
		List<User> outputs = Arrays.asList(output);
		when(userRepository.findAll(1)).thenReturn(outputs.stream());
		userService.findAllUsers(1, h -> {
			List<User> result = h.result();
			assertThat(result).isEqualTo(outputs);
			assertThat(result).isNotNull();
			assertThat(result.get(0)).isEqualTo(output);
			latch.countDown();
		});
		latch.await();
	}

	@Test(timeout = 2000)
	public void testGetByIdAndUsername() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(4);
		User output = new User.Builder()
				.id(0L)
				.username("test")
				.password("test")
				.build();
		when(userRepository.getById(0L)).thenReturn(Optional.of(output));
		when(userRepository.getById(1L)).thenReturn(Optional.ofNullable(null));
		userService.getUserById(0L, h -> {
			assertThat(h.succeeded()).isTrue();
			assertThat(h.result()).isEqualTo(output);
			latch.countDown();
		});
		userService.getUserById(1L, h -> {
			assertThat(h.failed()).isTrue();
			assertThat(h.cause()).isInstanceOf(IllegalArgumentException.class);
			latch.countDown();
		});
		when(userRepository.getByUsername("1")).thenReturn(Optional.of(output));
		when(userRepository.getByUsername("2")).thenReturn(Optional.ofNullable(null));
		userService.getUserByUsername("1", h -> {
			assertThat(h.succeeded()).isTrue();
			assertThat(h.result()).isEqualTo(output);
			latch.countDown();
		});
		userService.getUserByUsername("2", h -> {
			assertThat(h.failed()).isTrue();
			assertThat(h.cause()).isInstanceOf(IllegalArgumentException.class);
			latch.countDown();
		});
		latch.await();
	}
}
