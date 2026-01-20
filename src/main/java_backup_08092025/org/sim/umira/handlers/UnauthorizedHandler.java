package org.sim.umira.handlers;

import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedHandler implements ExceptionMapper<UnauthorizedException> {
    
    @Override
    public Response toResponse(UnauthorizedException exception) {
        System.out.println(exception);

        return Response.status(Response.Status.UNAUTHORIZED)
                       .type(MediaType.APPLICATION_JSON)
                       .entity(ResponseHandler.error("Unauthorized"))
                       .build();
    }
}
