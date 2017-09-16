package okhttp3.mock;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static okhttp3.mock.ClasspathResources.resource;
import static okhttp3.mock.MediaTypes.MEDIATYPE_JSON;
import static org.junit.Assert.assertEquals;

public class MockInterceptorITTest {
    private static final String TEST_URL = "https://api.github.com/users/gmazzo";
    private static final String TEST_RESPONSE = "good!";
    private MockInterceptor interceptor;
    private OkHttpClient client;

    @Before
    public void setup() {
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor = new MockInterceptor(Behavior.UNORDERED))
                .build();
    }

    @Test
    public void testURLStartsWith() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .get()
                .urlStarts("https://")
                .respond(TEST_RESPONSE));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test(expected = AssertionError.class)
    public void testURLStartsWith_Fail() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .get()
                .urlStarts("http://")
                .respond(TEST_RESPONSE)
                .code(401));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test
    public void testResourceResponse() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .respond(resource("sample.json")));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test
    public void testCustomResponse() throws IOException {
        final String json = "{\"succeed\":true}";

        interceptor.behavior(Behavior.SEQUENTIAL).addRule(new Rule.Builder()
                .get().or().post().or().put()
                .respond(json, MEDIATYPE_JSON));

        assertEquals(json, client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute()
                .body()
                .string());
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongOrSyntax1() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .or().get()
                .respond(HttpCodes.HTTP_409_CONFLICT));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongOrSyntax2() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .get().or().or().post()
                .respond(HttpCodes.HTTP_409_CONFLICT));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongNotSyntax1() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .put().not()
                .respond(HttpCodes.HTTP_409_CONFLICT));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongNotSyntax2() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .not().not().put()
                .respond(HttpCodes.HTTP_409_CONFLICT));
    }

    @Test(expected = AssertionError.class)
    public void testFailReasonSequential() throws IOException {
        interceptor.behavior(Behavior.SEQUENTIAL)
                .addRule(new Rule.Builder()
                        .get().or().post().or().put()
                        .respond("OK"));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .delete()
                .build())
                .execute();
    }

}
