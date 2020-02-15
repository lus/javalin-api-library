package dev.lukaesebrot.jal.endpoints;

import dev.lukaesebrot.jal.ratelimiting.RateLimiter;
import dev.lukaesebrot.jal.responses.ResponseBuilder;
import dev.lukaesebrot.jal.responses.ResponseType;
import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.eclipse.jetty.http.HttpStatus;

import java.util.Set;

/**
 * This class is used to simplify the creation of an endpoint
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Endpoint {

    private RateLimiter rateLimiter;

    /**
     * Creates a new endpoint
     * @param app The Javalin app
     * @param type The type of the endpoint handler
     * @param path The path of the endpoint
     */
    public Endpoint(Javalin app, HandlerType type, String path) {
        this(app, type, path, null, null);
    }

    /**
     * Creates a new endpoint
     * @param app The Javalin app
     * @param type The type of the endpoint handler
     * @param path The path of the endpoint
     * @param rateLimiter A RateLimiter object
     * @see RateLimiter
     */
    public Endpoint(Javalin app, HandlerType type, String path, RateLimiter rateLimiter) {
        this(app, type, path, rateLimiter, null);
    }

    /**
     * Creates a new endpoint
     * @param app The Javalin app
     * @param type The type of the endpoint handler
     * @param path The path of the endpoint
     * @param roles A set of permitted roles
     */
    public Endpoint(Javalin app, HandlerType type, String path, Set<Role> roles) {
        this(app, type, path, null, roles);
    }

    /**
     * Creates a new endpoint
     * @param app The Javalin app
     * @param type The type of the endpoint handler
     * @param path The path of the endpoint
     * @param rateLimiter A RateLimiter object
     * @param roles A set of permitted roles
     * @see RateLimiter
     */
    public Endpoint(Javalin app, HandlerType type, String path, RateLimiter rateLimiter, Set<Role> roles) {
        // Register the corresponding Javalin endpoint
        if (roles == null) app.addHandler(type, path, this::execute);
        if (roles != null) app.addHandler(type, path, this::execute, roles);

        // Define the current RateLimiter
        this.rateLimiter = rateLimiter;
    }

    /**
     * Handles the validation of the request and then calls the final handle method
     * @param ctx The request context
     */
    private void execute(Context ctx) {
        // Validate the rate limiting
        if (this.rateLimiter != null) {
            if (!this.rateLimiter.requestAllowed(ctx)) {
                onRateLimiting(ctx);
                return;
            }
            this.rateLimiter.notifyIPRequest(ctx);
        }

        // Proceed to the endpoint handler
        handle(ctx);
    }

    /**
     * Gets called if the rate limiting succeeded
     * @param ctx The request context
     */
    abstract public void handle(Context ctx);

    /**
     * Gets called if the corresponding IP is being rate limited
     * @param ctx The request context
     */
    public void onRateLimiting(Context ctx) {
        String response = new ResponseBuilder(HttpStatus.TOO_MANY_REQUESTS_429)
                .withResponseType(ResponseType.ERROR)
                .addData("message", "You are being rate limited!")
                .toJson();
        ctx.status(HttpStatus.TOO_MANY_REQUESTS_429).result(response);
    }

}
