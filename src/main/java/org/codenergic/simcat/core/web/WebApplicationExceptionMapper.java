package org.codenergic.simcat.core.web;

import java.util.Arrays;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codenergic.simcat.chat.core.annotation.WebComponent;

@Provider
@WebComponent
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException exception) {
		return Response.fromResponse(exception.getResponse())
				.entity(Arrays.asList(exception.getMessage()))
				.build();
	}

}
