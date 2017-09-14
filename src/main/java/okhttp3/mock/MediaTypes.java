package okhttp3.mock;

import okhttp3.MediaType;

public interface MediaTypes {

    MediaType TYPE_PLAIN_TEXT = MediaType.parse("text/plain");

    MediaType TYPE_JSON = MediaType.parse("application/json");

    MediaType TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");

}
