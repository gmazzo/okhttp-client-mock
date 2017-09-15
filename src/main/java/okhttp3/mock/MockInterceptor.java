package okhttp3.mock;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link Interceptor} for {@link okhttp3.OkHttpClient}, which with match request and provide pre-configured mock responses.
 */
public class MockInterceptor implements Interceptor {
    private final List<Rule> rules = new LinkedList<>();
    private Behavior behavior;

    /**
     * Creates a MockInterceptor with a default {@link Behavior#STRICT} behavior
     */
    public MockInterceptor() {
        this(Behavior.STRICT);
    }

    public MockInterceptor(Behavior behavior) {
        this.behavior = behavior;
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Adds a new mock rule to this interceptor. Please make sure to a {@link Rule.Builder} and call
     * one of the final statements {@link Rule.Builder#respond}.
     * <p>
     * Example:
     * <pre>{@code
     *  interceptor.addRule(new Rule.Builder()
     *      .isGet()
     *      .urlStarts("https://someserver/")
     *      .respond(HTTP_200_OK)
     *          .header("SomeHeader", "SomeValue"));
     * }</pre>
     */
    public MockInterceptor addRule(Response.Builder builder) {
        if (!(builder instanceof Rule.Builder.RuleResponseBuilder)) {
            throw new IllegalArgumentException("This response was not created with Rule.Builder!");
        }
        rules.add(((Rule.Builder.RuleResponseBuilder) builder).buildRule());
        return this;
    }

    public MockInterceptor clear() {
        rules.clear();
        return this;
    }

    public Behavior behavior() {
        return behavior;
    }

    public MockInterceptor behavior(Behavior behavior) {
        this.behavior = behavior;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        for (Iterator<Rule> it = rules.iterator(); it.hasNext(); ) {
            Rule rule = it.next();
            Response response = rule.accept(request);

            if (rule.isConsumed()) {
                it.remove();
            }
            if (response != null) {
                return response;

            } else if (behavior == Behavior.SORTED) {
                throw new AssertionError("Not mached next rule: " + rule + ", request=" + request);
            }
        }
        if (behavior == Behavior.STRICT) {
            throw new AssertionError("Not mached any rule: request=" + request);
        }
        return chain.proceed(request);
    }

}
