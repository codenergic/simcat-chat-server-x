package org.codenergic.simcat.auth.user.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codenergic.simcat.auth.user.AsyncUserService;
import org.codenergic.simcat.data.User;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;

public class UserRestServiceTest {
	private final Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
	@InjectMocks
	private UserRestService userRestService = new UserRestService();
	@Mock
	private AsyncUserService userService;
	@Captor
	private ArgumentCaptor<Handler<AsyncResult<List<User>>>> usersCaptor;

	private final User input = new User.Builder()
			.username("test")
			.password("test")
			.build();
	private final User output = new User.Builder()
			.id(0L)
			.username("test")
			.password("test")
			.build();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		dispatcher.getRegistry().addSingletonResource(userRestService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindAllUsers() throws URISyntaxException {
		doAnswer(invocation -> {
			Handler<AsyncResult<List<User>>> handler = invocation.getArgumentAt(1, Handler.class);
			Future.<List<User>>future().setHandler(handler).complete(Arrays.asList(output));
			return null;
		}).when(userService).findAllUsers(anyInt(), any());

		MockHttpRequest request = MockHttpRequest.get("/api/users");
		request.contentType(MediaType.APPLICATION_JSON);
		MockHttpResponse response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		assertThat(response.getOutputHeaders().getFirst(HttpHeaders.CONTENT_TYPE).toString()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(response.getContentAsString()).isEqualTo(Json.encode(Arrays.asList(new UserRestData(output))));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetById() throws URISyntaxException {
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(1, Handler.class);
			Future.<User>future().setHandler(handler).complete(output);
			return null;
		}).when(userService).getUserById(eq(0L), any());
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(1, Handler.class);
			Future.<User>future().setHandler(handler).fail(new IllegalArgumentException());
			return null;
		}).when(userService).getUserById(eq(1L), any());
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(1, Handler.class);
			Future.<User>future().setHandler(handler).fail(new RuntimeException());
			return null;
		}).when(userService).getUserById(eq(2L), any());

		MockHttpRequest request = MockHttpRequest.get("/api/users/0");
		request.contentType(MediaType.APPLICATION_JSON);
		MockHttpResponse response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		assertThat(response.getOutputHeaders().getFirst(HttpHeaders.CONTENT_TYPE).toString()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(response.getContentAsString()).isEqualTo(Json.encode(new UserRestData(output)));

		request = MockHttpRequest.get("/api/users/1");
		request.contentType(MediaType.APPLICATION_JSON);
		response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

		request = MockHttpRequest.get("/api/users/2");
		request.contentType(MediaType.APPLICATION_JSON);
		response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSaveUser() throws URISyntaxException {
		doThrow(Exception.class).when(userService).saveUser(any(), any());
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(1, Handler.class);
			Future.<User>future().setHandler(handler).complete(output);
			return null;
		}).when(userService).saveUser(eq(input), any());

		MockHttpRequest request = MockHttpRequest.post("/api/users");
		request.contentType(MediaType.APPLICATION_JSON);
		request.content(Json.encode(new UserRestData(input)).getBytes());
		MockHttpResponse response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		assertThat(response.getOutputHeaders().getFirst(HttpHeaders.CONTENT_TYPE).toString()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(response.getContentAsString()).isEqualTo(Json.encode(new UserRestData(output)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateUser() throws URISyntaxException {
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(2, Handler.class);
			Future.<User>future().setHandler(handler).complete(output);
			return null;
		}).when(userService).updateUser(eq(0L), eq(input), any());
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(2, Handler.class);
			Future.<User>future().setHandler(handler).fail(new IllegalArgumentException());
			return null;
		}).when(userService).updateUser(eq(1L), any(), any());
		doAnswer(invocation -> {
			Handler<AsyncResult<User>> handler = invocation.getArgumentAt(2, Handler.class);
			Future.<User>future().setHandler(handler).fail(new RuntimeException());
			return null;
		}).when(userService).updateUser(eq(2L), any(), any());

		MockHttpRequest request = MockHttpRequest.put("/api/users/0");
		request.contentType(MediaType.APPLICATION_JSON);
		request.content(Json.encode(new UserRestData(input)).getBytes());
		MockHttpResponse response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
		assertThat(response.getOutputHeaders().getFirst(HttpHeaders.CONTENT_TYPE).toString()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(response.getContentAsString()).isEqualTo(Json.encode(new UserRestData(output)));

		request = MockHttpRequest.put("/api/users/1");
		request.contentType(MediaType.APPLICATION_JSON);
		request.content(Json.encode(new UserRestData(input)).getBytes());
		response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

		request = MockHttpRequest.put("/api/users/2");
		request.contentType(MediaType.APPLICATION_JSON);
		request.content(Json.encode(new UserRestData(input)).getBytes());
		response = new MockHttpResponse();
		registerAsyncContext(dispatcher, request, response);
		dispatcher.invoke(request, response);
		assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	private void registerAsyncContext(Dispatcher dispatcher, MockHttpRequest request, MockHttpResponse response) {
		AsynchronousDispatcher asyncDispatcher = new AsynchronousDispatcher(dispatcher.getProviderFactory());
		ResteasyAsynchronousContext asyncContext = new SynchronousExecutionContext(asyncDispatcher, request, response);
		request.setAsynchronousContext(asyncContext);
	}
}
