package okhttp3.mock

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mock.ClasspathResources.resource
import okhttp3.mock.MediaTypes.MEDIATYPE_JSON
import org.junit.Assert.assertEquals
import org.junit.Test

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

    @Test
    fun testRequestBody() {
        val request1 = """{ "id": 1, "name": "name here" }"""
        val request2 = """{ "id" : 1 }"""

        val expectedResponse1 = "it`s request 1!"
        val expectedResponse2 = "it`s request 2!"

        interceptor.rule(post, url eq TEST_URL, body eq request1, times = anyTimes) { respond(expectedResponse1.toResponseBody(MEDIATYPE_JSON)) }
        interceptor.rule(delete, url eq TEST_URL, body eq request2, times = anyTimes) { respond(expectedResponse2.toResponseBody(MEDIATYPE_JSON)) }

        val response1 = client.newCall(Request.Builder().url(TEST_URL).post(request1.toRequestBody(MEDIATYPE_JSON)).build()).execute()
        assertEquals(expectedResponse1, response1.body!!.string())

        val response2 = client.newCall(Request.Builder().url(TEST_URL).delete(request2.toRequestBody(MEDIATYPE_JSON)).build()).execute()
        assertEquals(expectedResponse2, response2.body!!.string())

    }

    @Test(expected = AssertionError::class)
    fun testRequestBody_Fail() {
        val json = """{ "id": 1, "name": "name here" }"""
        val reqBody = json.toRequestBody(MEDIATYPE_JSON)

        interceptor.rule(post, url eq TEST_URL, body eq "", times = anyTimes) { respond(TEST_RESPONSE) }

        client.newCall(Request.Builder().url(TEST_URL).post(reqBody).build()).execute()
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
