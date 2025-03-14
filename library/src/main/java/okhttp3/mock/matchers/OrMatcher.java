package okhttp3.mock.matchers;

import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

public class OrMatcher implements Matcher {
    private final List<Matcher> matchers = new ArrayList<>();

    public OrMatcher(Matcher left, Matcher right) {
        add(left);
        add(right);
    }

    public void add(Matcher matcher) {
        matchers.add(matcher);
    }

    @Override
    public boolean matches(Request request) {
        for (Matcher matcher : matchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String failReason(Request request) {
        StringBuilder sb = new StringBuilder("or(");
        boolean first = true;
        for (Matcher matcher : matchers) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(matcher.failReason(request));
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("or(");
        boolean first = true;
        for (Matcher matcher : matchers) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(matcher);
        }
        sb.append(')');
        return sb.toString();
    }

}
