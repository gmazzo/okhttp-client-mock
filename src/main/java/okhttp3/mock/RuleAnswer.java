package okhttp3.mock;

import okhttp3.Request;
import okhttp3.Response;

public interface RuleAnswer {

    Response.Builder respond(Request request);

}
