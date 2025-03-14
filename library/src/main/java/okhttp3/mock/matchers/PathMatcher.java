package okhttp3.mock.matchers;

import okhttp3.Request;

import java.util.regex.Pattern;

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
