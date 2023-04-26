package okhttp3.mock;

import androidx.annotation.StringDef;

import static okhttp3.mock.HttpMethod.DELETE;
import static okhttp3.mock.HttpMethod.GET;
import static okhttp3.mock.HttpMethod.HEAD;
import static okhttp3.mock.HttpMethod.OPTIONS;
import static okhttp3.mock.HttpMethod.PATCH;
import static okhttp3.mock.HttpMethod.POST;
import static okhttp3.mock.HttpMethod.PUT;

@StringDef({GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH})
public @interface HttpMethod {

    String GET = "GET";

    String HEAD = "HEAD";

    String POST = "POST";

    String PUT = "PUT";

    String DELETE = "DELETE";

    String OPTIONS = "OPTIONS";

    String PATCH = "PATCH";

}
