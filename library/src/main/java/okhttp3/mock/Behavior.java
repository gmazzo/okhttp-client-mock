package okhttp3.mock;

/**
 * Defines the available behavior modes for a {@link MockInterceptor}, regarding the {@link Rule}s configured on it.
 */
public enum Behavior {

    /**
     * The requests must match the same order the {@link Rule}s were configured, an {@link AssertionError} will be thrown otherwise.
     */
    SEQUENTIAL,

    /**
     * The requests must match the configured {@link Rule}s at any order, if no rule applies an {@link AssertionError} will be thrown.
     */
    UNORDERED,

    /**
     * The requests must match the configured {@link Rule}s at any order, if no rule applies a regular request will be performed instead.
     */
    RELAYED

}
