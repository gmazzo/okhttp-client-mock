package okhttp3.mock.matchers;

import okhttp3.Request;

public interface Matcher {

    boolean matches(Request request);

    String failReason(Request request);

}
