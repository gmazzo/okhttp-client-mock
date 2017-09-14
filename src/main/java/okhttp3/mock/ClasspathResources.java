package okhttp3.mock;

import java.io.InputStream;

/**
 * A helper class to provide responses from classpath sources
 */
public final class ClasspathResources {

    /**
     * Loads the content from the given classpath resource
     */
    public static InputStream resource(String name) {
        return resource(Thread.currentThread().getContextClassLoader(), name);
    }

    /**
     * Loads the content from the given classpath resource
     */
    public static InputStream resource(ClassLoader classLoader, String name) {
        return classLoader.getResourceAsStream("/" + name);
    }

    private ClasspathResources() {
    }

}
