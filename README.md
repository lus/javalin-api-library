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
        RateLimiter rateLimiter = new RateLimiter(allowedRequestsPerMinute, ctx -> ctx.status(HttpStatus.TOO_MANY_REQUESTS_429));
        
        // 3.): Register a new HttpServer
        HttpServer server = new HttpServer(app, rateLimiter);

        // 4.): Register the '/test' endpoint
        server.endpoint("/test", HandlerType.GET, new TestEndpoint());
    }

}

class TestEndpoint extends Endpoint {

    @Override
    public void handle(Context ctx) {
        ctx.status(HttpStatus.OK_200).result("Hello, world!");
    }

}
```


## Using the response builder
```java
public class Example {

    public static void main(String[] args){
        // 1.): Start a new Javalin instance
        Javalin app = Javalin.create().start(7700);

        // 2.): Register a new RateLimiter instance (optional)
        long allowedRequestsPerMinute = 60;
        RateLimiter rateLimiter = new RateLimiter(allowedRequestsPerMinute, ctx -> ctx.status(HttpStatus.TOO_MANY_REQUESTS_429));
        
        // 3.): Register a new HttpServer
        HttpServer server = new HttpServer(app, rateLimiter);

        // 4.): Register the '/test' endpoint
        server.endpoint("/test", HandlerType.GET, new TestEndpoint());
    }

}

class TestEndpoint extends Endpoint {

    @Override
    public void handle(Context ctx) {
        String response = new ResponseBuilder(HttpStatus.OK_200)
            .withResponseType(ResponseType.SUCCESS)
            .addData("message", "Hello, world!")
            .toJson();
        ctx.status(HttpStatus.OK_200).result(response);
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