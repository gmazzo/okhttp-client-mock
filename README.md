# okhttp-client-mock
A simple OKHttp client mock, using a programmable request interceptor

[![Download](https://api.bintray.com/packages/gmazzo/maven/okhttp-client-mock/images/download.svg) ](https://bintray.com/gmazzo/maven/okhttp-client-mock/_latestVersion)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-OKHttp%20client%20mock-green.svg?style=flat )](https://android-arsenal.com/details/1/6763)
[![Build Status](https://travis-ci.com/gmazzo/okhttp-client-mock.svg?branch=master)](https://travis-ci.com/gmazzo/okhttp-client-mock)
[![codecov](https://codecov.io/gh/gmazzo/okhttp-client-mock/branch/master/graph/badge.svg)](https://codecov.io/gh/gmazzo/okhttp-client-mock)

## Import
On your `build.gradle` add:
```groovy
dependencies {
    testImplementation 'com.github.gmazzo:okhttp-mock:<version>'
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

}
```
Or in Java:
```java
MockInterceptor interceptor = new MockInterceptor();

interceptor.addRule()
        .get().or().post().or().put()
        .url("https://testserver/api/login")
        .respond(HTTP_401_UNAUTHORIZED))
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

Check an example [Integration Test](src/test/java/okhttp3/mock/MockInterceptorITTest.java) with mocked HTTP responses

You can use the following helper classes to provide mock responses from resources:
- `ClasspathResources.resource` to load content from classpath
- `AndroidResources.asset` to load content from an Android's asset
- `AndroidResources.rawRes` to load content from an Android's raw resource
- `RoboResources.asset` and `RoboResources.rawRes` if you are running [Roboelectric](https://github.com/robolectric/robolectric) tests
