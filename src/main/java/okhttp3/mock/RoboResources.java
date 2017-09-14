package okhttp3.mock;

import android.content.Context;
import android.support.annotation.RawRes;

import org.robolectric.RuntimeEnvironment;

import java.io.InputStream;

/**
 * A helper class to provide responses from Roboelectric's sources
 */
public final class RoboResources {

    /**
     * Loads the content from the given asset
     */
    public static InputStream asset(String name) {
        return AndroidResources.asset(RuntimeEnvironment.application, name);
    }

    /**
     * Loads the content from the given raw resource
     */
    public static InputStream raw(Context context, @RawRes int resource) {
        return AndroidResources.raw(RuntimeEnvironment.application, resource);
    }

    private RoboResources() {
    }

}
