package okhttp3.mock;

import android.content.Context;
import android.support.annotation.RawRes;

import java.io.IOException;
import java.io.InputStream;

/**
 * A helper class to provide responses from Android's sources
 */
public final class AndroidResources {

    /**
     * Loads the content from the given asset
     */
    public static InputStream asset(Context context, String name) {
        try {
            return context.getAssets().open(name);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Loads the content from the given raw resource
     */
    public static InputStream rawRes(Context context, @RawRes int resource) {
        return context.getResources().openRawResource(resource);
    }

    private AndroidResources() {
    }

}
