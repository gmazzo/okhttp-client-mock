package okhttp3.mock

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mock.ClasspathResources.resource
import okhttp3.mock.MediaTypes.MEDIATYPE_JSON
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalStateException

class MockInterceptorKotlinITTest {
    private val interceptor by lazy { MockInterceptor(Behavior.UNORDERED) }
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Test
    fun testGet() {
        interceptor.rule(get, url eq TEST_URL) { respond(TEST_RESPONSE) }

        client.newCall(
            Request.Builder()
                .url(TEST_URL)
                .get()
                .build()
        )
            .execute()
    }

    @Test
    fun testGetMultipleTimes() {
        interceptor.rule(get, url eq TEST_URL, times = anyTimes) { respond(TEST_RESPONSE) }

        var first: String? = null
        for (i in 0..9) {
            val actual = client.newCall(Request.Builder().url(TEST_URL).get().build())
                .execute().body!!.string()

            if (first == null) {
                first = actual

            } else {
                assertEquals(first, actual)
            }
        }
    }

    @Test
    fun testURLStartsWith() {
        interceptor.rule(get, url startWith "https://") { respond(TEST_RESPONSE) }

        client.newCall(
            Request.Builder()
                .url(TEST_URL)
                .get()
                .build()
        )
            .execute()
    }

    @Test(expected = AssertionError::class)
    fun testURLStartsWith_Fail() {
        interceptor.rule(get, url startWith "http://") {
            respond(TEST_RESPONSE).code(HttpCode.HTTP_401_UNAUTHORIZED)
        }

        client.newCall(
            Request.Builder()
                .url(TEST_URL)
                .get()
                .build()
        )
            .execute()
    }

    @Test
    fun testResourceResponse() {
        interceptor.rule { respond(resource("sample.json")) }

        client.newCall(
            Request.Builder()
                .url(TEST_URL)
                .get()
                .build()
        )
            .execute()
    }

    @Test
    fun testCustomResponse() {
        val json = "{\"succeed\":true}"

        interceptor.behavior(Behavior.SEQUENTIAL)
            .rule(get or post or put) {
                respond(json, MEDIATYPE_JSON)
            }

        assertEquals(
            json, client.newCall(
                Request.Builder()
                    .url(TEST_URL)
                    .get()
                    .build()
            )
                .execute()
                .body!!
                .string()
        )
    }

    @Test
    fun testCustomizeResponse() {
        val body = "<html/>".toResponseBody(MediaTypes.MEDIATYPE_XML)

        interceptor.rule { respond(TEST_RESPONSE).body(body).addHeader("Test", "aValue") }

        val response = client.newCall(Request.Builder().url(TEST_URL).get().build()).execute()

        assertEquals(MediaTypes.MEDIATYPE_XML.type, response.body!!.contentType()!!.type)
        assertEquals(MediaTypes.MEDIATYPE_XML.subtype, response.body!!.contentType()!!.subtype)
        assertEquals("aValue", response.header("Test"))
        assertEquals("<html/>", response.body!!.string())
    }

    @Test(expected = AssertionError::class)
    fun testFailReasonSequential() {
        interceptor.behavior(Behavior.SEQUENTIAL)
            .rule(get or post or put) { respond("OK") }

        client.newCall(
            Request.Builder()
                .url(TEST_URL)
                .delete()
                .build()
        )
            .execute()
    }

    @Test
    fun testAnswer() {
        interceptor.rule(get, path matches "/aPath/(\\w+)".toRegex(), times = anyTimes) {
            respond(200) { body(it.url.encodedPath) }
        }

        val paths = arrayOf("/aPath/aaa", "/aPath/bbb", "/aPath/ccc")
        for (expectedBody in paths) {
            val url = TEST_URL.toHttpUrlOrNull()!!.newBuilder().encodedPath(expectedBody).build()

            val body = client!!.newCall(
                Request.Builder()
                    .url(url)
                    .get()
                    .build()
            )
                .execute()
                .body!!
                .string()

            assertEquals(expectedBody, body)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testThrowingException() {
        interceptor.rule(get) {
            respond { throw IllegalStateException() }
        }

        client.newCall(
            Request.Builder()
                .url(TEST_URL)
                .get()
                .build()
        )
            .execute()
    }

    companion object {
        private const val TEST_URL = "https://api.github.com/users/gmazzo"
        private const val TEST_RESPONSE = "good!"
    }

}
