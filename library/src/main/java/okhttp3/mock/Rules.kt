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

public object url
public object path
public object body

public data class param(val name: String)
public data class header(val name: String)

public val get: MethodMatcher = method(HttpMethod.GET)
public val head: MethodMatcher = method(HttpMethod.HEAD)
public val post: MethodMatcher = method(HttpMethod.POST)
public val put: MethodMatcher = method(HttpMethod.PUT)
public val delete: MethodMatcher = method(HttpMethod.DELETE)
public val options: MethodMatcher = method(HttpMethod.OPTIONS)
public val patch: MethodMatcher = method(HttpMethod.PATCH)
public val any: Pattern = any()
public const val anyTimes: Int = Integer.MAX_VALUE

public fun method(@HttpMethod method: String): MethodMatcher = MethodMatcher(method)
public fun url(value: String): URLMatcher = url eq value
public fun path(value: String): PathMatcher = path eq value
public fun body(value: String): BodyMatcher = body eq value
public fun not(matcher: Matcher): NotMatcher = NotMatcher(matcher)
public fun has(param: param): QueryParamMatcher = param(param.name) matches any
public fun has(header: header): HeaderMatcher = header(header.name) matches any

public infix fun Matcher.or(matcher: Matcher): OrMatcher = OrMatcher(this, matcher)

public infix fun url.eq(url: String): URLMatcher = matches(exact(url))
public infix fun url.startWith(url: String): URLMatcher = matches(prefix(url))
public infix fun url.endsWith(url: String): URLMatcher = matches(suffix(url))
public infix fun url.matches(pattern: Pattern): URLMatcher = URLMatcher(pattern)
public infix fun url.matches(regex: Regex): URLMatcher = matches(regex.toPattern())

public infix fun path.eq(path: String): PathMatcher = matches(exact(path))
public infix fun path.startWith(path: String): PathMatcher = matches(prefix(path))
public infix fun path.endsWith(path: String): PathMatcher = matches(suffix(path))
public infix fun path.matches(pattern: Pattern): PathMatcher = PathMatcher(pattern)
public infix fun path.matches(regex: Regex): PathMatcher = matches(regex.toPattern())

public infix fun body.eq(body: String): BodyMatcher = matches(exact(body))
public infix fun body.matches(pattern: Pattern): BodyMatcher = BodyMatcher(pattern)
public infix fun body.matches(regex: Regex): BodyMatcher = matches(regex.toPattern())

public infix fun header.eq(value: String): HeaderMatcher = matches(exact(value))
public infix fun header.matches(pattern: Pattern): HeaderMatcher = HeaderMatcher(name, pattern)
public infix fun header.matches(regex: Regex): HeaderMatcher = matches(regex.toPattern())

public infix fun param.eq(value: String): QueryParamMatcher = matches(exact(value))
public infix fun param.matches(pattern: Pattern): QueryParamMatcher = QueryParamMatcher(name, pattern)
public infix fun param.matches(regex: Regex): QueryParamMatcher = matches(regex.toPattern())

public fun MockInterceptor.rule(
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

public fun Response.Builder.body(content: ByteArray, contentType: MediaType? = null): Response.Builder =
    body(content.toResponseBody(contentType))

public fun Response.Builder.body(content: String, contentType: MediaType? = null): Response.Builder =
    body(content.toResponseBody(contentType))

public fun Response.Builder.body(content: BufferedSource, contentLength: Long = -1L, contentType: MediaType? = null): Response.Builder =
    body(content.asResponseBody(contentType, contentLength))

public fun Response.Builder.body(content: InputStream, contentLength: Long = -1L, contentType: MediaType? = null): Response.Builder =
    body(Buffer().readFrom(content), contentLength, contentType)

private val dummyResponse = object : Response.Builder() {

    override fun build(): Response {
        throw IllegalStateException("this response is just for syntax sugar. Please review your usage of this API")
    }

}

public fun Rule.Builder.respond(
    @HttpCode code: Int = HttpCode.HTTP_200_OK,
    answer: Response.Builder.(Request) -> Response.Builder
): Response.Builder =
    respond(RuleAnswer { answer(Response.Builder().code(code), it) })

public fun Rule.Builder.respond(answer: RuleAnswer): Response.Builder {
    answer(answer)
    return dummyResponse
}

internal fun assertThat(value: Boolean, lazyMessage: () -> Any) = check(value, lazyMessage)
