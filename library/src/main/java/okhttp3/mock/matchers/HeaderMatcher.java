package okhttp3.mock.matchers;

import okhttp3.Request;

import java.util.regex.Pattern;

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
