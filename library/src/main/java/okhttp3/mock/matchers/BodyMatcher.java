package okhttp3.mock.matchers;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static okhttp3.mock.Rules.assertThat;

public class BodyMatcher extends PatternMatcher {
    private final Charset charset;

    public BodyMatcher(Pattern pattern) {
        this(pattern, null);
    }

    public BodyMatcher(Pattern pattern, Charset charset) {
        super(pattern);

        this.charset = charset != null ? charset : StandardCharsets.UTF_8;
    }

    @Override
    protected String getText(Request request) {
        RequestBody body = request.body();
        assertThat(body != null, () -> "Request" + request + " does not have a body");
        assert body != null;
        assertThat(!body.isDuplex(), () -> "duplex bodies can't be matched");
        assertThat(!body.isOneShot(), () -> "onShoot bodies can't be matched");

        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readString(charset);

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read body of request: " + request, e);
        }
    }

    @Override
    public String toString() {
        return "requestBody(" + pattern.pattern() + "); charset=" + charset;
    }

}
