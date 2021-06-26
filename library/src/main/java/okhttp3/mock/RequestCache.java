package okhttp3.mock;

import okhttp3.Request;
import okio.Buffer;

import java.io.IOException;

public enum RequestCache {

    INSTANCE;

    private String requestBody;

    public void setRequest(Request req) {
        if (req.body() != null) {
            try {
                Buffer buffer = new Buffer();
                req.body().writeTo(buffer);
                if (req.body().contentType() == null) {
                    requestBody = buffer.readUtf8();
                } else {
                    requestBody = buffer.readString(req.body().contentType().charset());
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    public String requestBody() { return requestBody; }

}
