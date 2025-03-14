@file:JvmName("Rules")
@file:Suppress("ClassName", "unused")

package okhttp3.mock

import java.io.InputStream
import java.util.regex.Pattern
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.asResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mock.matchers.BodyMatcher
import okhttp3.mock.matchers.HeaderMatcher
import okhttp3.mock.matchers.Matcher
import okhttp3.mock.matchers.MatcherHelper.any
import okhttp3.mock.matchers.MatcherHelper.exact
import okhttp3.mock.matchers.MatcherHelper.prefix
import okhttp3.mock.matchers.MatcherHelper.suffix
import okhttp3.mock.matchers.MethodMatcher
import okhttp3.mock.matchers.NotMatcher
import okhttp3.mock.matchers.OrMatcher
import okhttp3.mock.matchers.PathMatcher
import okhttp3.mock.matchers.QueryParamMatcher
import okhttp3.mock.matchers.URLMatcher
import okio.Buffer
import okio.BufferedSource

object url
object path
object body

data class param(val name: String)
data class header(val name: String)

val get = method(HttpMethod.GET)
val head = method(HttpMethod.HEAD)
val post = method(HttpMethod.POST)
val put = method(HttpMethod.PUT)
val delete = method(HttpMethod.DELETE)
val options = method(HttpMethod.OPTIONS)
val patch = method(HttpMethod.PATCH)
val any: Pattern = any()
const val anyTimes = Integer.MAX_VALUE

fun method(@HttpMethod method: String) = MethodMatcher(method)
fun url(value: String) = url eq value
fun path(value: String) = path eq value
fun body(value: String) = body eq value
fun not(matcher: Matcher) = NotMatcher(matcher)
fun has(param: param) = param(param.name) matches any
fun has(header: header) = header(header.name) matches any

infix fun Matcher.or(matcher: Matcher) = OrMatcher(this, matcher)

infix fun url.eq(url: String) = matches(exact(url))
infix fun url.startWith(url: String) = matches(prefix(url))
infix fun url.endsWith(url: String) = matches(suffix(url))
infix fun url.matches(pattern: Pattern) = URLMatcher(pattern)
infix fun url.matches(regex: Regex) = matches(regex.toPattern())

infix fun path.eq(path: String) = matches(exact(path))
infix fun path.startWith(path: String) = matches(prefix(path))
infix fun path.endsWith(path: String) = matches(suffix(path))
infix fun path.matches(pattern: Pattern) = PathMatcher(pattern)
infix fun path.matches(regex: Regex) = matches(regex.toPattern())

infix fun body.eq(body: String) = matches(exact(body))
infix fun body.matches(pattern: Pattern) = BodyMatcher(pattern)
infix fun body.matches(regex: Regex) = matches(regex.toPattern())

infix fun header.eq(value: String) = matches(exact(value))
infix fun header.matches(pattern: Pattern) = HeaderMatcher(name, pattern)
infix fun header.matches(regex: Regex) = matches(regex.toPattern())

infix fun param.eq(value: String) = matches(exact(value))
infix fun param.matches(pattern: Pattern) = QueryParamMatcher(name, pattern)
infix fun param.matches(regex: Regex) = matches(regex.toPattern())

fun MockInterceptor.rule(
    vararg allOf: Matcher,
    times: Int? = null,
    delay: Long? = null,
    closure: Rule.Builder.() -> Response.Builder
) {
    addRule().apply {
        allOf.forEach { matches(it) }
        times?.let(::times)
        delay?.let(::delay)
        closure()
    }
}

fun Response.Builder.body(content: ByteArray, contentType: MediaType? = null) =
    body(content.toResponseBody(contentType))

fun Response.Builder.body(content: String, contentType: MediaType? = null) =
    body(content.toResponseBody(contentType))

fun Response.Builder.body(content: BufferedSource, contentLength: Long = -1L, contentType: MediaType? = null) =
    body(content.asResponseBody(contentType, contentLength))

fun Response.Builder.body(content: InputStream, contentLength: Long = -1L, contentType: MediaType? = null) =
    body(Buffer().readFrom(content), contentLength, contentType)

private val dummyResponse = object : Response.Builder() {

    override fun build(): Response {
        throw IllegalStateException("this response is just for syntax sugar. Please review your usage of this API")
    }

}

fun Rule.Builder.respond(
    @HttpCode code: Int = HttpCode.HTTP_200_OK,
    answer: Response.Builder.(Request) -> Response.Builder
): Response.Builder =
    respond(RuleAnswer { answer(Response.Builder().code(code), it) })

fun Rule.Builder.respond(answer: RuleAnswer): Response.Builder {
    answer(answer)
    return dummyResponse
}

internal fun assertThat(value: Boolean, lazyMessage: () -> Any) = check(value, lazyMessage)
