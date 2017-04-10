package org.codenergic.simcat.auth.core.auth0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;

import org.codenergic.simcat.auth.core.auth0.Auth0Service.Connection;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class Auth0ServiceTest {
	private Auth0Service auth0Service;

	@Test(timeout = 2000)
	public void testSendPasswordlessEmail() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		auth0Service.sendVerificationCode("test@example.com", Connection.EMAIL, h -> {
			assertThat(h.succeeded()).isTrue();
			PasswordlessInfo info = h.result();
			assertThat(info.getEmail()).isEqualTo("test@example.com");
			latch.countDown();
		});
		auth0Service.sendVerificationCode("test@example", Connection.EMAIL, h -> {
			assertThat(h.failed());
			latch.countDown();
		});
		latch.await();
	}

	@Test(timeout = 2000)
	public void testVerifyPasswordlessEmail() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		auth0Service.verifyCode("test@example.com", "123", Connection.EMAIL, h -> {
			assertThat(h.succeeded()).isTrue();
			Auth0User user = h.result();
			assertThat(user.getEmail()).isEqualTo("test@example.com");
			latch.countDown();
		});
		latch.await();
	}
}
