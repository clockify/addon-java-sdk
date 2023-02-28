Addon SDK is a framework that is written in Java and offers abstractions to get up and running with the development of addons for the CAKE.com marketplace.

The SDK is intended to be easy to use and intuitive.

More features are planned and will be added to the SDK.

## Project
The project is organized in two modules:
- the annotation processor
- the addon SDK

## Annotation processor
The annotation processor module is only used at build-time. It generates helper interfaces based on the manifest schema.

Clockify manifest components make use of the ```@ExtendClockifyManifest(definition = )``` annotation in order to let the annotation processor know
that it should generate interfaces for this class's builder according the specified definition.

For instance, suppose we have the following definition inside the json schema:
```json
"definitions": {
    "lifecycle": {
      "properties": {
        "path": {...},
        "type": {
          "enum": [
            "INSTALLED",
            "DELETED",
            "SETTINGS_UPDATED"
          ]
        }
      },
      "required": [...]
    }
    ...
}
```
By annotating the ClockifyLifecycleEvent class with:
```java
@ExtendClockifyManifest(definition = "lifecycle")
public class ClockifyLifecycleEvent extends LifecycleEvent
```

The annotation processor will generate the following interfaces:
```java
public interface ClockifyLifecycleEventBuilderPathStep {
    ClockifyLifecycleEventBuilderTypeStep path(String value);
}

public interface ClockifyLifecycleEventBuilderTypeStep {
    ClockifyLifecycleEventBuilderBuildStep type(String value);

    default ClockifyLifecycleEventBuilderBuildStep onInstalled() {
        return type ("INSTALLED");
    }

    default ClockifyLifecycleEventBuilderBuildStep onDeleted() {
        return type ("DELETED");
    }

    default ClockifyLifecycleEventBuilderBuildStep onSettingsUpdated() {
        return type ("SETTINGS_UPDATED");
    }
}

public interface ClockifyLifecycleEventBuilderBuildStep {
    ClockifyLifecycleEvent build();
}
```

The above interfaces, combined with a Lombok builder result in a straightforward step-builder implementation.
```java
@ExtendClockifyManifest(definition = "lifecycle")
public class ClockifyLifecycleEvent extends LifecycleEvent {

    @Builder
    protected ClockifyLifecycleEvent(@NonNull String type, @NonNull String path) {
        super(type, path);
    }

    public static ClockifyLifecycleEventBuilderPathStep builder() {
        return new ClockifyLifecycleEventBuilder();
    }

    private static class ClockifyLifecycleEventBuilder implements
            ClockifyLifecycleEventBuilderTypeStep,
            ClockifyLifecycleEventBuilderPathStep,
            ClockifyLifecycleEventBuilderBuildStep {
    }
}
```

Each required property will have its own step, and each step will only be able to set one property.
```java
ClockifyLifecycleEvent lifecycle = ClockifyLifecycleEvent
                .builder()
                .path("/lifecycle")
                .onInstalled()
                .build();
```


## Addon SDK
The SDK is structured into two parts, the shared codebase and the product-specific codebase:
- shared
- clockify, pumble, plaky

The product-specific layers will allow for more granular configuration and validation as well as provide specific helper classes.

### Features
- Predefined POJO models
- Product specific validation & helpers
- Centralized definition and handling of all the components of the addon
- Easy to get started by either relying on the embedded webserver or serving the provided servlet class through a web framework
- Middleware support

### Getting started
First, define an addon descriptor:
```java
AddonDescriptor descriptor = new AddonDescriptor("key", "name", "description", "baseUrl");
```
The baseUrl must be a full URI, with the scheme, host and path.

Then, create an addon instance:
```java
ClockifyAddon clockifyAddon = new ClockifyAddon(descriptor);
```

Finally, register any webhook / lifecycle / component / setting or custom HTTP handler on the addon:

Each object has its own builder class which by using the step builder pattern guides through the instantiation of the object and makes it easy to distinguish between required and optional properties.
```java
 ClockifyComponent component = ClockifyComponent
        .builder()
        .activityTab()
        .allowAdmins()
        .path("/component")
        .options(Map.of("opt1", "val1"))
        .build();

ClockifySetting number = ClockifySetting.builder()
        .id("id")
        .name("name")
        .asNumber()
        .value(12)
        .build();

ClockifySetting multipleSetting = ClockifySetting.builder()
        .id("id")
        .name("name")
        .asDropdownMultiple()
        .value(List.of("1", "2"))
        .allowedValues(List.of("1", "2", "3"))
        .build();


RequestHandler<HttpRequest> handler = new RequestHandler() { ... }
clockifyAddon.registerComponent(component, handler);
...
```

A handler receiving a custom subclass of HttpRequest can also be used - a middleware adapter must be implemented in this case.
```java
Middleware middleware = ...; // middleware that takes HttpRequest, adapts it to CustomHttpRequest and proceeds with the chain
clockifyAddon.useMiddleware(middleware);

RequestHandler<CustomHttpRequest> handler = new RequestHandler() { ... }
clockifyAddon.registerComponent(component, handler);
```

### Serving the addon
#### Using the embedded jetty server
```java
AddonServlet servlet = new AddonServlet(clockifyAddon);
EmbeddedServer server = new EmbeddedServer(servlet);
server.start(port);
```
#### Serving via spring boot (or any other framework)

```java
import addonsdk.shared.Addon;
import addonsdk.shared.AddonServlet;

@WebServlet(...)
public class AppServlet extends AddonServlet {

    public AppServlet(Addon addon) {
        super(addon);
    }
}
```

## Documentation
### Manifest
The central part of an addon is its manifest. The manifest defines the basic information related to the addon, as well as the components and the routes that it supports.

A handler is automatically registered on the GET '/manifest' path when an addon object is instantiated.

When invoked, this handler will return a JSON containing the serialized manifest.

### Handlers
The following arguments are required when registering a handler:
- the HTTP method
- the relative path
- the handler itself

If the handler is being registered through either a component / lifecycle / webhook, the inferred HTTP method will depend on the type of the object itself.
- component -> GET
- lifecycle -> POST
- webhook -> POST

Each addon implementation will only accept its own component / lifecycle / webhook subclasses.

### Request & Response objects
The RequestHandlers accept subclasses of HttpRequest as arguments and return HttpResponse responses.
These classes serve as basic data objects, and are used to decouple the handlers from the servlet classes.

If the RequestHandler accepts a subclass of HttpRequest, an unsafe cast will be performed.
The HttpRequest should be adapted to the custom subclass through a middleware prior to the handler being called.

### Middlewares
Middlewares accept HttpRequest and MiddlewareChain arguments and return HttpResponse responses.

They can be used to:
- intercept requests
- implement authentication mechanism
- decorate requests
- adapt requests to custom request subclasses
- etc

Every middleware will be invoked once per request, in the order it was registered.

To register a middleware, simply call the 'useMiddleware' method on the addon:
```java
clockifyAddon.useMiddleware(middleware);
```