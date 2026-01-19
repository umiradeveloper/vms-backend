package org.sim.umira.handlers;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = 500;
        String message = "Internal Server Error";
        System.out.println(exception);
        if (exception instanceof WebApplicationException webEx) {
            status = webEx.getResponse().getStatus();
            message = webEx.getMessage();
            // System.out.println(webEx.getLocalizedMessage());
        }else if (exception instanceof ConstraintViolationException ve) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
            message = ve.getConstraintViolations()
                    .stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Bad Request");

        }
        exception.printStackTrace();
        System.out.println(exception);
        return Response
                .status(status)
                .entity(ResponseHandler.error(message))
                .build();
    }
}
