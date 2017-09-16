package okhttp3.mock.matchers;

import java.util.regex.Pattern;

import okhttp3.Request;

public interface Matcher {

    boolean matches(Request request);

    String failReason(Request request);

    static Pattern any() {
        return Pattern.compile(".*");
    }

    static Pattern exact(String text) {
        return Pattern.compile(Pattern.quote(text));
    }

    static Pattern prefix(String text) {
        return Pattern.compile("^" + Pattern.quote(text) + ".*$");
    }

    static Pattern suffix(String text) {
        return Pattern.compile("^.*" + Pattern.quote(text) + "$");
    }

    static String reason(String expected, String actual) {
        return "expected=" + expected + ";actual=" + actual;
    }

}
