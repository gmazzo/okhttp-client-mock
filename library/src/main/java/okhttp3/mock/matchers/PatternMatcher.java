package okhttp3.mock.matchers;

import java.util.regex.Pattern;

import okhttp3.Request;

import static okhttp3.mock.matchers.MatcherHelper.reason;

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

    @Override
    public String failReason(Request request) {
        String actual = getText(request);
        return reason(pattern.pattern(), actual);
    }

}
