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
     *
     * @param context the android context
     * @param name    the name of the asset
     * @return the content as an {@link InputStream}
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
     *
     * @param context  the android context
     * @param resource the id of the resource
     * @return the content as an {@link InputStream}
     */
    public static InputStream rawRes(Context context, @RawRes int resource) {
        return context.getResources().openRawResource(resource);
    }

    private AndroidResources() {
    }

}
