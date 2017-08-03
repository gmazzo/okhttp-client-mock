package okhttp3.mock.matchers;

import java.util.regex.Pattern;

import okhttp3.Request;

public abstract class PatternMatcher implements Matcher {
    protected final Pattern pattern;

    public PatternMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    protected abstract String getText(Request request);

    @Override
    public boolean matches(Request request) {
        String text = getText(request);
        return text != null && pattern.matcher(text).matches();
    }

}
