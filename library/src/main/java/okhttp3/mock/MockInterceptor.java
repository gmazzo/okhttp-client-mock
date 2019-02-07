package okhttp3.mock;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mock.matchers.Matcher;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An {@link Interceptor} for {@link okhttp3.OkHttpClient}, which with match request and provide pre-configured mock responses.
 */
public class MockInterceptor implements Interceptor {
    private final List<Rule> rules = new LinkedList<>();
    private Behavior behavior;

    /**
     * Creates a MockInterceptor with a default {@link Behavior#SEQUENTIAL} behavior
     */
    public MockInterceptor() {
        this(Behavior.SEQUENTIAL);
    }

    public MockInterceptor(Behavior behavior) {
        this.behavior = behavior;
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Adds a new mock rule to this interceptor. Use {@link Rule.Builder} to create one and
     * call the final statements {@link Rule.Builder#respond}.
     * <p>
     * Example:
     * <pre>{@code
     *  interceptor.addRule(new Rule.Builder()
     *      .get()
     *      .urlStarts("https://someserver/")
     *      .respond(HTTP_200_OK)
     *          .header("SomeHeader", "SomeValue"));
     * }</pre>
     *
     * @param builder the rule to add
     * @return this instance
     */
    public MockInterceptor addRule(Response.Builder builder) {
        if (!(builder instanceof Rule.Builder.FinalRuleBuilder)) {
            throw new IllegalArgumentException("This response was not created with Rule.Builder!");
        }
        addRule(((Rule.Builder.FinalRuleBuilder) builder).buildRule());
        return this;
    }

    /**
     * Adds a new mock rule to this interceptor.
     *
     * @param rule the rule to add
     * @return this instance
     */
    public MockInterceptor addRule(Rule rule) {
        rules.add(rule);
        return this;
    }

    /**
     * Short hand for {@link #addRule(Response.Builder builder)}
     *
     * @return a rule builder
     */
    public Rule.Builder addRule() {
        return new Rule.Builder() {

            @Override
            void onBuild(Rule rule) {
                addRule(rule);
            }

        };
    }

    public MockInterceptor reset() {
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

        for (Rule rule : rules) {
            if (rule.isConsumed()) {
                continue;
            }

            Response response = rule.accept(request);
            if (response != null) {
                return response;

            } else if (behavior == Behavior.SEQUENTIAL) {
                StringBuilder sb = new StringBuilder("Not matched next rule: ");
                sb.append(rule);
                sb.append(", request=");
                sb.append(request);
                sb.append("\nFailed to match:");
                int i = 0;
                for (Map.Entry<Matcher, String> e : rule.getFailReason(request).entrySet()) {
                    sb.append("\n\t");
                    sb.append(++i);
                    sb.append(": ");
                    sb.append(e.getValue());
                    sb.append("; matcher=");
                    sb.append(e.getKey());
                }
                throw new AssertionError(sb.toString());
            }
        }

        // no matched rules or no more rules
        if (behavior == Behavior.RELAYED) {
            return chain.proceed(request);
        }

        StringBuilder sb = new StringBuilder("Not matched any rule: request=");
        sb.append(request);
        if (rules.isEmpty()) {
            sb.append("\nNo remaining rules!");

        } else {
            sb.append("\nRemaining rules:");
            int i = 0;
            for (Rule rule : rules) {
                sb.append("\n\t");
                sb.append(++i);
                sb.append(": ");
                sb.append(rule);
            }
        }
        throw new AssertionError(sb.toString());
    }

}
