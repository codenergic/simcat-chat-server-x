package org.codenergic.simcat.auth.user.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codenergic.simcat.Simcat;
import org.codenergic.simcat.auth.user.AsyncUserService;
import org.codenergic.simcat.chat.core.annotation.WebComponent;
import org.codenergic.simcat.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

@WebComponent
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Profile({ Simcat.PROFILE_DEVELOPMENT, Simcat.PROFILE_PRODUCTION })
public class UserRestService {
	@Autowired
	private AsyncUserService userService;

	@GET
	public void findAllUsers(@QueryParam("limit") @DefaultValue("10") int limit, @Suspended AsyncResponse response) {
		userService.findAllUsers(limit, h -> {
			List<UserRestData> userDatas = h.result().stream()
					.map(UserRestData::new)
					.collect(Collectors.toList());
			response.resume(userDatas);
		});
	}

	@GET
	@Path("/{id}")
	public void getUserById(@PathParam("id") Long id, @Suspended AsyncResponse response) {
		userService.getUserById(id, h -> {
			if (h.succeeded())
				response.resume(new UserRestData(h.result()));
			else if (h.cause() instanceof IllegalArgumentException)
				response.resume(Response.status(Status.BAD_REQUEST).build());
			else
				response.resume(Response.serverError().entity(h.cause()).build());
		});
	}

	@POST
	public void saveUser(UserRestData userData, @Suspended AsyncResponse response) {
		User user = new User.Builder()
				.username(userData.getUsername())
				.password(userData.getPassword())
				.build();
		userService.saveUser(user, h -> response.resume(new UserRestData(h.result())));
	}

	@PUT
	@Path("/{id}")
	public void updateUser(@PathParam("id") Long id, UserRestData userData, @Suspended AsyncResponse response) {
		User user = new User.Builder()
				.username(userData.getUsername())
				.password(userData.getPassword())
				.build();
		userService.udpateUser(id, user, h -> {
			if (h.succeeded())
				response.resume(new UserRestData(h.result()));
			else if (h.cause() instanceof IllegalArgumentException)
				response.resume(Response.status(Status.BAD_REQUEST).build());
			else
				response.resume(Response.serverError().entity(h.cause()).build());
		});
	}
}
