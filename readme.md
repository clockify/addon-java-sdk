Addon SDK is a framework that is written in Java and offers abstractions to get up and running with the development of addons for the CAKE.com marketplace.

The SDK is intended to be easy to use and intuitive.

More features are planned and will be added to the SDK.

## Installation
Maven must be configured as described <a href="https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages">here</a>
before the Addons SDK can be installed.

Once you have configured the required settings, you can import the Addons SDK into your project by adding
the following dependency to your pom.xml file:

```
<dependency>
    <groupId>com.cake.clockify</groupId>
    <artifactId>addon-sdk</artifactId>
    <version>${addonssdk.version}</version>
</dependency>
```

## Project
The project is organized in two modules:
- the annotation processor
- the addon SDK

## Annotation processor
The annotation processor module is only used at build-time. It generates helper interfaces based on the manifest schema.

Clockify manifest makes use of the ```@ExtendClockifyManifest``` annotation in order to let the annotation processor know
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

The above interfaces will result in a straightforward step-builder implementation.

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

### Getting started
First, create an addon instance:
```java
ClockifyAddon clockifyAddon = new ClockifyAddon(
        ClockifyManifest.v1_2Builder()
        .key(key)
        .name(name)
        .baseUrl(baseUrl)
        .requireBasicPlan()
        .scopes(List.of(
            ClockifyScope.PROJECT_READ,
            ClockifyScope.PROJECT_WRITE
        ))
        .build()
        );
```

The baseUrl must be a full URI, with the scheme, host and path.

Then, register any webhook / lifecycle / component / setting or custom endpoints on the addon.

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

Addon-specific filters can be registered for requests that will be handled through the addon's servlet.
```java
Filter filter = ...; // servlet filter
clockifyAddon.addFilter(filter);
```

### Validating Clockify tokens
[https://dev-docs.marketplace.cake.com/development-toolkit/authentication-and-authorization/](https://dev-docs.marketplace.cake.com/development-toolkit/authentication-and-authorization/)

ClockifySignatureParser can be used to verify that the received tokens have been signed by Clockify:
```java
RSAPublicKey publicKey = ...;
ClockifySignatureParser parser = new ClockifySignatureParser("{manifest-key}", publicKey);

String token = ...;
Map<String, Object> claims = parser.parseClaims(token);
String workspaceId = (String) claims.get(ClockifySignatureParser.CLAIM_WORKSPACE_ID);
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