package dev.lukaesebrot.jal.endpoints;

import dev.lukaesebrot.jal.ratelimiting.RateLimiter;
import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.HandlerType;

import java.util.Set;

/**
 * This class is used to handle endpoint registrations
 */
public class HttpServer {

    private Javalin app;
    private RateLimiter rateLimiter;

    /**
     * Creates a new HttpServer instance without a {@link RateLimiter}
     *
     * @param app The Javalin app instance
     */
    public HttpServer(Javalin app) {
        this(app, null);
    }

    /**
     * Creates a new HttpServer instance with a {@link RateLimiter}
     *
     * @param app         The Javalin app instance
     * @param rateLimiter The RateLimiter instance
     */
    public HttpServer(Javalin app, RateLimiter rateLimiter) {
        this.app = app;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Registers a new {@link Endpoint}
     *
     * @param path     The path of the endpoint
     * @param type     The handler type
     * @param endpoint The endpoint object
     */
    public void endpoint(String path, HandlerType type, Endpoint endpoint) {
        endpoint.injectRateLimiter(this.rateLimiter);
        app.addHandler(type, path, endpoint::execute);
    }

    /**
     * Registers a new {@link Endpoint}
     *
     * @param path           The path of the endpoint
     * @param type           The handler type
     * @param endpoint       The endpoint object
     * @param permittedRoles A set of permitted roles
     */
    public void endpoint(String path, HandlerType type, Endpoint endpoint, Set<Role> permittedRoles) {
        endpoint.injectRateLimiter(this.rateLimiter);
        app.addHandler(type, path, endpoint::execute, permittedRoles);
    }

}
