package okhttp3.mock.matchers;

import java.util.regex.Pattern;

import okhttp3.Request;

public class PathMatcher extends PatternMatcher {

    public PathMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    protected String getText(Request request) {
        return request.url().url().getPath();
    }

    @Override
    public String toString() {
        return "path(~=" + pattern.pattern() + ")";
    }

}
