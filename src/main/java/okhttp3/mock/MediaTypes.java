package okhttp3.mock;

import okhttp3.MediaType;

public interface MediaTypes {

    MediaType TYPE_PLAIN_TEXT = MediaType.parse("text/plain");

    MediaType TYPE_HTML = MediaType.parse("text/html");

    MediaType TYPE_XML = MediaType.parse("text/xml");

    MediaType TYPE_JSON = MediaType.parse("application/json");

    MediaType TYPE_FORM_DATA = MediaType.parse("multipart/form-data");

    MediaType TYPE_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

    MediaType TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");

}
