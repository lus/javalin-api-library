package dev.lukaesebrot.jal.ratelimiting;

import io.javalin.http.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This class is used to handle the validation of the amount of requests in a specific time
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class RateLimiter {

    private long allowedPerMinute;
    private Map<String, Long> ipRequestsThisMinute;
    private ScheduledExecutorService clearService;
    private Consumer<Context> onRateLimiting;

    /**
     * Creates a new RateLimiter
     * @param allowedPerMinute The allowed amount of requests per minute per IP
     * @param onRateLimiting Gets accepted if a requester is being rate limited
     */
    public RateLimiter(long allowedPerMinute, Consumer<Context> onRateLimiting) {
        this.allowedPerMinute = allowedPerMinute;
        this.onRateLimiting = onRateLimiting;

        // Initialize the request amount map
        this.ipRequestsThisMinute = new ConcurrentHashMap<>();

        // Initialize the thread which clears the request amount map every minute
        this.clearService = Executors.newSingleThreadScheduledExecutor();
        this.clearService.scheduleAtFixedRate(() -> ipRequestsThisMinute.clear(), 0, 60, TimeUnit.SECONDS);
    }

    /**
     * Increases the amount of executed requests this minute of the given IP
     * @param ctx The request context which holds the IP address
     */
    public void notifyIPRequest(Context ctx) {
        ipRequestsThisMinute.compute(ctx.ip(), (key, amount) -> amount == null ? 1 : amount + 1);
    }

    /**
     * Returns whether or not the given IP is being rate limited and executes the rate limiting consumer if needed
     * @param ctx The request context which holds the IP address
     * @return <code>true</code>, if the IP is allowed to execute the request, <code>false</code> otherwise
     */
    public boolean requestAllowed(Context ctx) {
        boolean allowed = ipRequestsThisMinute.getOrDefault(ctx.ip(), 1L) < allowedPerMinute;
        if (!allowed) onRateLimiting.accept(ctx);
        return allowed;
    }

    /**
     * Interrupts the amount map clearing thread and clears the amount map itself
     */
    public void shutdown() {
        this.clearService.shutdown();
        this.ipRequestsThisMinute.clear();
    }

}
