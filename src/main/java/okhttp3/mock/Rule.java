package okhttp3.mock;

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

    public static class Builder {
        private final List<Matcher> matchers = new LinkedList<>();
        private Response.Builder response;
        private int times = 1;
        private long delay = 0;
        private boolean negateNext;

        public Builder isGET() {
            match(new MethodMatcher("GET"));
            return this;
        }

        public Builder isPOST() {
            match(new MethodMatcher("POST"));
            return this;
        }

        public Builder isPUT() {
            match(new MethodMatcher("PUT"));
            return this;
        }

        public Builder isDELETE() {
            match(new MethodMatcher("DELETE"));
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
            match(new URLMatcher(pattern));
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
            match(new PathMatcher(pattern));
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
            match(new HeaderMatcher(header, pattern));
            return this;
        }

        public Builder not() {
            negateNext = true;
            return this;
        }

        public Builder match(Matcher matcher) {
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

        public Rule andRespond(String body) {
            andRespond(ResponseBody.create(MediaType.parse("text/plain"), body));
            return build();
        }

        public Rule andRespond(byte[] body) {
            andRespond(ResponseBody.create(MediaType.parse("application/octet-stream"), body));
            return build();
        }

        public Rule andRespond(InputStream body) {
            try {
                andRespond(ResponseBody.create(MediaType.parse("application/octet-stream"), -1, new Buffer().readFrom(body)));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return build();
        }

        public Rule andRespond(ResponseBody body) {
            andRespond(200 /* OK */, body);
            return build();
        }

        public Rule andRespond(int code, ResponseBody body) {
            andRespond(new Response.Builder()
                    .code(code)
                    .body(body));
            return build();
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
