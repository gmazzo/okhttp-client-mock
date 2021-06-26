package okhttp3.mock.matchers;

import okhttp3.Request;
import okhttp3.mock.RequestCache;
import java.util.regex.Pattern;


public class BodyMatcher extends PatternMatcher  {

    public BodyMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    protected String getText(Request request) {
        return RequestCache.INSTANCE.requestBody();
    }

    @Override
    public String toString() {
        return "requestBody(" + pattern.pattern() + ")";
    }
}
