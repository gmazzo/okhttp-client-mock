package okhttp3.mock.matchers;

import okhttp3.Request;

public class MethodMatcher implements Matcher {
    private final String method;

    public MethodMatcher(String method) {
        this.method = method;
    }

    @Override
    public boolean matches(Request request) {
        return method.equalsIgnoreCase(request.method());
    }

    @Override
    public String toString() {
        return "method(" + method + ")";
    }

}
