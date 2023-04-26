package okhttp3.mock;

import androidx.annotation.IntDef;

import static okhttp3.mock.HttpCode.HTTP_200_OK;
import static okhttp3.mock.HttpCode.HTTP_204_NO_CONTENT;
import static okhttp3.mock.HttpCode.HTTP_302_FOUND;
import static okhttp3.mock.HttpCode.HTTP_303_SEE_OTHER;
import static okhttp3.mock.HttpCode.HTTP_304_NOT_MODIFIED;
import static okhttp3.mock.HttpCode.HTTP_400_BAD_REQUEST;
import static okhttp3.mock.HttpCode.HTTP_401_UNAUTHORIZED;
import static okhttp3.mock.HttpCode.HTTP_403_FORBIDDEN;
import static okhttp3.mock.HttpCode.HTTP_404_NOT_FOUND;
import static okhttp3.mock.HttpCode.HTTP_405_METHOD_NOT_ALLOWED;
import static okhttp3.mock.HttpCode.HTTP_409_CONFLICT;
import static okhttp3.mock.HttpCode.HTTP_500_INTERNAL_SERVER_ERROR;

@IntDef({HTTP_200_OK, HTTP_204_NO_CONTENT,
        HTTP_302_FOUND, HTTP_303_SEE_OTHER, HTTP_304_NOT_MODIFIED,
        HTTP_400_BAD_REQUEST, HTTP_401_UNAUTHORIZED, HTTP_403_FORBIDDEN, HTTP_404_NOT_FOUND, HTTP_405_METHOD_NOT_ALLOWED, HTTP_409_CONFLICT,
        HTTP_500_INTERNAL_SERVER_ERROR})
public @interface HttpCode {

    int HTTP_200_OK = 200;

    int HTTP_204_NO_CONTENT = 204;

    int HTTP_302_FOUND = 302;

    int HTTP_303_SEE_OTHER = 303;

    int HTTP_304_NOT_MODIFIED = 304;

    int HTTP_400_BAD_REQUEST = 400;

    int HTTP_401_UNAUTHORIZED = 401;

    int HTTP_403_FORBIDDEN = 403;

    int HTTP_404_NOT_FOUND = 404;

    int HTTP_405_METHOD_NOT_ALLOWED = 405;

    int HTTP_409_CONFLICT = 409;

    int HTTP_500_INTERNAL_SERVER_ERROR = 500;

}
