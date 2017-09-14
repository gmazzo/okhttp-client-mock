package okhttp3.mock;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mock.matchers.HeaderMatcher;
import okhttp3.mock.matchers.Matcher;
import okhttp3.mock.matchers.MethodMatcher;
import okhttp3.mock.matchers.NotMatcher;
import okhttp3.mock.matchers.PathMatcher;
import okhttp3.mock.matchers.URLMatcher;
import okio.Buffer;

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

    public static class Builder implements HttpCodes, MediaTypes {
        private final List<Matcher> matchers = new LinkedList<>();
        private Response.Builder response;
        private int times = 1;
        private long delay = 0;
        private boolean negateNext;

        public Builder isGET() {
            method("GET");
            return this;
        }

        public Builder isPOST() {
            method("POST");
            return this;
        }

        public Builder isPUT() {
            method("PUT");
            return this;
        }

        public Builder isDELETE() {
            method("DELETE");
            return this;
        }

        public Builder method(String method) {
            matches(new MethodMatcher(method));
            return this;
        }

        public Builder urlIs(String url) {
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

        public Builder pathIs(String path) {
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

        public Builder headerIs(String header, String value) {
            headerMatches(header, exact(value));
            return this;
        }

        public Builder headerMatches(String header, Pattern pattern) {
            matches(new HeaderMatcher(header, pattern));
            return this;
        }

        public Builder not() {
            negateNext = true;
            return this;
        }

        public Builder matches(Matcher matcher) {
            if (negateNext) {
                negateNext = false;
                matcher = new NotMatcher(matcher);
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

        public Rule andRespond(@NonNull String body) {
            return andRespond(HTTP_OK, body);
        }

        public Rule andRespond(int code, @NonNull String body) {
            return andRespond(code, ResponseBody.create(TYPE_PLAIN_TEXT, body));
        }

        public Rule andRespond(@NonNull byte[] body) {
            return andRespond(HTTP_OK, body);
        }

        public Rule andRespond(int code, @NonNull byte[] body) {
            return andRespond(code, new ByteArrayInputStream(body));
        }

        public Rule andRespond(@NonNull InputStream body) {
            return andRespond(HTTP_OK, body);
        }

        public Rule andRespond(int code, @NonNull InputStream body) {
            try {
                return andRespond(code, ResponseBody.create(TYPE_OCTET_STREAM, -1, new Buffer().readFrom(body)));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Rule andRespond(int code) {
            return andRespond(code, (ResponseBody) null);
        }

        public Rule andRespond(@Nullable ResponseBody body) {
            return andRespond(HTTP_OK, body);
        }

        public Rule andRespond(int code, @Nullable ResponseBody body) {
            return andRespond(new Response.Builder()
                    .code(code)
                    .body(body));
        }

        public Rule andRespond(Response.Builder response) {
            this.response = response;
            return build();
        }

        private Rule build() {
            if (negateNext) {
                throw new IllegalStateException("Misted a predicate after 'not()'!");
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
