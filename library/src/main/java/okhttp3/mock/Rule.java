package okhttp3.mock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.*;
import okhttp3.mock.matchers.*;
import okio.Buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

import static okhttp3.mock.HttpCodes.HTTP_200_OK;
import static okhttp3.mock.HttpMethod.*;
import static okhttp3.mock.MediaTypes.MEDIATYPE_RAW_DATA;
import static okhttp3.mock.MediaTypes.MEDIATYPE_TEXT;
import static okhttp3.mock.matchers.MatcherHelper.*;

public class Rule {
    private final List<Matcher> matchers;
    private final RuleAnswer answer;
    private final long delay;
    private int times;

    private Rule(List<Matcher> matchers, RuleAnswer answer, int times, long delay) {
        this.matchers = matchers;
        this.answer = answer;
        this.times = times;
        this.delay = delay;
    }

    protected Response accept(Request request) {
        if (isConsumed()) {
            return null;
        }
        for (Matcher matcher : matchers) {

            if (!matcher.matches(request)) {
                return null;
            }
        }
        if (delay > 0) {
            try {
                Thread.sleep(delay);

            } catch (InterruptedException ignored) {
            }
        }
        if (times > 0 && times != Integer.MAX_VALUE) {
            times--;
        }
        return answer.respond(request)
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .message("Rule response " + this)
                .build();
    }

    public Map<Matcher, String> getFailReason(Request request) {
        Map<Matcher, String> reasons = new LinkedHashMap<>();
        for (Matcher matcher : matchers) {
            if (!matcher.matches(request)) {
                reasons.put(matcher, matcher.failReason(request));
            }
        }
        return reasons;
    }

    public boolean isConsumed() {
        return times == 0;
    }

    @Override
    public String toString() {
        return matchers.toString() + ", consumed=" + isConsumed();
    }

    public static class Builder {
        private final List<Matcher> matchers = new LinkedList<>();
        private int times = 1;
        private long delay = 0;
        private boolean negateNext;
        private boolean orNext;

        public Builder get() {
            method(GET);
            return this;
        }

        public Builder get(String url) {
            get();
            return url(url);
        }

        public Builder head() {
            method(HEAD);
            return this;
        }

        public Builder head(String url) {
            head();
            return url(url);
        }

        public Builder post() {
            method(POST);
            return this;
        }

        public Builder post(String url) {
            post();
            return url(url);
        }

        public Builder put() {
            method(PUT);
            return this;
        }

        public Builder put(String url) {
            put();
            return url(url);
        }

        public Builder delete() {
            method(DELETE);
            return this;
        }

        public Builder delete(String url) {
            delete();
            return url(url);
        }

        public Builder options() {
            method(OPTIONS);
            return this;
        }

        public Builder options(String url) {
            options();
            return url(url);
        }

        public Builder patch() {
            method(PATCH);
            return this;
        }

        public Builder patch(String url) {
            patch();
            return url(url);
        }

        public Builder method(@HttpMethod String method) {
            matches(new MethodMatcher(method));
            return this;
        }

        public Builder url(String url) {
            urlMatches(exact(url));
            return this;
        }

        public Builder urlStarts(String prefix) {
            urlMatches(prefix(prefix));
            return this;
        }

        public Builder urlEnds(String suffix) {
            urlMatches(suffix(suffix));
            return this;
        }

        public Builder urlMatches(Pattern pattern) {
            matches(new URLMatcher(pattern));
            return this;
        }

        public Builder path(String path) {
            pathMatches(exact(path));
            return this;
        }

        public Builder pathStarts(String prefix) {
            pathMatches(prefix(prefix));
            return this;
        }

        public Builder pathEnds(String suffix) {
            pathMatches(suffix(suffix));
            return this;
        }

        public Builder pathMatches(Pattern pattern) {
            matches(new PathMatcher(pattern));
            return this;
        }

        public Builder hasHeader(String header) {
            headerMatches(header, any());
            return this;
        }

        public Builder header(String header, String value) {
            headerMatches(header, exact(value));
            return this;
        }

        public Builder headerMatches(String header, Pattern pattern) {
            matches(new HeaderMatcher(header, pattern));
            return this;
        }

        public Builder paramMatches(String param, Pattern pattern) {
            matches(new QueryParamMatcher(param, pattern));
            return this;
        }

        public Builder hasParam(String param) {
            paramMatches(param, any());
            return this;
        }

        public Builder param(String param, String value) {
            paramMatches(param, exact(value));
            return this;
        }

        public Builder body(String value) {
            return body(value, null);
        }

        public Builder body(String value, Charset charset) {
            bodyMatches(exact(value), charset);
            return this;
        }

        public Builder bodyMatches(Pattern pattern) {
            return bodyMatches(pattern, null);
        }

        public Builder bodyMatches(Pattern pattern, Charset charset) {
            matches(new BodyMatcher(pattern, charset));
            return this;
        }

        public Builder not() {
            if (negateNext) {
                throw new IllegalStateException("'not()' can't be followed by another 'not()'");
            }
            negateNext = true;
            return this;
        }

        public Builder or() {
            if (orNext) {
                throw new IllegalStateException("'or()' can't be followed by another 'or()'");
            }
            orNext = true;
            return this;
        }

        public Builder matches(Matcher matcher) {
            if (negateNext) {
                negateNext = false;
                matcher = new NotMatcher(matcher);
            }
            if (orNext) {
                int count = matchers.size();
                if (count <= 0) {
                    throw new IllegalStateException("'or()' can't be the first matcher!");
                }

                orNext = false;
                Matcher prev = matchers.remove(count - 1);
                if (prev instanceof OrMatcher) {
                    ((OrMatcher) prev).add(matcher);
                    matcher = prev;

                } else {
                    matcher = new OrMatcher(prev, matcher);
                }
            }
            matchers.add(matcher);
            return this;
        }

        public Builder times(int times) {
            this.times = times;
            return this;
        }

        public Builder anyTimes() {
            this.times = Integer.MAX_VALUE;
            return this;
        }

        public Builder delay(long milliseconds) {
            this.delay = milliseconds;
            return this;
        }

        public Response.Builder respond(@NonNull String body) {
            return respond(body, MEDIATYPE_TEXT);
        }

        public Response.Builder respond(@NonNull String body, @NonNull MediaType mediaType) {
            return respond(ResponseBody.create(mediaType, body));
        }

        public Response.Builder respond(@NonNull byte[] body) {
            return respond(body, MEDIATYPE_RAW_DATA);
        }

        public Response.Builder respond(@NonNull byte[] body, @NonNull MediaType mediaType) {
            return respond(ResponseBody.create(mediaType, body));
        }

        public Response.Builder respond(@NonNull InputStream body) {
            return respond(-1, body);
        }

        public Response.Builder respond(@NonNull InputStream body, @NonNull MediaType mediaType) {
            return respond(-1, body, mediaType);
        }

        public Response.Builder respond(long contentLength, @NonNull InputStream body) {
            return respond(contentLength, body, MEDIATYPE_RAW_DATA);
        }

        public Response.Builder respond(long contentLength, @NonNull InputStream body, @NonNull MediaType mediaType) {
            try {
                return respond(ResponseBody.create(mediaType, contentLength, new Buffer().readFrom(body)));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Response.Builder respond(@HttpCode int code) {
            return respond(code, (ResponseBody) null);
        }

        public Response.Builder respond(@Nullable ResponseBody body) {
            return respond(HTTP_200_OK, body);
        }

        public Response.Builder respond(@HttpCode int code, @Nullable ResponseBody body) {
            FinalRuleBuilder builder = new FinalRuleBuilder();
            builder.code(code);
            builder.body(body != null ? body : ResponseBody.create(null, ""));
            onBuild(builder.buildRule());
            return builder;
        }

        public void answer(RuleAnswer answer) {
            onBuild(new Rule(Collections.unmodifiableList(matchers), answer, times, delay));
        }

        void onBuild(Rule rule) {
        }

        class FinalRuleBuilder extends Response.Builder implements RuleAnswer {
            private final boolean repeteable = times != 1;
            private ResponseBody repeteableBody;
            private byte repeteableBodyContent[];

            Rule buildRule() {
                if (negateNext) {
                    throw new IllegalStateException("Misted a predicate after 'not()'!");
                }
                if (orNext) {
                    throw new IllegalStateException("Misted a predicate after 'or()'!");
                }
                if (times < 1) {
                    throw new IllegalStateException("Time can't be less than 1!");
                }
                if (delay < 0) {
                    throw new IllegalStateException("Delay can't be less than 0!");
                }
                return new Rule(Collections.unmodifiableList(matchers), this, times, delay);
            }

            @Override
            public Response.Builder body(ResponseBody body) {
                if (repeteable) {
                    repeteableBody = body;
                    try {
                        repeteableBodyContent = body.bytes();

                    } catch (IOException e) {
                        throw new IllegalArgumentException("error preloading body for rule " + this, e);
                    }
                }
                return super.body(body);
            }

            @Override
            public Response.Builder respond(Request request) {
                if (repeteable) {
                    Buffer buffer = repeteableBody.source().buffer();
                    buffer.clear();
                    buffer.write(repeteableBodyContent);
                }
                return this;
            }

        }

    }

}
