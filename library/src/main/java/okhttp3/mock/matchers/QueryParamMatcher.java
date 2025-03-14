package okhttp3.mock.matchers;

import okhttp3.Request;

import java.util.regex.Pattern;

public class QueryParamMatcher extends PatternMatcher {
    private final String param;

    public QueryParamMatcher(String param, Pattern pattern) {
        super(pattern);

        this.param = param;
    }

    @Override
    protected String getText(Request request) {
        return request.url().queryParameter(param);
    }

    @Override
    public String toString() {
        return "param(" + param + "~=" + pattern.pattern() + ")";
    }

}
