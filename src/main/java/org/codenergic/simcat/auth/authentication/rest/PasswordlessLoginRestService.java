package org.codenergic.simcat.auth.authentication.rest;

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codenergic.simcat.auth.core.auth0.Auth0Service;
import org.codenergic.simcat.auth.core.auth0.Auth0Service.Connection;
import org.codenergic.simcat.chat.core.annotation.WebComponent;
import org.codenergic.simcat.core.web.Validator;
import org.springframework.beans.factory.annotation.Autowired;

@WebComponent
@Path("/api/auth/passwordless")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PasswordlessLoginRestService {
	@Autowired
	private Auth0Service auth0Service;

	@POST
	public void sendVerificationCode(PasswordlessLoginRestData data, @PathParam("conn") @DefaultValue("SMS") Connection conn,
			@Suspended AsyncResponse response) {
		Validator.isNotBlank(data.getUsername(), "username property must be provided");
		Validator.isNotNull(data.getConnection(), "connection property must be provided");
		auth0Service.sendVerificationCode(data.getUsername(), data.getConnection(), h -> {
			if (h.succeeded())
				response.resume(data);
			else
				response.resume(Response
						.status(Status.BAD_REQUEST)
						.entity(Arrays.asList(h.cause().getMessage()))
						.build());
		});
	}
}
