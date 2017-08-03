package okhttp3.mock.matchers;

import okhttp3.Request;

public class OrMatcher implements Matcher {
    private final Matcher left;
    private final Matcher right;

    public OrMatcher(Matcher left, Matcher right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean matches(Request request) {
        return left.matches(request) || right.matches(request);
    }

    @Override
    public String toString() {
        return "or(" + left + "," + right + ")";
    }

}
