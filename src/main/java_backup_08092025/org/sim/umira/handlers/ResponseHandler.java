package org.sim.umira.handlers;

public class ResponseHandler<T> {
    public boolean success;
    public String message;
    public T data;

    public ResponseHandler(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseHandler<T> ok(String message, T data) {
        return new ResponseHandler<>(true, message, data);
    }

    public static <T> ResponseHandler<T> error(String message) {
        return new ResponseHandler<>(false, message, null);
    }
}
