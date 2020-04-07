package dev.lukaesebrot.jal.responses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is used to build a json response
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.1
 * @since 1.0.0
 */
public class ResponseBuilder {

    private int statusCode;
    private ResponseType type;
    private Map<String, Object> data;
    private Object entity;

    /**
     * Creates a new response builder
     *
     * @param statusCode The status code of the response
     */
    public ResponseBuilder(int statusCode) {
        this.statusCode = statusCode;
        this.type = ResponseType.SUCCESS;
        this.data = new LinkedHashMap<>();
    }

    /**
     * Defines the response type
     *
     * @param type The response type
     * @return The new ResponseBuilder state
     */
    public ResponseBuilder withResponseType(ResponseType type) {
        this.type = type;
        return this;
    }

    /**
     * Adds a new entry to the data map
     *
     * @param key   The data key
     * @param value The data itself
     * @return The new ResponseBuilder state
     */
    public ResponseBuilder addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * Defines a raw entity to use for the data field
     *
     * @param entity The object to use
     * @return The new ResponseBuilder state
     */
    public ResponseBuilder entity(Object entity) {
        this.entity = entity;
        return this;
    }

    /**
     * Parses the current response state to a json string
     *
     * @return The parsed json string
     */
    public String toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("status", statusCode);
        object.addProperty("type", type.toString().toLowerCase());
        object.add("data", new Gson().toJsonTree(entity == null ? data : entity));

        return object.toString();
    }

    /**
     * Sends the set status code and the set response to the context.
     *
     * @param ctx The context to respond to
     */
    public void respondTo(Context ctx) {
        ctx.status(statusCode).result(toJson());
    }

    public static String ok() {
        return new ResponseBuilder(HttpStatus.OK_200)
                .withResponseType(ResponseType.SUCCESS)
                .toJson();
    }

    public static String error(int errorCode, String errorMessage) {
        return new ResponseBuilder(errorCode)
                .withResponseType(ResponseType.ERROR)
                .addData("message", errorMessage)
                .toJson();
    }

    public static String tooManyRequests() {
        return error(HttpStatus.TOO_MANY_REQUESTS_429, "You are being rate limited!");
    }

    public static String internalServerError(String errorMessage) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR_500, errorMessage);
    }

}
