# okhttp-client-mock

A simple OKHttp client mock, using a programmable request interceptor

![GitHub](https://img.shields.io/github/license/gmazzo/okhttp-client-mock)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.gmazzo.okhttp.mock/mock-client)](https://central.sonatype.com/artifact/com.github.gmazzo.okhttp.mock/mock-client)
[![Build Status](https://github.com/gmazzo/okhttp-client-mock/actions/workflows/ci-cd.yaml/badge.svg)](https://github.com/gmazzo/okhttp-client-mock/actions/workflows/ci-cd.yaml)
[![codecov](https://codecov.io/gh/gmazzo/okhttp-client-mock/branch/master/graph/badge.svg)](https://codecov.io/gh/gmazzo/okhttp-client-mock)
[![Users](https://img.shields.io/badge/users_by-Sourcegraph-purple)](https://sourcegraph.com/search?q=content:okhttp-mock\b+content:com.github.gmazzo.okhttp+-repo:github.com/gmazzo/okhttp-client-mock&patternType=regexp)

[![Contributors](https://contrib.rocks/image?repo=gmazzo/okhttp-client-mock)](https://github.com/gmazzo/okhttp-client-mock/graphs/contributors)

## Import

On your `build.gradle` add:

```groovy
dependencies {
    testImplementation 'com.github.gmazzo.okhttp.mock:mock-client:<version>'
}
```

## Usage

Create an OkHttp request interceptor and record some rules, for instance:

```kotlin
val interceptor = MockInterceptor().apply {

    rule(get or post or put, url eq "https://testserver/api/login") {
        respond(HTTP_401_UNAUTHORIZED).header("WWW-Authenticate", "Basic")
    }

    rule(url eq "https://testserver/api/json") {
        respond("{succeed:true}", MEDIATYPE_JSON)
    }

    rule(url eq "https://testserver/api/json") {
        respond(resource("sample.json"), MEDIATYPE_JSON)
    }

    rule(path matches "/aPath/(\\w+)".toRegex(), times = anyTimes) {
        respond { body("Path was " + it.url().encodedPath()) }
    }

    rule(delete) {
        respond(code = HTTP_405_METHOD_NOT_ALLOWED) {
            body("{succeed:false}", MEDIATYPE_JSON)
        }
    }

    // throw an exception
    rule(get) {
        respond { throw IllegalStateException("an IO error") }
    }

}
```

Or in Java:

```java
MockInterceptor interceptor = new MockInterceptor();

interceptor.addRule()
        .get().or().post().or().put()
        .url("https://testserver/api/login")
        .respond(HTTP_401_UNAUTHORIZED)
        .header("WWW-Authenticate", "Basic");

interceptor.addRule()
        .get("https://testserver/api/json")
        .respond("{succeed:true}", MEDIATYPE_JSON);

interceptor.addRule()
        .get("https://testserver/api/json")
        .respond(resource("sample.json"), MEDIATYPE_JSON);

interceptor.addRule()
        .pathMatches(Pattern.compile("/aPath/(\\w+)"))
        .anyTimes()
        .answer(request -> new Response.Builder()
            .code(200)
            .body(ResponseBody.create(null, "Path was " + request.url().encodedPath())));
```

Then add the interceptor to your OkHttpClient client and use it as usual:

```java
OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
```

Check an example [Integration Test](/library/src/test/java/okhttp3/mock/MockInterceptorITTest.java) with mocked HTTP
responses

You can use the following helper classes to provide mock responses from resources:

- `ClasspathResources.resource` to load content from classpath
- `AndroidResources.asset` to load content from an Android's asset
- `AndroidResources.rawRes` to load content from an Android's raw resource
- `RoboResources.asset` and `RoboResources.rawRes` if you are
  running [Roboelectric](https://github.com/robolectric/robolectric) tests
