package okhttp3.mock.matchers;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import okhttp3.Request;

import static okhttp3.mock.matchers.Matcher.any;
import static okhttp3.mock.matchers.Matcher.exact;
import static org.junit.Assert.assertEquals;

public class HeaderMatcherTest {
    private Request request;

    @Before
    public void setup() {
        request = new Request.Builder()
                .url("http://test.com")
                .header("a", "")
                .header("b", "aValue")
                .build();
    }

    private boolean hasHeader(String name) {
        return new HeaderMatcher(name, any()).matches(request);
    }

    private boolean headerIs(String name, String value) {
        return new HeaderMatcher(name, exact(value)).matches(request);
    }

    @Test
    public void testHasHeader_a() {
        assertEquals(true, hasHeader("a"));
    }

    @Test
    public void testHasHeader_A() {
        assertEquals(true, hasHeader("A"));
    }

    @Test
    public void testHasHeader_b() {
        assertEquals(true, hasHeader("b"));
    }

    @Test
    public void testHasHeader_B() {
        assertEquals(true, hasHeader("b"));
    }

    @Test
    public void testHasHeader_c() {
        assertEquals(false, hasHeader("c"));
    }

    @Test
    public void testHasHeader_C() {
        assertEquals(false, hasHeader("C"));
    }

    @Test
    public void testHeaderIs_a() {
        assertEquals(false, headerIs("a", "someValue"));
    }

    @Test
    public void testHeaderIs_a_Empty() {
        assertEquals(true, headerIs("a", ""));
    }

    @Test
    public void testHeaderIs_b() {
        assertEquals(true, headerIs("b", "aValue"));
    }

    @Test
    public void testHeaderIs_b_Empty() {
        assertEquals(false, headerIs("b", ""));
    }

    @Test
    public void testHeaderIs_b_Partial() {
        assertEquals(false, headerIs("b", "aVa"));
    }

    @Test
    public void testHeaderIs_b_Like() {
        assertEquals(true, new HeaderMatcher("b", Pattern.compile(".*alue.*")).matches(request));
    }

}
