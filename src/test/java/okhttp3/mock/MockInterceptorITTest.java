package okhttp3.mock;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    public void testGet() throws IOException {
        interceptor.addRule()
                .get(TEST_URL)
                .respond(TEST_RESPONSE);

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test
    public void testURLStartsWith() throws IOException {
        interceptor.addRule()
                .get()
                .urlStarts("https://")
                .respond(TEST_RESPONSE);

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test(expected = AssertionError.class)
    public void testURLStartsWith_Fail() throws IOException {
        interceptor.addRule()
                .get()
                .urlStarts("http://")
                .respond(TEST_RESPONSE)
                .code(401);

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test
    public void testResourceResponse() throws IOException {
        interceptor.addRule()
                .respond(resource("sample.json"));

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute();
    }

    @Test
    public void testCustomResponse() throws IOException {
        final String json = "{\"succeed\":true}";

        interceptor.behavior(Behavior.SEQUENTIAL).addRule()
                .get().or().post().or().put()
                .respond(json, MEDIATYPE_JSON);

        assertEquals(json, client.newCall(new Request.Builder()
                .url(TEST_URL)
                .get()
                .build())
                .execute()
                .body()
                .string());
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongOrSyntax1() {
        interceptor.addRule()
                .or().get()
                .respond(HttpCodes.HTTP_409_CONFLICT);
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongOrSyntax2() {
        interceptor.addRule()
                .get().or().or().post()
                .respond(HttpCodes.HTTP_409_CONFLICT);
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongNotSyntax1() {
        interceptor.addRule()
                .put().not()
                .respond(HttpCodes.HTTP_409_CONFLICT);
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongNotSyntax2() {
        interceptor.addRule()
                .not().not().put()
                .respond(HttpCodes.HTTP_409_CONFLICT);
    }

    @Test(expected = AssertionError.class)
    public void testFailReasonSequential() throws IOException {
        interceptor.behavior(Behavior.SEQUENTIAL)
                .addRule()
                .get().or().post().or().put()
                .respond("OK");

        client.newCall(new Request.Builder()
                .url(TEST_URL)
                .delete()
                .build())
                .execute();
    }

    @Test
    public void testAnswer() throws IOException {
        interceptor.addRule()
                .get()
                .pathMatches(Pattern.compile("/aPath/(\\w+)"))
                .anyTimes()
                .answer(new RuleAnswer() {

                    @Override
                    public Response.Builder respond(Request request) {
                        return new Response.Builder()
                                .code(200)
                                .body(ResponseBody.create(null, request.url().encodedPath()));
                    }

                });

        String[] paths = new String[]{"/aPath/aaa", "/aPath/bbb", "/aPath/ccc"};
        for (String expectedBody : paths) {
            HttpUrl url = HttpUrl.parse(TEST_URL).newBuilder().encodedPath(expectedBody).build();

            String body = client.newCall(new Request.Builder()
                    .url(url)
                    .get()
                    .build())
                    .execute()
                    .body()
                    .string();

            assertEquals(expectedBody, body);
        }
    }

}
