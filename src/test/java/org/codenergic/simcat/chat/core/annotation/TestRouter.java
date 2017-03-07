package org.codenergic.simcat.chat.core.annotation;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

@Router
public class TestRouter {
	protected static final String TEST1_BODY = "TEST1_BODY";
	protected static final String TEST2_BODY = "TEST2_BODY";
	protected static final String TEST3_BODY = "TEST3_BODY";
	protected static final String TEST4_BODY = "TEST4_BODY";

	@Route(path = "/test1")
	public void testRoute1(RoutingContext routingContext) {
		routingContext.response().end(TEST1_BODY);
	}

	@Route(path = "/test2", method = HttpMethod.GET)
	public void testRoute2(RoutingContext routingContext) {
		routingContext.response().end(TEST2_BODY);
	}

	@Route(path = "/test3", method = HttpMethod.GET, blocking = true)
	public void testRoute3(RoutingContext routingContext) {
		routingContext.response().end(TEST3_BODY);
	}

	@RouteWithRegex(@Route(path = "/test4"))
	public void testRoute4(RoutingContext routingContext) {
		routingContext.response().end(TEST4_BODY);
	}

	@Route(path = "/test5")
	public void testRoute5(RoutingContext routingContext) {
		HttpServerRequest request = routingContext.request();
		HttpServerResponse response = routingContext.response();
		response.headers().addAll(request.headers());
		request.bodyHandler(response::end);
	}

	@Route(path = "/test6", handleBody = false)
	public void testRoute6(RoutingContext routingContext) {
		HttpServerRequest request = routingContext.request();
		HttpServerResponse response = routingContext.response();
		request.bodyHandler(response::end);
		request.endHandler(v -> response.end());
	}
}
