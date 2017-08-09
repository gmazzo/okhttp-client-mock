# okhttp-client-mock
A simple OKHttp client mock, using a programmable request interceptor

## Import
On your `build.gradle` add:
```groovy
dependencies {
    testCompile 'com.github.gmazzo:okhttp-mock:0.1'
}
```
[![Download](https://api.bintray.com/packages/gmazzo/maven/okhttp-client-mock/images/download.svg) ](https://bintray.com/gmazzo/maven/okhttp-client-mock/_latestVersion)
## Usage
Create an OkHttp request interceptor and record some rules:
```java
MockInterceptor interceptor = new MockInterceptor();
interceptor.addRule(new Rule.Builder()
        .isGET()
        .urlIs("https://testserver/api/json")
        .mediaType("appplication/json")
        .andRespond("{succeed:true}"));
interceptor.addRule(new Rule.Builder()
        .isPOST()
        .urlIs("https://testserver/api/login")
        .responseCode(401)
        .andRespond("invalid user!"));
```

Then add the interceptor to your OkHttpClient client and use it as usual:
```java
OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
```

See an example [Integration Test](src/test/java/okhttp3/mock/MockInterceptorITTest.java) with mocked HTTP responses
