package okhttp3.mock;


import androidx.annotation.NonNull;
import okhttp3.MediaType;

@SuppressWarnings("ConstantConditions")
public interface MediaTypes {

    @NonNull
    MediaType MEDIATYPE_TEXT = MediaType.parse("text/plain");

    @NonNull
    MediaType MEDIATYPE_HTML = MediaType.parse("text/html");

    @NonNull
    MediaType MEDIATYPE_XML = MediaType.parse("text/xml");

    @NonNull
    MediaType MEDIATYPE_JSON = MediaType.parse("application/json");

    @NonNull
    MediaType MEDIATYPE_FORM_DATA = MediaType.parse("multipart/form-data");

    @NonNull
    MediaType MEDIATYPE_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

    @NonNull
    MediaType MEDIATYPE_RAW_DATA = MediaType.parse("application/octet-stream");

}
