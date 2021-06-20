package okhttp3.mock.matchers;

import okhttp3.Request;
import okio.Buffer;

import java.io.IOException;
import java.util.regex.Pattern;


public class RequestBodyMatcher extends PatternMatcher  {
    public RequestBodyMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    protected String getText(Request request) {
        try {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public String toString() {
        return "requestBody(" + pattern.pattern() + ")";
    }
}
