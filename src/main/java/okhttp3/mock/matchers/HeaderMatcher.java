package okhttp3.mock.matchers;

import java.util.regex.Pattern;

import okhttp3.Request;

public class HeaderMatcher extends PatternMatcher {
    private final String header;

    public HeaderMatcher(String header, Pattern pattern) {
        super(pattern);

        this.header = header;
    }

    @Override
    protected String getText(Request request) {
        return request.header(header);
    }

    @Override
    public String toString() {
        return "header(" + header + "~=" + pattern.pattern() + ")";
    }

}
