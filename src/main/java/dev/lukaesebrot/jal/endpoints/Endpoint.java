package dev.lukaesebrot.jal.endpoints;

import dev.lukaesebrot.jal.ratelimiting.RateLimiter;
import io.javalin.http.Context;

/**
 * This class is used to simplify the creation of an endpoint
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Endpoint {

    private RateLimiter rateLimiter;

    /**
     * Injects the current RateLimiter
     *
     * @param rateLimiter The RateLimiter instance
     */
    protected void injectRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * Handles the validation of the request and then calls the final handle method
     *
     * @param ctx The request context
     */
    protected void execute(Context ctx) {
        // Validate the rate limiting
        if (this.rateLimiter != null) {
            if (!this.rateLimiter.requestAllowed(ctx)) return;
            this.rateLimiter.notifyIPRequest(ctx);
        }

        // Proceed to the endpoint handler
        handle(ctx);
    }

    /**
     * Gets called if the rate limiting succeeded
     *
     * @param ctx The request context
     */
    abstract public void handle(Context ctx);

}
