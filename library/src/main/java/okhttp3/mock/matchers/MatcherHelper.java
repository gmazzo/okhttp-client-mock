package okhttp3.mock.matchers;

import java.util.regex.Pattern;

public final class MatcherHelper {

    public static Pattern any() {
        return Pattern.compile(".*");
    }

    public static Pattern exact(String text) {
        return Pattern.compile(Pattern.quote(text));
    }

    public static Pattern prefix(String text) {
        return Pattern.compile("^" + Pattern.quote(text) + ".*$");
    }

    public static Pattern suffix(String text) {
        return Pattern.compile("^.*" + Pattern.quote(text) + "$");
    }

    public static String reason(String expected, String actual) {
        return "expected=" + expected + ";actual=" + actual;
    }

    private MatcherHelper() {
    }

}
