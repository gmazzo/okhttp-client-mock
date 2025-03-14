package okhttp3.mock.matchers;

import okhttp3.Request;

import java.util.regex.Pattern;

public class URLMatcher extends PatternMatcher {

    public URLMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    protected String getText(Request request) {
        return request.url().toString();
    }

    @Override
    public String toString() {
        return "url(~=" + pattern.pattern() + ")";
    }

}
