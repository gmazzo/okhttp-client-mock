# okhttp-client-mock
A simple OKHttp client mock, using a programmable request interceptor

## Import
On your `build.gradle` add:
```groovy
dependencies {
    testCompile 'com.github.gmazzo:okhttp-mock:1.0.3'
}
```
[![Download](https://api.bintray.com/packages/gmazzo/maven/okhttp-client-mock/images/download.svg) ](https://bintray.com/gmazzo/maven/okhttp-client-mock/_latestVersion)
## Usage
Create an OkHttp request interceptor and record some rules, for example:
```java
MockInterceptor interceptor = new MockInterceptor();

interceptor.addRule(new Rule.Builder()
        .get().or().post().or().put()
        .url("https://testserver/api/login")
        .respond(HTTP_401_UNAUTHORIZED))
        .header("WWW-Authenticate", "Basic");

interceptor.addRule(new Rule.Builder()
        .get()
        .url("https://testserver/api/json")
        .respond("{succeed:true}", MEDIATYPE_JSON));

interceptor.addRule(new Rule.Builder()
        .get()
        .url("https://testserver/api/json")
        .respond(resource("sample.json"), MEDIATYPE_JSON));
```

Then add the interceptor to your OkHttpClient client and use it as usual:
```java
OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
```

Check an example [Integration Test](src/test/java/okhttp3/m#ock/MockInterceptorITTest.java) with mocked HTTP responses

You can use the following helper classes to provide mock responses from resources:
- `ClasspathResources.resource` to load content from classpath
- `AndroidResources.asset` to load content from an Android's asset
- `AndroidResources.rawRes` to load content from an Android's raw resource
- `RoboResources.asset` and `RoboResources.rawRes` if you are running *Roboelectric* tests
