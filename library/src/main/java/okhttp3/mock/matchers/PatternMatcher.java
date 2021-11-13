package okhttp3.mock.matchers;

import okhttp3.Request;

import java.util.regex.Pattern;

import static okhttp3.mock.matchers.MatcherHelper.reason;

public abstract class PatternMatcher implements Matcher {
    protected final Pattern pattern;

    public PatternMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    protected abstract CharSequence getText(Request request);

    @Override
    public boolean matches(Request request) {
        CharSequence text = getText(request);
        return text != null && pattern.matcher(text).matches();
    }

    @Override
    public String failReason(Request request) {
        CharSequence actual = getText(request);
        return reason(pattern.pattern(), actual);
    }

}
