package okhttp3.mock;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.Assert.assertEquals;

public class MockInterceptorITTest {
    private static final String TEST_URL = "https://api.github.com/users/gmazzo";
    private static final String TEST_RESPONSE = "good!";
    private MockInterceptor interceptor;
    private OkHttpClient client;

    @Before
    public void setup() {
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor = new MockInterceptor(Behavior.STRICT))
                .build();
    }

    @Test
    public void testURLStartsWith() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .isGET()
                .urlStarts("https://")
                .andRespond(TEST_RESPONSE));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test(expected = AssertionError.class)
    public void testURLStartsWith_Fail() throws IOException {
        interceptor.addRule(new Rule.Builder()
                .isGET()
                .urlStarts("http://")
                .andRespond(401, TEST_RESPONSE));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test
    public void testCustomResponse() throws IOException {
        final String json = "{\"succeed\":true}";

        interceptor.addRule(new Rule.Builder()
                .andRespond(new Response.Builder()
                        .code(200)
                        .body(ResponseBody.create(MediaType.parse("application/json"), json))));

        assertEquals(json, client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute()
                .body()
                .string());
    }

}
