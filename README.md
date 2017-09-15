# okhttp-client-mock
A simple OKHttp client mock, using a programmable request interceptor

## Import
On your `build.gradle` add:
```groovy
dependencies {
    testCompile 'com.github.gmazzo:okhttp-mock:0.5'
}
```
[![Download](https://api.bintray.com/packages/gmazzo/maven/okhttp-client-mock/images/download.svg) ](https://bintray.com/gmazzo/maven/okhttp-client-mock/_latestVersion)
## Usage
Create an OkHttp request interceptor and record some rules:
```java
MockInterceptor interceptor = new MockInterceptor();

interceptor.addRule(new Rule.Builder()
        .isPOST()
        .urlIs("https://testserver/api/login")
        .andRespond(401));

interceptor.addRule(new Rule.Builder()
        .isGET()
        .urlIs("https://testserver/api/json")
        .andRespond("{succeed:true}"));

interceptor.addRule(new Rule.Builder()
        .isGET()
        .urlIs("https://testserver/api/json")
        .andRespond(resource("sample.json")));
```

Then add the interceptor to your OkHttpClient client and use it as usual:
```java
OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
```

See an example [Integration Test](src/test/java/okhttp3/m#ock/MockInterceptorITTest.java) with mocked HTTP responses

You can use the following helper classes to provide mock responses from resources:
- `ClasspathResources.resource` to load content from classpath
- `AndroidResources.asset` to load content from an Android's asset
- `AndroidResources.raw` to load content from an Android's raw resource
- `RoboResources.asset` and `RoboResources.raw` if you are running *Roboelectric* tests
