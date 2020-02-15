# javalin-api-library

A simple to use library for creating APIs using Javalin

## How to implement?
Maven:
```xml
coming soon
```

**Information:** The maven project already builds the following dependencies:
```
- org.slf4j.slf4j-simple [1.7.28]
- io.javalin.javalin [3.7.0]
- com.google.code.gson.gson [2.8.6]
```
You don't have to implement them in your project.


## Registering endpoints
```java
public class Example {

    public static void main(String[] args){
        // 1.): Start a new Javalin instance
        Javalin app = Javalin.create().start(7700);

        // 2.): Register a new RateLimiter instance (optional)
        long allowedRequestsPerMinute = 60;
        RateLimiter rateLimiter = new RateLimiter(allowedRequestsPerMinute);
        
        // 3.): Register the TestEndpoint
        new TestEndpoint(app, rateLimiter);
    }

}

public class TestEndpoint extends Endpoint {

    public TestEndpoint(Javalin app, RateLimiter rateLimiter) {
        super(app, HandlerType.GET, "/test", rateLimiter);
    }

    @Override
    public void handle(Context ctx) {
        ctx.result("Hello, world.");
    }

    @Override
    public void onRateLimiting(Context ctx) {
        ctx.result("You are being rate limited!");
    }

}
```


## Using the response builder
```java
import dev.lukaesebrot.jal.responses.ResponseBuilder;import dev.lukaesebrot.jal.responses.ResponseType;import org.eclipse.jetty.http.HttpStatus;public class Example {
    public static void main(String[] args){
        // 1.): Start a new Javalin instance
        Javalin app = Javalin.create().start(7700);

        // 2.): Register a new RateLimiter instance (optional)
        long allowedRequestsPerMinute = 60;
        RateLimiter rateLimiter = new RateLimiter(allowedRequestsPerMinute);
        
        // 3.): Register the TestEndpoint
        new TestEndpoint(app, rateLimiter);
    }

}

public class TestEndpoint extends Endpoint {

    public TestEndpoint(Javalin app, RateLimiter rateLimiter) {
        super(app, HandlerType.GET, "/test", rateLimiter);
    }

    @Override
    public void handle(Context ctx) {
        String response = new ResponseBuilder(HttpStatus.OK_200)
            .withResponseType(ResponseType.SUCCESS)
            .addData("message", "Hello, world!")
            .toJson();
        ctx.status(HttpStatus.OK_200).result(response);
    }

    @Override
    public void onRateLimiting(Context ctx) {
        ctx.result("You are being rate limited!");
    }

}
```

The response will look like this:
```json
{
  "status": 200,
  "type": "success",
  "data": {
    "message": "Hello, world!"
  }
}
```