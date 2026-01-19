package org.sim.umira.handlers;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ConstraintViolationsHandler implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {

        String message = exception.getConstraintViolations()
                .stream()
                .map(v -> {
                    String field = v.getPropertyPath().toString();
                    field = field.substring(field.lastIndexOf('.') + 1);
                    return field + " " + v.getMessage();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation error");

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ResponseHandler.error(message))
                .build();
    }
}
