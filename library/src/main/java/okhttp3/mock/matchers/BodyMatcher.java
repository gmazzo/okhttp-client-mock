package okhttp3.mock.matchers;

import okhttp3.Request;
import okhttp3.mock.RequestCache;
import java.util.regex.Pattern;


public class BodyMatcher extends PatternMatcher  {

    public BodyMatcher(Predicate<RequestBody> condition) {
        super(pattern);
    }

    @Override
    protected String getText(Request request) {
        RequestBody body = request.requestBody();
        check(!body.isDuplex()) { "duplex bodies can't be matched" }
        check(!body.isOneShoot()) { "onShoot bodies can't be matched" }

        Buffer buffer = new Buffer()
        body.writeTo(buffer)
        return buffer.readString(charset);
    }

    @Override
    public String toString() {
        return "requestBody(" + pattern.pattern() + ")";
    }
}
