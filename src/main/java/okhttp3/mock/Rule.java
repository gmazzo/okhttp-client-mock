package okhttp3.mock;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mock.matchers.HeaderMatcher;
import okhttp3.mock.matchers.Matcher;
import okhttp3.mock.matchers.MethodMatcher;
import okhttp3.mock.matchers.NotMatcher;
import okhttp3.mock.matchers.OrMatcher;
import okhttp3.mock.matchers.PathMatcher;
import okhttp3.mock.matchers.QueryParamMatcher;
import okhttp3.mock.matchers.URLMatcher;
import okio.Buffer;

import static okhttp3.mock.HttpCodes.HTTP_200_OK;
import static okhttp3.mock.HttpMethods.DELETE;
import static okhttp3.mock.HttpMethods.GET;
import static okhttp3.mock.HttpMethods.POST;
import static okhttp3.mock.HttpMethods.PUT;
import static okhttp3.mock.MediaTypes.MEDIATYPE_RAW_DATA;
import static okhttp3.mock.MediaTypes.MEDIATYPE_TEXT;
import static okhttp3.mock.matchers.Matcher.any;
import static okhttp3.mock.matchers.Matcher.exact;
import static okhttp3.mock.matchers.Matcher.prefix;
import static okhttp3.mock.matchers.Matcher.suffix;

public class Rule {
    private final List<Matcher> matchers;
    private final Response.Builder response;
    private final long delay;
    private int times;

    private Rule(List<Matcher> matchers, Response.Builder response, int times, long delay) {
        this.matchers = matchers;
        this.response = response;
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
        return response
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .message("Rule response " + this)
                .build();
    }

    public boolean isConsumed() {
        return times == 0;
    }

    @Override
    public String toString() {
        return matchers.toString();
    }

    public static class Builder {
        private final List<Matcher> matchers = new LinkedList<>();
        private RuleResponseBuilder response;
        private int times = 1;
        private long delay = 0;
        private boolean negateNext;
        private boolean orNext;

        public Builder get() {
            method(GET);
            return this;
        }

        public Builder post() {
            method(POST);
            return this;
        }

        public Builder put() {
            method(PUT);
            return this;
        }

        public Builder delete() {
            method(DELETE);
            return this;
        }

        public Builder method(String method) {
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
                matcher = new OrMatcher(matchers.remove(count - 1), matcher);
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

        public Response.Builder respond(int code) {
            return respond((ResponseBody) null)
                    .code(code);
        }

        public Response.Builder respond(@Nullable ResponseBody body) {
            return this.response = new RuleResponseBuilder(body);
        }

        class RuleResponseBuilder extends Response.Builder {

            private RuleResponseBuilder(ResponseBody body) {
                code(HTTP_200_OK);
                body(body != null ? body : ResponseBody.create(null, ""));
            }

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
                if (response == null) {
                    throw new IllegalStateException("No response recorded for this rule!");
                }
                return new Rule(Collections.unmodifiableList(new ArrayList<>(matchers)), response, times, delay);
            }

        }

    }

}
