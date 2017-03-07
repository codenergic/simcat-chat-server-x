package org.codenergic.simcat.chat.core.annotation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.codenergic.simcat.chat.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, RouterAnnotationProcessorTest.class })
@ActiveProfiles({ "test", "webtest" })
public class RouterAnnotationProcessorTest {
	@Autowired
	private HttpClient httpClient;

	@Test
	public void testRouting1() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(3);
		httpClient.get("/test1", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST1_BODY);
				latch.countDown();
			});
		}).end();
		httpClient.post("/test1", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST1_BODY);
				latch.countDown();
			});
		}).end();
		httpClient.delete("/test1", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST1_BODY);
				latch.countDown();
			});
		}).end();
		Assertions.assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testRouting2() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		httpClient.get("/test2", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST2_BODY);
				latch.countDown();
			});
		}).end();
		httpClient.post("/test2", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(404);
			latch.countDown();
		}).end();
		Assertions.assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testRouting3() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		httpClient.get("/test3", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST3_BODY);
				latch.countDown();
			});
		}).end();
		httpClient.post("/test3", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(404);
			latch.countDown();
		}).end();
		Assertions.assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testRouting4() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		httpClient.get("/test4", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST4_BODY);
				latch.countDown();
			});
		}).end();
		Assertions.assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testRouting5() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		httpClient.post("/test5", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST1_BODY);
				latch.countDown();
			});
		}).end(TestRouter.TEST1_BODY);
		httpClient.put("/test5", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			Assertions.assertThat(r.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(HttpHeaders.TEXT_HTML.toString());
			r.bodyHandler(body -> {
				Assertions.assertThat(body.toString()).isEqualTo(TestRouter.TEST2_BODY);
				latch.countDown();
			});
		}).putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).end(TestRouter.TEST2_BODY);
		Assertions.assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	public void testRouting6() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		httpClient.post("/test6", r -> {
			Assertions.assertThat(r.statusCode()).isEqualTo(200);
			r.bodyHandler(body -> {
				Assertions.assertThat(body.length()).isEqualTo(0);
				latch.countDown();
			});
		}).end(TestRouter.TEST1_BODY);
		Assertions.assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
	}

	@Bean
	public HttpClient httpClient(Vertx vertx) {
		return vertx.createHttpClient(new HttpClientOptions().setDefaultPort(8080));
	}
}
